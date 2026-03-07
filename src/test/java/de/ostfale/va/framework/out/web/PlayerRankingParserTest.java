package de.ostfale.va.framework.out.web;

import de.ostfale.va.PlayWrightTestBase;
import de.ostfale.va.application.domain.model.playerrankings.PlayerRankingResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DisplayName("Test reading valid ranking points for a player")
class PlayerRankingParserTest extends PlayWrightTestBase {

    private final PlayerRankingParser sut = new PlayerRankingParser();

    @Test
    void shouldParseLocalHtmlToDomainModel() {
        // given
        String fileUrl = getFileUrl("player/vbPoints.html");
        page.navigate(fileUrl);

        // When
        List<PlayerRankingResult> results = sut.parseValidRankingPoints(page);

        // Then
        assertThat(results).hasSize(12);
        assertThat(results.getFirst().tournamentName()).contains("1. Norddeutsches RLT");
        assertThat(results.getFirst().points()).isEqualTo(13768);
        assertThat(results.getFirst().isRelevant()).isTrue();
    }
}
