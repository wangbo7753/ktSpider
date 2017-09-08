package com.kthw.spider.processor;

import com.kthw.spider.common.SpiderProperties;
import com.kthw.spider.model.TCDSBean;
import org.apache.commons.lang.StringUtils;
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

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Calendar;

/**
 * Created by YFZX-WB on 2017/4/10.
 */
public class TCDSPageProcessor implements PageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(TCDSPageProcessor.class);

    private static final String BASE_URL = "http://10.128.2.36/tcds/tcds/RealTimeTrain/%s";

    private static final String MONITOR_NO = "monitorNo";

    private Site site;

    private Map<String, String> paraMap;

    public TCDSPageProcessor(Map<String, String> params) {
        this.paraMap = params;
        Set<Integer> acceptCode = new HashSet<Integer>();
        acceptCode.add(200);
        acceptCode.add(302);
        site = Site.me().setRetryTimes(3).setTimeOut(30000).setCharset("utf-8")
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .setAcceptStatCode(acceptCode);
    }

    public void process(Page page) {
        if (page.getRequest().getUrl().indexOf("login.aspx") != -1) {
            Request request = new Request();
            request.setUrl(String.format(BASE_URL, "frmLaterFault.aspx?progid=tcds_lastfault&previlege=0001"));
            request.setMethod(HttpConstant.Method.POST);
            NameValuePair[] nameValuePair = buildRequestParam(paraMap);
            request.putExtra("nameValuePair", nameValuePair);
            page.addTargetRequest(request);
        } else if (page.getRequest().getUrl().indexOf("frmLaterFault.aspx") != -1) {
            Selectable span = page.getHtml().xpath("//span[@id='lblMsg']");
            if (span != null && StringUtils.isBlank(span.xpath("//span/text()").get())) {
                List<String> details = parseDetailUrls(page.getHtml()
                    .xpath("//table[@id='dgdCarFault']//tr[@class='dgdItem' or @class='dgdAlterntingItem']").nodes());
                page.addTargetRequests(details);
            }
        } else {
            List<TCDSBean> parseData = parseInsertData(page);
            page.putField("TCDataList", parseData);
        }
    }

    private NameValuePair[] buildRequestParam(Map<String, String> params) {
        params.put("dttStart$ctl03", params.get("dttEnd$ctl03"));
        params.put("dttStart$ctl07", params.get("dttEnd$ctl07"));
        params.put("dttStart$ctl11", params.get("dttEnd$ctl11"));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        params.put("dttEnd$ctl03", format.format(calendar.getTime()));
        params.put("dttEnd$ctl07", String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)));
        params.put("dttEnd$ctl11", String.format("%02d", calendar.get(Calendar.MINUTE)));
        if (StringUtils.isBlank(params.get("dttStart$ctl03")) || StringUtils.isBlank(params.get("dttStart$ctl07"))
                || StringUtils.isBlank(params.get("dttStart$ctl11"))) {
            calendar.add(Calendar.MINUTE, -5);
            params.put("dttStart$ctl03", format.format(calendar.getTime()));
            params.put("dttStart$ctl07", String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)));
            params.put("dttStart$ctl11", String.format("%02d", calendar.get(Calendar.MINUTE)));
        }
        LOG.info("Begin Time:" + params.get("dttStart$ctl03") + " " + params.get("dttStart$ctl07") + ":" + params.get("dttStart$ctl11"));
        LOG.info("End Time:" + params.get("dttEnd$ctl03") + " " + params.get("dttEnd$ctl07") + ":" + params.get("dttEnd$ctl11"));
        NameValuePair[] pairs = new NameValuePair[params.size() - 2];
        int index = 0;
        for (Map.Entry entry : params.entrySet()) {
            if (!entry.getKey().equals("LOGIN_URL") && !entry.getKey().equals("INTERVAL")) {
                pairs[index++] = new BasicNameValuePair((String) entry.getKey(), (String) entry.getValue());
            }
        }
        return pairs;
    }

    private List<String> parseDetailUrls(List<Selectable> trNodes) {
        List<String> details = new ArrayList<String>();
        if (trNodes.size() > 0) {
            for (Selectable trNode : trNodes) {
                String onClick = null, monitorNo = null;
                try {
                    List<Selectable> tdNodes = trNode.xpath("//td").nodes();
                    onClick = tdNodes.get(1).xpath("//td/@onclick").get();
                    monitorNo = tdNodes.get(2).xpath("//td/text()").get();
                } catch (Exception e) {
                    LOG.error("parse get link table error, please check: ", e);
                }
                if (StringUtils.isNotBlank(onClick)) {
                    onClick = onClick.split("'")[1].substring(2);
                    details.add(String.format(BASE_URL, new StringBuffer(onClick).append("&").append(MONITOR_NO).append("=").append(monitorNo)));
                }
            }
        }
        if (details.size() == 0) {
            LOG.info("===============no data============");
        } else {
            SpiderProperties.writeProperties("tcds.properties", paraMap);
        }
        return details;
    }

    private List<TCDSBean> parseInsertData(Page page) {
        List<TCDSBean> result = new ArrayList<TCDSBean>();
        String monitorNo = null;
        if (page.getRequest().getUrl().indexOf(MONITOR_NO) != -1) {
            monitorNo = page.getRequest().getUrl().substring(page.getRequest().getUrl().lastIndexOf("&") + MONITOR_NO.length() + 2);
        }
        String title = page.getHtml().xpath("//span[@id='lblTitle']/allText()").get();
        if (StringUtils.isNotBlank(title)) {
            String[] carInfo = title.split(" ");
            List<Selectable> trNodes = page.getHtml().xpath("//table[@id='dgdCarFault']//tr[@class='dgdItem' or @class='dgdAlterntingItem']").nodes();
            for (Selectable trNode : trNodes) {
                TCDSBean bean = new TCDSBean();
                bean.setCarDepot(carInfo[0]);
                bean.setTrainNo(carInfo[1].replace("次", ""));
                bean.setCarOrder(carInfo[3].replace("厢", ""));
                bean.setCarNo(carInfo[4].replace("车", ""));
                bean.setMonitorNo(monitorNo);
                List<Selectable> tdNodes = trNode.xpath("//td").nodes();
                bean.setProvider(tdNodes.get(1).xpath("//td/text()").get());
                bean.setFaultType(tdNodes.get(2).xpath("//td/text()").get());
                bean.setFaultDesc(tdNodes.get(3).xpath("//td/text()").get());
                bean.setRunState(tdNodes.get(4).xpath("//td/text()").get());
                bean.setState(tdNodes.get(5).xpath("//td/text()").get());
                bean.setDetectTime(tdNodes.get(6).xpath("//td/text()").get());
                bean.setParam1(tdNodes.get(7).xpath("//td/text()").get());
                bean.setParam2(tdNodes.get(8).xpath("//td/text()").get());
                bean.setParam3(tdNodes.get(9).xpath("//td/text()").get());
                bean.setParam4(tdNodes.get(10).xpath("//td/text()").get());
                result.add(bean);
            }
        }
        return result;
    }

    public Site getSite() {
        return site;
    }
}
