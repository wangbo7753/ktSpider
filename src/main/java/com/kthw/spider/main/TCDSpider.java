package com.kthw.spider.main;

import com.kthw.spider.common.SpiderProperties;
import com.kthw.spider.common.db.DBLink;
import com.kthw.spider.pipeline.TCDSPipeline;
import com.kthw.spider.processor.TCDSPageProcessor;
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
 * Created by YFZX-WB on 2017/4/10.
 */
public class TCDSpider extends KTSpider {

    private static final Logger LOG = LoggerFactory.getLogger(TCDSpider.class);

    private static final String FILE_PATH = "tcds.properties";

    public TCDSpider(DBLink dbLink) {
        super(dbLink);
    }

    public void run() {
        LOG.info("TCDSpider Begin");
        while (isRunning()) {
            try {
                Map<String, String> params = SpiderProperties.getAllProperties(FILE_PATH);
                process(params);
                Thread.sleep(Integer.parseInt(params.get("INTERVAL")) * 60 * 1000);
            } catch (Exception e) {
                LOG.error("TCDSpider error: " + e);
                break;
            }
        }
        LOG.info("TCDSpider End");
    }

    public void process(Map<String, String> params) {
        String loginUrl = params.get("LOGIN_URL");
        if (StringUtils.isNotBlank(loginUrl)) {
            Request request = new Request(loginUrl);
            request.setMethod(HttpConstant.Method.POST);
            NameValuePair[] nameValuePair = buildLoginParam();
            request.putExtra("nameValuePair", nameValuePair);
            Spider.create(new TCDSPageProcessor(params))
                .addRequest(request)
                    .addPipeline(new TCDSPipeline(getDbLink()))
                        .thread(1).run();
        }
    }

    private NameValuePair[] buildLoginParam() {
        NameValuePair[] pairs = new NameValuePair[7];
        pairs[0] = new BasicNameValuePair("__VIEWSTATE", "/wEPDwULLTE1Mzk3MDM3OTQPZBYCAgEPZBYGAgMPDxYCHgdWaXNpYmxlaGRkAgkPFgIfAGgWAmYPZBYCAgMPD2QWAh4Hb25jbGljawU6U2hvd0NoYW5nZVBhc3NXb3JkRm9ybSgpO3dpbmRvdy5ldmVudC5yZXR1cm5WYWx1ZSA9IGZhbHNlO2QCCw8PZBYCHwEFC0NoZWNrRm9ybSgpZBgBBR5fX0NvbnRyb2xzUmVxdWlyZVBvc3RCYWNrS2V5X18WAQUJYnRDb25maXJtqJlZ/f4ygTaY6e83S3Nsk+mVlIDXQsRo9QyRW3gyJVw=");
        pairs[1] = new BasicNameValuePair("__EVENTVALIDATION", "/wEWBQL0hYiHCgLmusTQBAKl1bKzCQK1qbSWCwKt58etAdWA9STxFqrRdEcksRU9PC+heaMGgOblzynYkoDz1kfH");
        pairs[2] = new BasicNameValuePair("hfPopup", "0");
        pairs[3] = new BasicNameValuePair("txtUserName", "5t");
        pairs[4] = new BasicNameValuePair("txtPassWord", "sh");
        pairs[5] = new BasicNameValuePair("btConfirm.x", "48");
        pairs[6] = new BasicNameValuePair("btConfirm.y", "12");
        return pairs;
    }
}
