package com.kthw.spider.main;

import com.kthw.spider.common.SpiderProperties;
import com.kthw.spider.common.TAUtils;
import com.kthw.spider.common.db.DBLink;
import com.kthw.spider.downloader.DEVDownLoader;
import com.kthw.spider.pipeline.DEVPipeline;
import com.kthw.spider.processor.DEVPageProcessor;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.Map;

public class DEVSpider extends KTSpider {

    private static final Logger LOG = LoggerFactory.getLogger(TADSpider.class);

    public static final String FILE_PATH = "dev.properties";

    public DEVSpider(DBLink dbLink) {
        super(dbLink);
    }

    public void run() {
        LOG.info("DEVSpider begin");
        while(isRunning()) {
            try {
                Map<String, String> params = SpiderProperties.getAllProperties(FILE_PATH);
                process(params);
                Thread.sleep(Integer.parseInt(params.get("INTERVAL")) * 60 * 1000);
            } catch (Exception e) {
                LOG.error("DEVSpider run error, please check :", e);
                break;
            }
        }
        LOG.info("DEVSpider end");
    }

    public void process(Map<String, String> params) {
    	String loginUrl = params.get("LOGIN_URL");
        if (StringUtils.isNotBlank(loginUrl)) {
            Request request = new Request(loginUrl);
            request.setMethod(HttpConstant.Method.POST);
            NameValuePair[] nameValuePair = buildLoginParam();
            request.putExtra("nameValuePair", nameValuePair);
            Spider.create(new DEVPageProcessor(params))
                .addRequest(request)
//                    .setDownloader(new DEVDownLoader(getDbLink(), "D"))
                        .addPipeline(new DEVPipeline(getDbLink()))
                            .thread(1).run();
        }
    }
    
    private NameValuePair[] buildLoginParam() {
        NameValuePair[] pairs = new NameValuePair[2];
        pairs[0] = new BasicNameValuePair("LoginName", "zgs");
        pairs[1] = new BasicNameValuePair("Password", "666666");
        return pairs;
    }

}
