package com.kthw.spider.vehicle.spider;

import com.kthw.spider.common.SpiderProperties;
import com.kthw.spider.common.db.DBLink;
import com.kthw.spider.main.KTSpider;
import com.kthw.spider.vehicle.downloader.VADSDownloader;
import com.kthw.spider.vehicle.pipeline.VADSPipeline;
import com.kthw.spider.vehicle.processor.VADSPageProcessor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.Map;

/**
 * Created by YFZX-WB on 2016/12/20.
 */
public class VADSpider extends KTSpider {

    private static final Logger LOG = LoggerFactory.getLogger(VADSpider.class);

    public static final String FILE_PATH = "vads.properties";

    public VADSpider(DBLink dbLink) {
        super(dbLink);
    }

    public void run() {
        LOG.info("VADSpider begin");
        while(isRunning()) {
            try {
                Map<String, String> params = SpiderProperties.getAllProperties(FILE_PATH);
                process(params);
                Thread.sleep(Integer.parseInt(params.get("INTERVAL")) * 60 * 1000);
            } catch (Exception e) {
                LOG.error("VADSpider run error, please check : ", e);
                break;
            }
        }
        LOG.info("VADSpider end");
    }

    public void process(Map<String, String> params) {
        String baseUrl = params.get("BASE_URL");
        if (StringUtils.isNotBlank(baseUrl)) {
            Request request = new Request("");
            request.setMethod(HttpConstant.Method.POST);
            Spider.create(new VADSPageProcessor(params))
                .addRequest(request)
                    .setDownloader(new VADSDownloader(params))
                        .addPipeline(new VADSPipeline(getDbLink()))
                            .thread(1).run();
        }
    }

}
