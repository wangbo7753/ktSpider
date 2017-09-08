package com.kthw.spider.main;

import com.kthw.spider.common.SpiderProperties;
import com.kthw.spider.common.db.DBLink;
import com.kthw.spider.downloader.KTDownLoader;
import com.kthw.spider.pipeline.TVDSImagePipeline;
import com.kthw.spider.pipeline.TVDSPipeline;
import com.kthw.spider.processor.TVDSPageProcessor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Spider;

import java.util.Map;

/**
 * Created by YFZX-WB on 2016/10/8.
 */
public class TVDSpider extends KTSpider {

    private static final Logger LOG = LoggerFactory.getLogger(TVDSpider.class);

    public static final String FILE_PATH = "tvds.properties";

    public TVDSpider(DBLink dbLink) {
        super(dbLink);
    }

    public void run() {
        LOG.info("TVDSpider begin");
        while (isRunning()) {
            try {
                Map<String, String> params = SpiderProperties.getAllProperties(FILE_PATH);
                process(params);
                Thread.sleep(Integer.valueOf(params.get("INTERVAL")) * 60 * 1000);
            } catch (Exception e) {
                LOG.error("TVDSpider run error, please check :" , e);
                break;
            }
        }
        LOG.info("TVDSpider end");
    }

    public void process(Map<String, String> params) {
        String baseUrl = params.get("BASE_URL");
        if (StringUtils.isNotBlank(baseUrl)) {
            Spider.create(new TVDSPageProcessor(params))
                .addUrl(baseUrl)
                    .setDownloader(new KTDownLoader(getDbLink(), "V"))
                        .addPipeline(new TVDSPipeline(getDbLink(), params))
                            .addPipeline(new TVDSImagePipeline(params))
                                .thread(1).run();
        }
    }

}
