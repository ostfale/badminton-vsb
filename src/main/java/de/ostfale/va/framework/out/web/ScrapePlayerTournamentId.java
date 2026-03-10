package de.ostfale.va.framework.out.web;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import de.ostfale.va.application.domain.model.playerrankings.PlayerTournamentId;
import de.ostfale.va.application.port.out.ranking.PageProcessor;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ScrapePlayerTournamentId implements PageProcessor<PlayerTournamentId>, UseLogging {
    private static final String PLAYER_PROFILE_PATH = "/player-profile/";
    private static final String LIST_ITEM_SELECTOR = ".list__item";
    private static final String MEDIA_TITLE_SELECTOR = "h5.media__title";
    private static final String MEDIA_LINK_SELECTOR = "a.media__link";

    private String targetPlayerId;

    public void setTargetPlayerId(String targetPlayerId) {
        this.targetPlayerId = targetPlayerId;
    }

    @Override
    public Optional<PlayerTournamentId> process(Page page) {

        try {
            page.waitForSelector(LIST_ITEM_SELECTOR,
                    new Page.WaitForSelectorOptions().setTimeout(3000));
            return findPlayerLink(page).flatMap(this::extractTournamentId);
        } catch (TimeoutError e) {
            log().warn("Scrape :: Timeout - Spieler nicht gefunden auf: {}", page.url());
            return Optional.empty();
        }
    }

    private Optional<Locator> findPlayerLink(Page page) {
        String playerIdText = "(" + targetPlayerId + ")";
        Locator playerLink = page.locator(MEDIA_TITLE_SELECTOR)
                .filter(new Locator.FilterOptions().setHasText(playerIdText))
                .locator(MEDIA_LINK_SELECTOR);

        // More explicit check
        return playerLink.count() > 0 ? Optional.of(playerLink.first()) : Optional.empty();
    }

    private Optional<PlayerTournamentId> extractTournamentId(Locator playerLink) {
        String href = playerLink.getAttribute("href");
        if (href != null && href.contains(PLAYER_PROFILE_PATH)) {
            String uuid = parseUuidFromHref(href);
            log().info("ScrapePlayerTournamentId :: extracted tournament id: {} for player id: {}", uuid, targetPlayerId);
            return Optional.of(new PlayerTournamentId(uuid));
        }
        return Optional.empty();
    }

    private String parseUuidFromHref(String href) {
        return href.substring(href.lastIndexOf("/") + 1);
    }
}

