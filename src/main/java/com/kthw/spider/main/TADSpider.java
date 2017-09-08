package com.kthw.spider.main;

import com.kthw.spider.common.SpiderProperties;
import com.kthw.spider.common.TAUtils;
import com.kthw.spider.common.db.DBLink;
import com.kthw.spider.downloader.KTDownLoader;
import com.kthw.spider.pipeline.TADSPipeline;
import com.kthw.spider.processor.TADSPageProcessor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.Map;

/**
 * Created by YFZX-WB on 2016/9/28.
 */
public class TADSpider extends KTSpider {

    private static final Logger LOG = LoggerFactory.getLogger(TADSpider.class);

    public static final String FILE_PATH = "tads.properties";

    public TADSpider(DBLink dbLink) {
        super(dbLink);
    }

    public void run() {
        LOG.info("TADSpider begin");
        while(isRunning()) {
            try {
                Map<String, String> params = SpiderProperties.getAllProperties(FILE_PATH);
                process(params);
                Thread.sleep(Integer.parseInt(params.get("INTERVAL")) * 60 * 1000);
            } catch (Exception e) {
                LOG.error("TADSpider run error, please check :", e);
                break;
            }
        }
        LOG.info("TADSpider end");
    }

    public void process(Map<String, String> params) {
        String baseUrl = params.get("BASE_URL");
        if (StringUtils.isNotBlank(baseUrl)) {
            params.put("randomCode", TAUtils.getRandomCode());
            Request request = new Request(baseUrl.replace("randomCode", params.get("randomCode")));
            request.setMethod(HttpConstant.Method.GET);
            Spider.create(new TADSPageProcessor(params))
                .addRequest(request)
                    .setDownloader(new KTDownLoader(getDbLink(), "A"))
                        .addPipeline(new TADSPipeline(getDbLink()))
                            .thread(1).run();
        }
    }

}
