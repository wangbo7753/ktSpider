package com.kthw.spider.processor;

import com.kthw.spider.common.SpiderProperties;
import com.kthw.spider.main.TVDSpider;
import com.kthw.spider.model.TVDSBean;
import com.kthw.spider.model.TVDSJsonObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Json;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by YFZX-WB on 2016/10/8.
 */
public class TVDSPageProcessor implements PageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(TVDSPageProcessor.class);

    private Map<String, String> params;

    private DateFormat df;

    private Site site;

    public TVDSPageProcessor(Map<String, String> params) {
        site = Site.me().setRetryTimes(3).setTimeOut(30000).setCharset("utf-8")
            .addCookie("10.1.184.32", "ASP.NET_SessionId", params.get("COOKIE"));
        this.params = params;
        df = new SimpleDateFormat("yyyyMMddHHmmss");
    }

    public void process(Page page) {
        List<TVDSBean> parseData = parseInsertData(page.getJson());
        page.putField("TVDataList", parseData);
    }

    private List<TVDSBean> parseInsertData(Json json) {
        // LOG.info("TVDS Json is :" + json.get());
        TVDSJsonObj obj = json.toObject(TVDSJsonObj.class);
        List<TVDSBean> parseData = new ArrayList<TVDSBean>();
        if (obj.getTotal() != null && !obj.getTotal().equals("0")) {
            Date recordTime = parseTime(params.get("LAST_END_TIME"));
            if (recordTime == null) {
                parseData = obj.getRows();
            } else {
                for (TVDSBean bean : obj.getRows()) {
                    Date passTime = parseTime(bean.getSPassTime());
                    if (passTime == null) {
                        continue;
                    }
                    if (passTime.getTime() <= recordTime.getTime()) {
                        break;
                    }
                    parseData.add(bean);
                }
            }
        }
        if (parseData.size() > 0) {
            SpiderProperties.writeProperties(TVDSpider.FILE_PATH, "LAST_END_TIME", parseData.get(0).getSPassTime());
        } else {
            LOG.info("===============no data============");
        }
        return parseData;
    }

    private Date parseTime(String timeStr) {
        Date date = null;
        try {
            date = df.parse(timeStr);
        } catch (ParseException e) {
            LOG.warn("In TVDSpider, parse date error!");
        }
        return date;
    }

    public Site getSite() {
        return site;
    }
}
