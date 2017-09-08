package com.kthw.spider.vehicle.processor;

import com.kthw.spider.common.SpiderProperties;
import com.kthw.spider.vehicle.model.VVDSBean;
import com.kthw.spider.vehicle.model.VVJsonObj;
import com.kthw.spider.vehicle.spider.VVDSpider;
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
 * Created by YFZX-WB on 2016/12/19.
 */
public class VVDSPageProcessor implements PageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(VVDSPageProcessor.class);

    private Site site;

    private Map<String, String> params;

    private DateFormat df;

    public VVDSPageProcessor(Map<String, String> params) {
        site = Site.me().setRetryTimes(3).setTimeOut(30000).setCharset("utf-8")
            .addCookie("10.1.184.32", "ASP.NET_SessionId", params.get("COOKIE"));
        this.params = params;
        this.df = new SimpleDateFormat("yyyyMMddHHmmss");
    }

    public void process(Page page) {
        List<VVDSBean> parseData = parseInsertData(page.getJson());
        page.putField("VVDataList", parseData);
    }

    private List<VVDSBean> parseInsertData(Json json) {
        VVJsonObj obj = json.toObject(VVJsonObj.class);
        List<VVDSBean> parseData = new ArrayList<VVDSBean>();
        if (obj.getRows() != null && obj.getRows().size() > 0) {
            Date recordTime = parseTime(params.get("LAST_END_TIME"));
            if (recordTime == null) {
                parseData = obj.getRows();
            } else {
                for (VVDSBean bean : obj.getRows()) {
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
            SpiderProperties.writeProperties(VVDSpider.FILE_PATH, "LAST_END_TIME", parseData.get(0).getSPassTime());
        } else {
            LOG.info("===============VEDS train pass no data============");
        }
        return parseData;
    }

    private Date parseTime(String timeStr) {
        Date date = null;
        try {
            date = df.parse(timeStr);
        } catch (ParseException e) {
            LOG.warn("In VVDSpider, parse date error!");
        }
        return date;
    }

    public Site getSite() {
        return site;
    }
}
