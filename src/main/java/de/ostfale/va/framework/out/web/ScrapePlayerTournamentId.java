package de.ostfale.va.framework.out.web;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import de.ostfale.va.application.domain.model.playerrankings.PlayerTournamentId;
import de.ostfale.va.application.port.out.ranking.PageProcessor;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ScrapePlayerTournamentId implements PageProcessor<PlayerTournamentId>, UseLogging {
    private static final String PLAYER_PROFILE_PATH = "/player-profile/";
    private static final String COOKIEWALL_PATH = "cookiewall";
    private static final String LIST_ITEM_SELECTOR = ".list__item";
    private static final String MEDIA_TITLE_SELECTOR = "h5.media__title";
    private static final String MEDIA_LINK_SELECTOR = "a.media__link";
    private static final int COOKIEWALL_WAIT_TIMEOUT_MS = 2000;
    private static final int SELECTOR_WAIT_TIMEOUT_MS = 7000;

    private String targetPlayerId;

    public void setTargetPlayerId(String targetPlayerId) {
        this.targetPlayerId = targetPlayerId;
    }

    @Override
    public Optional<PlayerTournamentId> process(Page page) {
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        if (page.url().contains(COOKIEWALL_PATH)) {
            page.waitForTimeout(COOKIEWALL_WAIT_TIMEOUT_MS);
            log().error("Scrape :: Abbruch - Immer noch auf Cookiewall: {}", page.url());
            return Optional.empty();
        }

        try {
            page.waitForSelector(LIST_ITEM_SELECTOR, new Page.WaitForSelectorOptions().setTimeout(SELECTOR_WAIT_TIMEOUT_MS));
            return findPlayerLink(page).flatMap(this::extractTournamentId);
        } catch (Exception e) {
            log().warn("Scrape :: Timeout auf URL: {}", page.url());
            return Optional.empty();
        }
    }

    private Optional<Locator> findPlayerLink(Page page) {
        String playerIdText = "(" + targetPlayerId + ")";
        Locator playerLink = page.locator(MEDIA_TITLE_SELECTOR)
                .filter(new Locator.FilterOptions().setHasText(playerIdText))
                .locator(MEDIA_LINK_SELECTOR);
        return playerLink.count() > 0 ? Optional.of(playerLink) : Optional.empty();
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

