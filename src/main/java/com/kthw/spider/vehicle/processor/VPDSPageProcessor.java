package com.kthw.spider.vehicle.processor;

import com.kthw.spider.common.SpiderProperties;
import com.kthw.spider.vehicle.model.VPDSBean;
import com.kthw.spider.vehicle.spider.VPDSpider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.utils.HttpConstant;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by YFZX-WB on 2016/12/22.
 */
public class VPDSPageProcessor implements PageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(VPDSPageProcessor.class);

    private Site site;

    private Map<String, String> params;

    public VPDSPageProcessor(Map<String, String> params) {
        Set<Integer> acceptCode = new HashSet<Integer>(2);
        acceptCode.add(200);
        acceptCode.add(302);
        site = Site.me().setRetryTimes(3).setTimeOut(30000).setCharset("utf-8")
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .setAcceptStatCode(acceptCode);
        this.params = params;
    }

    public void process(Page page) {
        if (page.getRequest().getUrl().indexOf("frmLogin") != -1) {
            Request request = new Request(params.get("BASE_URL"));
            NameValuePair[] nameValuePair = buildRequestParam(params);
            request.setMethod(HttpConstant.Method.POST);
            request.putExtra("nameValuePair", nameValuePair);
            page.addTargetRequest(request);
        } else {
            List<Selectable> trNodes = page.getHtml()
                    .xpath("//table[@class='GridViewStyle']//tr[@class='GridViewRow' or @class='GridViewAlterna']").nodes();
            if (CollectionUtils.isNotEmpty(trNodes)) {
                List<VPDSBean> parseData = parseInsertData(trNodes);
                page.putField("VPDataList", parseData);
                if (parseData.size() == 0) {
                    LOG.info("===============no data============");
                } else {
                    LOG.info("===============get {} sizes data============", parseData.size());
                    SpiderProperties.writeProperties(VPDSpider.FILE_PATH, "LAST_END_TIME", params.get("LAST_END_TIME"));
                }
            } else {
                LOG.warn("=============error page==============");
            }
        }
    }

    private NameValuePair[] buildRequestParam(Map<String, String> params) {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!entry.getKey().equals("LOGIN_URL") && !entry.getKey().equals("BASE_URL") && !entry.getKey().equals("INTERVAL") && !entry.getKey().equals("LAST_END_TIME")) {
                pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -5);
        String lastEndTime = df.format(calendar.getTime());
        pairs.add(new BasicNameValuePair("dtTimeRange2$ctl02", format.format(calendar.getTime())));
        pairs.add(new BasicNameValuePair("dtTimeRange2$ctl04", String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY))));
        pairs.add(new BasicNameValuePair("dtTimeRange2$ctl08", String.format("%02d", calendar.get(Calendar.MINUTE))));
        Date startTime = null;
        try {
            startTime = df.parse(params.get("LAST_END_TIME"));
        } catch (ParseException e) {
            LOG.error("In VPDSpider, parse date error...", e);
        }
        if (startTime == null) {
            calendar.add(Calendar.MINUTE, -5);
        } else {
            calendar.setTime(startTime);
            calendar.add(Calendar.MINUTE, 1);
        }
        pairs.add(new BasicNameValuePair("dtTimeRange$ctl02", format.format(calendar.getTime())));
        pairs.add(new BasicNameValuePair("dtTimeRange$ctl04", String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY))));
        pairs.add(new BasicNameValuePair("dtTimeRange$ctl08", String.format("%02d", calendar.get(Calendar.MINUTE))));
        params.put("LAST_END_TIME", lastEndTime);
        return pairs.toArray(new NameValuePair[pairs.size()]);
    }

    private List<VPDSBean> parseInsertData(List<Selectable> trNodes) {
        List<VPDSBean> result = new ArrayList<VPDSBean>();
        for (Selectable trNode : trNodes) {
            List<Selectable> tdNodes = trNode.xpath("//td").nodes();
            if (tdNodes.size() > 1) {
                VPDSBean bean = new VPDSBean();
                bean.setTrainDirect(tdNodes.get(8).xpath("//td/text()").get());
                bean.setStationName(tdNodes.get(3).xpath("//td/text()").get() + bean.getTrainDirect());
                bean.setLineType(tdNodes.get(4).xpath("//td/text()").get());
                bean.setPassTime(tdNodes.get(5).xpath("//td/text()").get());
                bean.setTrainType(tdNodes.get(6).xpath("//td/text()").get() + "车");
                bean.setTrainId(tdNodes.get(7).xpath("//td/text()").get().replaceAll("[\\u00A0]+", ""));
                bean.setAvgSpeed(tdNodes.get(9).xpath("//td/text()").get());
                bean.setVehNumber(tdNodes.get(10).xpath("//td/allText()").get());
                bean.setReportId(tdNodes.get(10).xpath("//td/a/@href").get());
                result.add(bean);
            }
        }
        return result;
    }

    public Site getSite() {
        return site;
    }
}
