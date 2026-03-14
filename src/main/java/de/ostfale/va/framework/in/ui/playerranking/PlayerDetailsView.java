package de.ostfale.va.framework.in.ui.playerranking;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.port.in.ranking.ForLoadingRankings;
import de.ostfale.va.application.port.out.ForGettingUserConfiguration;
import de.ostfale.va.application.port.out.ForStoringUserData;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.common.UseTimeHandling;

import java.util.List;

public class PlayerDetailsView extends VerticalLayout implements UseTimeHandling, UseLogging {

    private static final String PLAYER_SEPARATOR_SELECTION = "Spieler Suchen";
    private static final String PLAYER_SEPARATOR_DETAILS = "Spieler Details";
    private static final String PLAYER_RANKING_POINTS = "Spieler Ranglistenpunkte";


    private final PlayerDetailsSearchComponent searchComponent;
    private final PlayerDetailsMatrixComponent matrixComponent;

    private final ForLoadingRankings rankingService;

    private TextField playerNameField;
    private TextField playerIdField;
    private TextField playerGenderField;
    private TextField playerAgeClassField;
    private TextField playerYearOfBirthField;
    private TextField playerClubNameField;
    private TextField playerDistrictNameField;
    private TextField playerStateNameField;
    private TextField playerStateGroupField;

    public PlayerDetailsView(GetPlayerDetailsService playerDetailsService,
                             ForLoadingRankings rankingService,
                             ForStoringUserData forStoringUserData,
                             ForGettingUserConfiguration userConfiguration) {
        this.searchComponent = new PlayerDetailsSearchComponent(rankingService, this, userConfiguration, forStoringUserData);
        this.matrixComponent = new PlayerDetailsMatrixComponent(playerDetailsService);
        log().debug("PlayerDetailsView :: constructor");
        this.rankingService = rankingService;
        initLayout();
    }

    public void initLayout() {
        add(new FormSectionHeader(PLAYER_SEPARATOR_SELECTION));

        HorizontalLayout actionRow = searchComponent.getComponent();

        add(actionRow);
        actionRow.setWidth("50%");

        add(new FormSectionHeader(PLAYER_SEPARATOR_DETAILS));
        add(createDetailsBlock());

        add(new FormSectionHeader(PLAYER_RANKING_POINTS + " (KW " + getCurrentCalendarWeek() + ")"));
        add(matrixComponent.getComponent());
    }

    private FormLayout createDetailsBlock() {
        FormLayout detailsBlock = new FormLayout();
        detailsBlock.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("800px", 2),
                new FormLayout.ResponsiveStep("1000px", 3),
                new FormLayout.ResponsiveStep("1200px", 5)
        );

        List<TextField> detailsFields = List.of(
                playerNameField = new TextField("Name"),
                playerGenderField = new TextField("M / F"),
                playerIdField = new TextField("Spieler ID"),
                playerAgeClassField = new TextField("Altersklasse"),
                playerYearOfBirthField = new TextField("Geburtsjahr"),
                playerClubNameField = new TextField("Verein"),
                playerDistrictNameField = new TextField("Bezirk"),
                playerStateNameField = new TextField("Landesverband"),
                playerStateGroupField = new TextField("Gruppe")
        );

        detailsFields.forEach(field -> {
            field.setReadOnly(true);
            field.setWidthFull();
            field.addClassName("no-shadow-field");
            detailsBlock.add(field);
        });

        return detailsBlock;
    }

    public void updatePlayerDetails(Player player) {
        playerNameField.setValue(player.toString());
        playerGenderField.setValue(player.getGender().getDisplayName());
        playerIdField.setValue(player.getPlayerId().toString());
        playerAgeClassField.setValue(player.getAgeClassGeneral());
        playerYearOfBirthField.setValue(String.valueOf(player.getYearOfBirth()));
        playerClubNameField.setValue(player.getClubName());
        playerDistrictNameField.setValue(player.getDistrictName());
        playerStateNameField.setValue(player.getStateName());
        playerStateGroupField.setValue(player.getStateGroup().getDisplayName());

        List<Player> allPlayers = rankingService.loadPlayer();
        matrixComponent.updateRanking(player, allPlayers);
    }

    public void clearDetails() {
        playerNameField.clear();
        playerGenderField.clear();
        playerIdField.clear();
        playerAgeClassField.clear();
        playerYearOfBirthField.clear();
        playerClubNameField.clear();
        playerDistrictNameField.clear();
        playerStateNameField.clear();
        playerStateGroupField.clear();

        matrixComponent.clearDetails();
    }
}
