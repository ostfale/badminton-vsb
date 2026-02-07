package de.ostfale.va.application.domain.model.plannedournaments;

import de.ostfale.va.application.domain.model.plannedournaments.vo.PlannedTournamentCategoriesVO;
import de.ostfale.va.application.domain.model.plannedournaments.vo.TournamentAgeClassesVO;
import de.ostfale.va.common.UseLogging;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class PlannedTournamentsFilter implements UseLogging {

    private final String location;
    private final String name;
    private final boolean validTournamentsOnly;
    private final boolean onlyThisYearsTournaments;
    private final boolean showOnlyFavorites;
    private final Set<TournamentAgeClassesVO> ageClasses;
    private final Set<PlannedTournamentCategoriesVO> tourCategories;

    public PlannedTournamentsFilter(String location,
                                    String name,
                                    boolean validTournamentsOnly,
                                    boolean onlyThisYearsTournaments,
                                    boolean showOnlyFavorites,
                                    Set<TournamentAgeClassesVO> ageClasses,
                                    Set<PlannedTournamentCategoriesVO> tourCategories
    ) {
        log().debug("TournamentsFilter :: constructor");
        this.location = location;
        this.name = name;
        this.validTournamentsOnly = validTournamentsOnly;
        this.onlyThisYearsTournaments = onlyThisYearsTournaments;
        this.showOnlyFavorites = showOnlyFavorites;
        this.ageClasses = (ageClasses != null) ? ageClasses : Collections.emptySet();
        this.tourCategories = (tourCategories != null) ? tourCategories : Collections.emptySet();
    }

    public static Builder builder() {
        return new Builder();
    }

    public Optional<String> location() {
        return Optional.ofNullable(location);
    }

    public Optional<String> name() {
        return Optional.ofNullable(name);
    }

    public boolean isValidTournamentsOnly() {
        return validTournamentsOnly;
    }

    public boolean onlyThisYearsTournaments() {
        return onlyThisYearsTournaments;
    }

    public boolean showOnlyFavorites() {
        return showOnlyFavorites;
    }

    public Set<TournamentAgeClassesVO> ageClasses() {
        return ageClasses;
    }

    public Set<PlannedTournamentCategoriesVO> tourCategories() {
        return tourCategories;
    }

    @Override
    public String toString() {
        return String.format("""
                        TournamentsFilter:
                          valid tournaments: %b
                          only this year:    %b
                          favorites:         %b
                          name:              %s
                          location:          %s""",
                validTournamentsOnly, onlyThisYearsTournaments,showOnlyFavorites, name, location);
    }

    public static final class Builder {

        private String location;
        private String name;

        private boolean validTournamentsOnly;
        private boolean onlyThisYearsTournaments;
        private boolean showOnlyFavorites;

        private Set<TournamentAgeClassesVO> ageClasses;
        private Set<PlannedTournamentCategoriesVO> tourCategories;

        public Builder withLocation(String location) {
            this.location = location;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withValidTournamentsOnly(boolean validTournamentsOnly) {
            this.validTournamentsOnly = validTournamentsOnly;
            return this;
        }

        public Builder withOnlyThisYearsTournaments(boolean onlyThisYearsTournaments) {
            this.onlyThisYearsTournaments = onlyThisYearsTournaments;
            return this;
        }

        public Builder withShowOnlyFavorites(boolean showOnlyFavorites) {
            this.showOnlyFavorites = showOnlyFavorites;
            return this;
        }

        public Builder withAgeClasses(Set<TournamentAgeClassesVO> ageClasses) {
            this.ageClasses = ageClasses;
            return this;
        }

        public Builder withTourCategories(Set<PlannedTournamentCategoriesVO> tourCategories) {
            this.tourCategories = tourCategories;
            return this;
        }

        public PlannedTournamentsFilter build() {
            return new PlannedTournamentsFilter(location, name, validTournamentsOnly, onlyThisYearsTournaments,showOnlyFavorites, ageClasses, tourCategories);
        }
    }
}
