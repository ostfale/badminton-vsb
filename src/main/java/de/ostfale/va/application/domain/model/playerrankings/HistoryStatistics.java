package de.ostfale.va.application.domain.model.playerrankings;

public class HistoryStatistics {

    private RankingSnapshot singleStatistics;
    private RankingSnapshot doubleStatistics;
    private RankingSnapshot mixedStatistics;

    public void updateStatistics(DisciplineType disciplineType, RankingSnapshot rankingSnapshot) {
        switch (disciplineType) {
            case SINGLE -> this.singleStatistics = rankingSnapshot;
            case DOUBLE -> this.doubleStatistics = rankingSnapshot;
            case MIXED -> this.mixedStatistics = rankingSnapshot;
        }}

    public RankingSnapshot getSingleStatistics() {
        return singleStatistics;
    }

    public void setSingleStatistics(RankingSnapshot singleStatistics) {
        this.singleStatistics = singleStatistics;
    }

    public RankingSnapshot getDoubleStatistics() {
        return doubleStatistics;
    }

    public void setDoubleStatistics(RankingSnapshot doubleStatistics) {
        this.doubleStatistics = doubleStatistics;
    }

    public RankingSnapshot getMixedStatistics() {
        return mixedStatistics;
    }

    public void setMixedStatistics(RankingSnapshot mixedStatistics) {
        this.mixedStatistics = mixedStatistics;
    }
}
