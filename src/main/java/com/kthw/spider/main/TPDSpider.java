package com.kthw.spider.main;

import com.kthw.spider.common.SpiderProperties;
import com.kthw.spider.common.db.DBLink;
import com.kthw.spider.downloader.KTDownLoader;
import com.kthw.spider.pipeline.TPDSPipeline;
import com.kthw.spider.processor.TPDSPageProcessor;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.Map;

/**
 * Created by YFZX-WB on 2016/9/12.
 */
public class TPDSpider extends KTSpider {

    private static final Logger LOG = LoggerFactory.getLogger(TPDSpider.class);

    private static final String FILE_PATH = "tpds.properties";

    public TPDSpider(DBLink dbLink) {
        super(dbLink);
    }

    public void run() {
        LOG.info("TPDSpider Begin");
        while (isRunning()) {
            try {
                Map<String, String> params = SpiderProperties.getAllProperties(FILE_PATH);
                process(params);
                int interMin = Integer.parseInt(params.get("INTERVAL"));
                Thread.sleep(interMin * 60 * 1000);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        LOG.info("TPDSpider End");
    }

    public void process(Map<String, String> params) {
        String loginUrl = params.get("LOGIN_URL");
        if (StringUtils.isNotBlank(loginUrl)) {
            Request request = new Request(loginUrl);
            request.setMethod(HttpConstant.Method.POST);
            NameValuePair[] nameValuePair = buildLoginParam();
            request.putExtra("nameValuePair", nameValuePair);
            Spider.create(new TPDSPageProcessor(params))
                .addRequest(request)
                    .setDownloader(new KTDownLoader(getDbLink(), "P"))
                        .addPipeline(new TPDSPipeline(getDbLink()))
                            .thread(1).run();
        }
    }

    private NameValuePair[] buildLoginParam() {
        NameValuePair[] pairs = new NameValuePair[4];
        pairs[0] = new BasicNameValuePair("__VIEWSTATE", "/wEPDwUKMTk4NjU3NTMxOQ9kFgICAw9kFgQCBQ8PFgIeBFRleHQFCeaAu+WFrOWPuGRkAgcPDxYCHwAFBFYzLjFkZGSoL2f52Gd0lSL/j9irUIoS+oYEuA==");
        pairs[1] = new BasicNameValuePair("tb_UserName", "5t");
        pairs[2] = new BasicNameValuePair("tb_Password", "tdb");
        pairs[3] = new BasicNameValuePair("btn_Login", "%E7%99%BB%E5%BD%95");
        return pairs;
    }

}
