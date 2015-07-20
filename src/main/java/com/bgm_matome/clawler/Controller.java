package com.bgm_matome.clawler;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import javax.enterprise.context.Dependent;

@Dependent
public class Controller {

    public static void main(String[] args) throws Exception {
        //DOMConfigurator.configure("src/main/resources/log4j.xml");

        String crawlStorageFolder = "/data/crawl/root";
        int numberOfCrawlers = 1;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setResumableCrawling(false);
        config.setMaxDepthOfCrawling(1);
        config.setUserAgentString("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:30.0) Gecko/20100101 Firefox/30.0");
        config.setPolitenessDelay(500);
        config.setFollowRedirects(false);
        config.setMaxDownloadSize(2048576);

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        for (int i = 1; i <= 100; i++) {
            controller.addSeed("http://matome.naver.jp/topic/1LvWv?page=" + i);
        }

        controller.start(MyCrawler.class, numberOfCrawlers);
    }
}
