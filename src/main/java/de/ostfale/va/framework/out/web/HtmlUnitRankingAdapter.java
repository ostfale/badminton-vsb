package de.ostfale.va.framework.out.web;

import de.ostfale.va.application.port.out.ranking.ForRankingFileDownload;
import de.ostfale.va.common.UseLogging;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Component
public class HtmlUnitRankingAdapter implements ForRankingFileDownload, UseLogging {

    private final ObjectProvider<WebClient> clientProvider;
    private final BadmintonDeTimestampParser timestampParser;

    public HtmlUnitRankingAdapter(ObjectProvider<WebClient> clientProvider, BadmintonDeTimestampParser timestampParser) {
        this.clientProvider = clientProvider;
        this.timestampParser = timestampParser;
    }

    @Override
    public Optional<LocalDateTime> getLatestRemoteTimestamp(String url) {

        try (WebClient webClient = clientProvider.getIfAvailable()) {

            // Configure WebClient to handle JavaScript errors gracefully
            webClient.getOptions().setJavaScriptEnabled(false);
            webClient.getOptions().setCssEnabled(false);


            HtmlPage page = Objects.requireNonNull(webClient, "WebClient must not be null").getPage(URI.create(url).toURL());
            return timestampParser.parseLastUpdate(page);
        } catch (Exception e) {
            log().error("HtmlUnitRankingAdapter :: Could not fetch remote timestamp: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void downloadRankingFile(String sourceUrl, Path targetPath) {

    }
}
