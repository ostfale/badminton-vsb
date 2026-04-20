package de.ostfale.va.framework.in.ui.playerinfo.signal;

import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.common.UseLogging;

@UIScope
@SpringComponent
public class PlayerSelectionState implements UseLogging {

    private final ValueSignal<Player> selectedPlayer = new ValueSignal<>(null);

    public Signal<Player> getSelectedPlayer() {
        return selectedPlayer;
    }

    public void setPlayer(Player player) {
        selectedPlayer.set(player);
    }
}
