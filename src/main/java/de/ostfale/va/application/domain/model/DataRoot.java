package de.ostfale.va.application.domain.model;

import de.ostfale.va.application.domain.model.plannedournaments.vo.UserIdendityVO;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataRoot {
    private final Map<UserIdendityVO, UserData> users = new ConcurrentHashMap<>();

    public Map<UserIdendityVO, UserData> getUsersMap() {
        return users;
    }
}
