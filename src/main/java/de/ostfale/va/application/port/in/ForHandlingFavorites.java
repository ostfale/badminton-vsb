package de.ostfale.va.application.port.in;

import de.ostfale.va.application.domain.model.plannedournaments.vo.UserIdendityVO;

import java.util.Set;

public interface ForHandlingFavorites<K> {

    Set<K> getFavorites(UserIdendityVO identity);
}
