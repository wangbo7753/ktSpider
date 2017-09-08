package com.kthw.spider.vehicle.processor;

import com.kthw.spider.common.SpiderProperties;
import com.kthw.spider.vehicle.model.VEDSBean;
import com.kthw.spider.vehicle.model.VEJsonObj;
import com.kthw.spider.vehicle.spider.VEDSpider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Json;

import java.util.List;
import java.util.Map;

/**
 * Created by YFZX-WB on 2016/12/16.
 */
public class VEDSPageProcessor implements PageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(VEDSPageProcessor.class);

    private Map<String, String> params;

    private Site site;

    public VEDSPageProcessor(Map<String, String> params) {
        this.site = Site.me().setRetryTimes(3).setTimeOut(30000).setCharset("utf-8")
                .addCookie("10.1.184.32", "ASP.NET_SessionId", params.get("COOKIE"));
        this.params = params;
    }

    public void process(Page page) {
        List<VEDSBean> parseData = parseInsertData(page.getJson());
        page.putField("VEDataList", parseData);
    }

    public List<VEDSBean> parseInsertData(Json json) {
        VEJsonObj obj = json.toObject(VEJsonObj.class);
        if (obj.getRows() != null && obj.getRows().size() > 0) {
            SpiderProperties.writeProperties(VEDSpider.FILE_PATH, "LAST_END_TIME", params.get("LAST_END_TIME"));
        } else {
            LOG.info("===============TEDS train pass no data============");
        }
        return obj.getRows();
    }

    public Site getSite() {
        return site;
    }
}
