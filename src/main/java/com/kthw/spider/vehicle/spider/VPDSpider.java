package com.kthw.spider.vehicle.spider;

import com.kthw.spider.common.SpiderProperties;
import com.kthw.spider.common.db.DBLink;
import com.kthw.spider.main.KTSpider;
import com.kthw.spider.vehicle.pipeline.VPDSPipeline;
import com.kthw.spider.vehicle.processor.VPDSPageProcessor;
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
 * Created by YFZX-WB on 2016/12/22.
 */
public class VPDSpider extends KTSpider {

    private static final Logger LOG = LoggerFactory.getLogger(VPDSpider.class);

    public static final String FILE_PATH = "vpds.properties";

    public VPDSpider(DBLink dbLink) {
        super(dbLink);
    }

    public void run() {
        LOG.info("VPDSpider begin");
        while(isRunning()) {
            try {
                Map<String, String> params = SpiderProperties.getAllProperties(FILE_PATH);
                process(params);
                Thread.sleep(Integer.parseInt(params.get("INTERVAL")) * 60 * 1000);
            } catch (Exception e) {
                LOG.error("VPDSpider run error, please check : ", e);
                break;
            }
        }
        LOG.info("VPDSpider end");
    }

    public void process(Map<String, String> params) {
        String loginUrl = params.get("LOGIN_URL");
        if (StringUtils.isNotBlank(loginUrl)) {
            Request request = new Request(loginUrl);
            request.setMethod(HttpConstant.Method.POST);
            NameValuePair[] nameValuePair = buildLoginParam();
            request.putExtra("nameValuePair", nameValuePair);
            Spider.create(new VPDSPageProcessor(params))
                .addRequest(request)
                    .addPipeline(new VPDSPipeline(getDbLink()))
                        .thread(1).run();
        }
    }

    private NameValuePair[] buildLoginParam() {
        NameValuePair[] pairs = new NameValuePair[4];
        pairs[0] = new BasicNameValuePair("tb_UserName", "5t");
        pairs[1] = new BasicNameValuePair("tb_Password", "tdb");
        pairs[2] = new BasicNameValuePair("__VIEWSTATE", "/wEPDwULLTE4MjgwODA1MTEPZBYCAgMPZBYEAgsPDxYCHgRUZXh0BQnmgLvlhazlj7hkZAINDw8WAh8ABQRWMy4wZGRkjVRAt84e0jWaaUjBeQRP6A/5kW8=");
        pairs[3] = new BasicNameValuePair("btn_Login", "%E7%99%BB%E5%BD%95");
        return pairs;
    }

}
