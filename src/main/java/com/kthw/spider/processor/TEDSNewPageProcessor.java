package com.kthw.spider.processor;

import com.kthw.spider.common.SpiderProperties;
import com.kthw.spider.main.TEDSpider;
import com.kthw.spider.model.TEDSBean;
import com.kthw.spider.model.TEDSJsonObj;
import com.kthw.spider.model.TEDSResponseBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Json;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * Created by YFZX-WB on 2016/9/8.
 */
public class TEDSNewPageProcessor implements PageProcessor {

    private static Logger LOG = LoggerFactory.getLogger(TEDSNewPageProcessor.class);

    private Map<String, String> params;

    private Site site;

    public TEDSNewPageProcessor(Map<String, String> params) {
        this.site = Site.me().setRetryTimes(3).setTimeOut(30000).setCharset("utf-8")
            .addCookie("10.1.184.32", "ASP.NET_SessionId", params.get("COOKIE"));
        this.params = params;
    }

    public void process(Page page) {
        List<TEDSBean> parseData = parseInsertData(page.getJson());
        page.putField("TEDataList", parseData);
    }

    public List<TEDSBean> parseInsertData(Json json) {
        TEDSJsonObj obj = json.toObject(TEDSJsonObj.class);
        List<TEDSBean> result = new ArrayList<TEDSBean>();
        if (obj.getTotal() != null && !obj.getTotal().equals("0")) {
            for (TEDSResponseBean resBean : obj.getRows()) {
                TEDSBean bean = new TEDSBean();
                bean.setTrainModel(resBean.getSTrainsettype2());
                bean.setTrainUnitNum(resBean.getSTrainsetno());
                bean.setTrainIdentityNum(resBean.getSCarcode());
                bean.setCrossTime(resBean.getSPasstime());
                bean.setCheckStation(resBean.getSStationname());
                bean.setAlarmType(resBean.getCAffirmflag());
                bean.setAlarmParts(resBean.getSRcdsystypename());
                bean.setManuAlertId(resBean.getSManualertid());
                bean.setAlarmName(resBean.getSAlarmmodename());
                bean.setConfirmor(resBean.getSChief());
                result.add(bean);
            }
        }
        if (result.size() > 0) {
            SpiderProperties.writeProperties(TEDSpider.FILE_PATH, "LAST_END_TIME", params.get("LAST_END_TIME"));
        } else {
            LOG.info("===============no data============");
        }
        return result;
    }

    public Site getSite() {
        return site;
    }

}
