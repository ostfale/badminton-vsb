package de.ostfale.va.framework.in.ui.playerinfo.general;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.ostfale.va.application.domain.model.playerrankings.HistoryChange;
import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.framework.in.ui.playerinfo.signal.PlayerSelectionState;
import de.ostfale.va.framework.in.ui.playerranking.FormSectionHeader;

import java.util.List;

@UIScope
@SpringComponent
public class PlayerDataTabComponent extends VerticalLayout implements UseLogging {
    private static final String PLAYER_DETAILS_SECTION_TITLE = "Informationen zum Spieler";
    private static final String PLAYER_CHANGES_SECTION_TITLE = "Veränderungen";
    private static final String NO_SHADOW_FIELD_CLASS = "no-shadow-field";

    private final TextField playerNameField = createReadOnlyField("Name");
    private final TextField playerLastUpdate = createReadOnlyField("Letztes Update");
    private final TextField playerGenderField = createReadOnlyField("M / F");
    private final TextField playerIdField = createReadOnlyField("Spieler ID");
    private final TextField playerAgeClassField = createReadOnlyField("Altersklasse");
    private final TextField playerYearOfBirthField = createReadOnlyField("Geburtsjahr");
    private final TextField playerClubNameField = createReadOnlyField("Verein");
    private final TextField playerDistrictNameField = createReadOnlyField("Bezirk");
    private final TextField playerStateNameField = createReadOnlyField("Landesverband");
    private final TextField playerStateGroupField = createReadOnlyField("Gruppe");

    private final List<TextField> detailFields = List.of(
            playerNameField,
            playerGenderField,
            playerIdField,
            playerAgeClassField,
            playerYearOfBirthField,
            playerLastUpdate,
            playerClubNameField,
            playerDistrictNameField,
            playerStateNameField,
            playerStateGroupField
    );

    private final VerticalLayout changesLayout = new VerticalLayout();

    public PlayerDataTabComponent(PlayerSelectionState selectionState) {
        initLayout();
        Signal.effect(this, () -> refreshContent(selectionState.getSelectedPlayer().get()));
    }

    private void initLayout() {
        add(new FormSectionHeader(PLAYER_DETAILS_SECTION_TITLE));
        add(createDetailsForm());
        add(new FormSectionHeader(PLAYER_CHANGES_SECTION_TITLE));
        add(createChangesForm());
    }

    private VerticalLayout createChangesForm() {
        changesLayout.setPadding(false);
        changesLayout.setSpacing(false);
        return changesLayout;
    }

    private FormLayout createDetailsForm() {
        FormLayout form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("800px", 2),
                new FormLayout.ResponsiveStep("1000px", 3),
                new FormLayout.ResponsiveStep("1200px", 5)
        );
        detailFields.forEach(form::add);
        return form;
    }

    private TextField createReadOnlyField(String label) {
        TextField field = new TextField(label);
        field.setReadOnly(true);
        field.setWidthFull();
        field.addClassName(NO_SHADOW_FIELD_CLASS);
        return field;
    }

    private void refreshContent(Player player) {
        if (player == null) {
            showNoPlayerSelected();
            return;
        }
        showPlayerDetails(player);
        showPlayerChanges(player);
    }

    private void showPlayerChanges(Player player) {
        changesLayout.removeAll();
        List<HistoryChange> historyChanges = player.getHistoryChanges();
        if (historyChanges == null ||historyChanges.isEmpty()) {
            TextField noChangesField = createReadOnlyField(null);
            noChangesField.setValue("Keine Veränderungen vorhanden");
            changesLayout.add(noChangesField);
        } else {
            historyChanges.forEach(change -> {
                TextField changeField = createReadOnlyField(null);
                changeField.setValue(change.toString());
                changesLayout.add(changeField);
            });
        }
    }

    private void showPlayerDetails(Player player) {
        log().debug("PlayerDataTabComponent :: showPlayerDetails for player: {}", player);
        setFieldValue(playerNameField, player);
        setFieldValue(playerGenderField, player.getGender() != null ? player.getGender().getDisplayName() : null);
        setFieldValue(playerIdField, player.getPlayerId());
        setFieldValue(playerAgeClassField, player.getAgeClassGeneral());
        setFieldValue(playerYearOfBirthField, player.getYearOfBirth());
        setFieldValue(playerLastUpdate, player.getLastUpdated() != null ? player.getLastUpdated().toString() : null);
        setFieldValue(playerClubNameField, player.getClubName());
        setFieldValue(playerDistrictNameField, player.getDistrictName());
        setFieldValue(playerStateNameField, player.getStateName());
        setFieldValue(playerStateGroupField, player.getStateGroup() != null ? player.getStateGroup().getDisplayName() : null);
    }

    private void setFieldValue(TextField field, Object value) {
        field.setValue(value == null ? "" : value.toString());
    }

    private void showNoPlayerSelected() {
        log().debug("PlayerDataTabComponent :: showNoPlayerSelected");
        clearDetails();
        changesLayout.removeAll();
    }

    public void clearDetails() {
        detailFields.forEach(TextField::clear);
    }
}
