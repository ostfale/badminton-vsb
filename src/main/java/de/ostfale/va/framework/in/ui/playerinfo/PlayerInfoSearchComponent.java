package de.ostfale.va.framework.in.ui.playerinfo;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.ostfale.va.application.domain.model.UserData;
import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.domain.model.playerrankings.PlayerId;
import de.ostfale.va.application.port.in.ranking.ForLoadingRankings;
import de.ostfale.va.application.port.out.ForGettingUserConfiguration;
import de.ostfale.va.application.port.out.ForStoringUserData;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.framework.in.ui.playerinfo.signal.PlayerSelectionState;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@SpringComponent
@UIScope
public class PlayerInfoSearchComponent implements UseLogging {

    private static final String LOG_PREFIX = "PlayerInfoSearchComponent :: ";
    private static final String SEARCH_PLACEHOLDER = "Spieler suchen...";
    private static final String SEARCH_HELPER_TEXT = "Mindestens 3 Buchstaben eingeben";
    private static final String FAVORITES_PLACEHOLDER = "Favoriten";
    private static final String LAYOUT_WIDTH = "50%";
    private static final int MIN_FILTER_LENGTH = 3;

    private final ForLoadingRankings rankingService;
    private final PlayerSelectionState playerSelectionState;
    private final ForGettingUserConfiguration userConfiguration;
    private final ForStoringUserData forStoringUserData;

    private ComboBox<Player> searchBox;
    private Select<Player> favoritesSelect;
    private Button favoriteButton;

    public PlayerInfoSearchComponent(
            ForLoadingRankings rankingService,
            PlayerSelectionState playerSelectionState,
            ForGettingUserConfiguration userConfiguration,
            ForStoringUserData forStoringUserData
    ) {
        this.rankingService = rankingService;
        this.playerSelectionState = playerSelectionState;
        this.userConfiguration = userConfiguration;
        this.forStoringUserData = forStoringUserData;
    }

    public HorizontalLayout getComponent() {
        log().info(LOG_PREFIX + "Created");
        return createLayout();
    }

    private HorizontalLayout createLayout() {
        favoritesSelect = createFavoritesSelect();
        favoriteButton = createFavoriteButton();
        searchBox = createPlayerSearchBox();
        Button reloadButton = createReloadButton();

        HorizontalLayout layout = new HorizontalLayout(reloadButton, favoriteButton, searchBox, favoritesSelect);
        layout.setFlexGrow(1.0, searchBox, favoritesSelect);
        layout.setFlexGrow(0, reloadButton, favoriteButton);
        layout.setVerticalComponentAlignment(FlexComponent.Alignment.BASELINE, reloadButton, favoriteButton, searchBox, favoritesSelect);
        layout.setSpacing(true);
        layout.setWidth(LAYOUT_WIDTH);
        layout.setPadding(false);

        refreshFavorites();
        return layout;
    }

    private ComboBox<Player> createPlayerSearchBox() {
        ComboBox<Player> cbPlayer = new ComboBox<>();
        cbPlayer.setPlaceholder(SEARCH_PLACEHOLDER);
        cbPlayer.setHelperText(SEARCH_HELPER_TEXT);
        cbPlayer.setClearButtonVisible(true);
        cbPlayer.setPrefixComponent(VaadinIcon.SEARCH.create());

        cbPlayer.setItems(
                query -> {
                    String filter = query.getFilter().orElse("").trim();
                    if (!isSearchFilterValid(filter)) {
                        return Stream.empty();
                    }
                    return rankingService.findPlayers(filter, query.getOffset(), query.getLimit()).stream();
                },
                query -> {
                    String filter = query.getFilter().orElse("").trim();
                    return isSearchFilterValid(filter) ? rankingService.countPlayers(filter) : 0;
                }
        );

        cbPlayer.setItemLabelGenerator(this::formatPlayerLabel);
        attachSearchBoxListener(cbPlayer);
        return cbPlayer;
    }

    private void attachSearchBoxListener(ComboBox<Player> cbPlayer) {
        cbPlayer.addValueChangeListener(event -> {
            Player selectedPlayer = event.getValue();
            if (selectedPlayer != null) {
                favoritesSelect.clear();
            }
            playerSelectionState.setPlayer(selectedPlayer);
        });
    }

    private Select<Player> createFavoritesSelect() {
        Select<Player> select = new Select<>();
        select.setPlaceholder(FAVORITES_PLACEHOLDER);
        select.setItemLabelGenerator(player -> player == null ? "" : formatPlayerLabel(player));
        select.setEmptySelectionAllowed(true);
        attachFavoritesSelectionListener(select);
        return select;
    }

    private void attachFavoritesSelectionListener(Select<Player> selectPlayer) {
        selectPlayer.addValueChangeListener(event -> {
            Player selectedPlayer = event.getValue();
            if (selectedPlayer != null) {
                searchBox.clear();
                updateFavoriteButtonIcon(selectedPlayer);
            } else {
                favoriteButton.setIcon(VaadinIcon.STAR_O.create());
            }
            playerSelectionState.setPlayer(selectedPlayer);
        });
    }

    private Button createFavoriteButton() {
        Button btn = createIconButton(VaadinIcon.STAR_O, "Als Favorit markieren");
        btn.addClickListener(e -> {
            Player current = searchBox.getValue();
            if (current != null) {
                toggleFavorite(current);
            }
        });
        return btn;
    }

    private Button createReloadButton() {
        return createIconButton(VaadinIcon.REFRESH, "Spielerdaten aktualisieren");
    }

    private Button createIconButton(VaadinIcon icon, String tooltip) {
        Button button = new Button(icon.create());
        button.setTooltipText(tooltip);
        return button;
    }

    private void toggleFavorite(Player player) {
        PlayerId playerId = player.getPlayerId();
        Set<PlayerId> favoritePlayerIds = getFavoritePlayerIds();
        boolean isFavorite = favoritePlayerIds.contains(playerId);

        if (isFavorite) {
            forStoringUserData.removePlayerFavorite(playerId);
            favoriteButton.setIcon(VaadinIcon.STAR_O.create());
        } else {
            forStoringUserData.addPlayerFavorite(playerId);
            favoriteButton.setIcon(VaadinIcon.STAR.create());
        }

        refreshFavorites();
    }

    private void refreshFavorites() {
        Set<PlayerId> favoritePlayerIds = getFavoritePlayerIds();
        if (favoritePlayerIds.isEmpty()) {
            favoritesSelect.setItems(Collections.emptyList());
            return;
        }

        List<Player> favoritePlayers = rankingService.getAllPlayers().stream()
                .filter(player -> favoritePlayerIds.contains(player.getPlayerId()))
                .toList();

        favoritesSelect.setItems(favoritePlayers);
    }

    private void updateFavoriteButtonIcon(Player player) {
        Set<PlayerId> favoritePlayerIds = getFavoritePlayerIds();
        boolean selectedIsFavorite = favoritePlayerIds.contains(player.getPlayerId());
        favoriteButton.setIcon((selectedIsFavorite ? VaadinIcon.STAR : VaadinIcon.STAR_O).create());
    }

    private Set<PlayerId> getFavoritePlayerIds() {
        UserData user = userConfiguration.getCurrentUser();
        return user != null ? user.getFavoritePlayerIds() : Set.of();
    }

    private boolean isSearchFilterValid(String filter) {
        return filter.length() >= MIN_FILTER_LENGTH;
    }

    private String formatPlayerLabel(Player player) {
        return player.getFirstName() + " " + player.getLastName();
    }
}
