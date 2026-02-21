package de.ostfale.va.framework.in.ui.playerranking;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.port.in.ranking.ForLoadingRankings;
import de.ostfale.va.common.UseLogging;

import java.util.stream.Stream;

public class PlayerDetailsSearchComponent implements UseLogging {

    private final ForLoadingRankings rankingService;
    private final PlayerDetailsView parentView;

    // UI-components as fields for access
    private ComboBox<Player> searchBox;
    private Select<Player> favoritesSelect;
    private Button favoriteButton;

    public PlayerDetailsSearchComponent(ForLoadingRankings rankingUseCase, PlayerDetailsView playerDetailsView) {
        this.rankingService = rankingUseCase;
        this.parentView = playerDetailsView;
    }

    public HorizontalLayout getComponent() {
        log().info("PlayerDetailsSearchComponent :: Created");
        return initLayout();
    }

    private HorizontalLayout initLayout() {
        log().debug("PlayerDetailsSearchComponent :: initLayout");
        favoritesSelect = createFavoritesSelect();
        favoriteButton = createFavoriteButton();
        searchBox = createSearchComboBox();
        Button reloadButton = createReloadButton();

        refreshFavorites();       // initial load of favorites

        HorizontalLayout layout = new HorizontalLayout(reloadButton, favoriteButton, searchBox, favoritesSelect);
        layout.setFlexGrow(1.0, searchBox, favoritesSelect);
        layout.setFlexGrow(0, reloadButton, favoriteButton);
        layout.setVerticalComponentAlignment(FlexComponent.Alignment.BASELINE, reloadButton, favoriteButton, searchBox, favoritesSelect);
        layout.setSpacing(true);
        layout.setWidthFull();
        layout.setPadding(false);
        return layout;
    }

    private void refreshFavorites() {
    }

    private Select<Player> createFavoritesSelect() {
        Select<Player> select = new Select<>();
        select.setPlaceholder("Favoriten");
        select.setItemLabelGenerator(p -> p != null ? p.getFirstName() + " " + p.getLastName() : "");
        select.setEmptySelectionAllowed(true);
        addChangeListener(select);
        return select;
    }

    private ComboBox<Player> createSearchComboBox() {
        ComboBox<Player> cbPlayer = new ComboBox<>();
        cbPlayer.setPlaceholder("Spieler suchen...");
        cbPlayer.setHelperText("Mindestens 3 Buchstaben eingeben");
        cbPlayer.setClearButtonVisible(true);
        cbPlayer.setPrefixComponent(VaadinIcon.SEARCH.create());

        // define data provider
        cbPlayer.setItems(
                query -> {
                    String filter = query.getFilter().orElse("").trim();
                    // needed to be called -> contract
                    int offset = query.getOffset();
                    int limit = query.getLimit();

                    if (filter.length() < 3) return Stream.empty();

                    return rankingService.findPlayers(filter, offset, limit).stream();
                },
                query -> {
                    String filter = query.getFilter().orElse("").trim();
                    if (filter.length() < 3) return 0;

                    // needed to know the total number of items
                    return rankingService.countPlayers(filter);
                }
        );

        cbPlayer.setItemLabelGenerator(p -> p.getFirstName() + " " + p.getLastName());
        addChangeListener(cbPlayer);
        return cbPlayer;
    }

    // listener for search combobox
    private void addChangeListener(ComboBox<Player> cbPlayer) {
        cbPlayer.addValueChangeListener(event -> {
            Player selectedPlayer = event.getValue();
            if (selectedPlayer != null) {
                log().info("PlayerDetailsSearchComponent :: Player selected: {} {}", selectedPlayer.getFirstName(), selectedPlayer.getLastName());
                parentView.updatePlayerDetails(selectedPlayer);
                updateFavoriteButtonIcon(selectedPlayer);
            } else {
                parentView.clearDetails();
                favoriteButton.setIcon(VaadinIcon.STAR_O.create());
            }
        });
    }

    // listener for favorites select
    private void addChangeListener(Select<Player> selectPlayer) {
        selectPlayer.addValueChangeListener(event -> {
            Player selectedPlayer = event.getValue();
            if (selectedPlayer != null) {
                parentView.updatePlayerDetails(selectedPlayer);
            } else {
                parentView.clearDetails();
            }
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

    private void toggleFavorite(Player player) {
        // Logik für EclipseStore
        // Hier rufen Sie Ihren Service auf: rankingService.toggleFavorite(player.getPlayerId());
        log().info("PlayerDetailsSearchComponent :: Toggle Favorite für Player-ID: {}", player.getPlayerId());

        // UI Refresh
        refreshFavorites();
        updateFavoriteButtonIcon(player);
    }

    private void updateFavoriteButtonIcon(Player player) {
        // Prüfen ob Favorit (via Service/EclipseStore)
        // boolean isFav = rankingService.isFavorite(player.getPlayerId());
        // favoriteButton.setIcon(isFav ? VaadinIcon.STAR.create() : VaadinIcon.STAR_O.create());
        // favoriteButton.getStyle().set("color", isFav ? "gold" : "");
    }

    private Button createReloadButton() {
        return createIconButton(VaadinIcon.REFRESH, "Spielerdaten aktualisieren");
    }

    private Button createIconButton(VaadinIcon icon, String tooltip) {
        Button button = new Button();
        button.setIcon(icon.create());
        button.setTooltipText(tooltip);
        return button;
    }
}
