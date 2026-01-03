package de.ostfale.va.application.domain.model.plannedournaments;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum TournamentAgeClassesVO {
    U9("u09"), U11("u11"), U13("u13"), U15("u15"), U17("u17"), U19("u19"),
    U22("u22"), O19("o19"), O35("o35"), UOX("uox");

    private static final Map<String, TournamentAgeClassesVO> LOOKUP_MAP = Arrays.stream(values())
            .flatMap(val -> Stream.of(val.name().toLowerCase(), val.alias.toLowerCase()))
            .distinct()
            .collect(Collectors.toUnmodifiableMap(s -> s, TournamentAgeClassesVO::findByNameOrAlias));

    private final String alias;

    TournamentAgeClassesVO(String alias) {
        this.alias = alias;
    }

    private static TournamentAgeClassesVO findByNameOrAlias(String key) {
        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(key) || v.alias.equalsIgnoreCase(key))
                .findFirst()
                .orElse(UOX);
    }

    public static TournamentAgeClassesVO[] getFilterValues() {
        return new TournamentAgeClassesVO[]{U9, U11, U13, U15, U17, U19, U22, O19, O35};
    }

    public static TournamentAgeClassesVO[] getPointFilterValues() {
        return new TournamentAgeClassesVO[]{U9, U11, U13, U15, U17, U19};
    }

    public static TournamentAgeClassesVO fromString(String ageClass) {
        return Optional.ofNullable(ageClass)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> s.length() > 3 ? s.substring(0, 3) : s)
                .map(String::toLowerCase)
                .map(key -> {
                    if (!key.startsWith("u") && !key.startsWith("o")) return UOX;
                    TournamentAgeClassesVO result = LOOKUP_MAP.get(key);
                    if (result == null) {
                        throw new TournamentAgeClassNotFoundException("Unknown age class found: " + ageClass);
                    }
                    return result;
                })
                .orElseThrow(() -> new IllegalArgumentException("Age class cannot be null or empty."));
    }
}
