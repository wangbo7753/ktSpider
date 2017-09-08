package com.kthw.spider.processor;

import com.kthw.spider.common.HTMLParser;
import com.kthw.spider.common.SpiderProperties;
import com.kthw.spider.model.TPDSBean;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Calendar;

/**
 * Created by YFZX-WB on 2016/9/12.
 */
public class TPDSPageProcessor implements PageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(TPDSPageProcessor.class);

    private Site site;

    private Map<String, String> paraMap;

    public TPDSPageProcessor(Map<String, String> params) {
        this.paraMap = params;
        Set<Integer> acceptCode = new HashSet<Integer>();
        acceptCode.add(200);
        acceptCode.add(302);
        site = Site.me().setRetryTimes(3).setTimeOut(30000).setCharset("utf-8")
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .setAcceptStatCode(acceptCode);
    }

    public void process(Page page) {
        if (page.getRequest().getUrl().indexOf("frmLogin") != -1) {
            Request request = new Request(paraMap.get("BASE_URL"));
            request.setMethod(HttpConstant.Method.POST);
            NameValuePair[] nameValuePair = buildRequestParam(paraMap);
            request.putExtra("nameValuePair", nameValuePair);
            page.addTargetRequest(request);
        } else {
            List<TPDSBean> parseData = parseInsertData(page.getHtml()
                    .xpath("//table[@class='GridViewStyle']//tr[@class='GridViewRow' or @class='GridViewAlterna']").nodes());
            page.putField("TPDataList", parseData);
        }
    }

    private NameValuePair[] buildRequestParam(Map<String, String> params) {
        params.put("dtTimeRange$ctl02", params.get("dtTimeRange$ctl16"));
        params.put("dtTimeRange$ctl04", params.get("dtTimeRange$ctl18"));
        params.put("dtTimeRange$ctl08", params.get("dtTimeRange$ctl22"));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        params.put("dtTimeRange$ctl16", format.format(calendar.getTime()));
        params.put("dtTimeRange$ctl18", formatHM(calendar.get(Calendar.HOUR_OF_DAY)));
        params.put("dtTimeRange$ctl22", formatHM(calendar.get(Calendar.MINUTE)));
        if (StringUtils.isBlank(params.get("dtTimeRange$ctl02")) || StringUtils.isBlank(params.get("dtTimeRange$ctl04"))
                || StringUtils.isBlank(params.get("dtTimeRange$ctl08"))) {
            calendar.add(Calendar.MINUTE, -5);
            params.put("dtTimeRange$ctl02", format.format(calendar.getTime()));
            params.put("dtTimeRange$ctl04", formatHM(calendar.get(Calendar.HOUR_OF_DAY)));
            params.put("dtTimeRange$ctl08", formatHM(calendar.get(Calendar.MINUTE)));
        }
        LOG.info("Begin Time:" + params.get("dtTimeRange$ctl02") + " " + params.get("dtTimeRange$ctl04") + ":" + params.get("dtTimeRange$ctl08"));
        LOG.info("End Time:" + params.get("dtTimeRange$ctl16") + " " + params.get("dtTimeRange$ctl18") + ":" + params.get("dtTimeRange$ctl22"));
        NameValuePair[] pairs = new NameValuePair[params.size() - 3];
        int index = 0;
        for (Map.Entry entry : params.entrySet()) {
            if (!entry.getKey().equals("LOGIN_URL") && !entry.getKey().equals("BASE_URL") && !entry.getKey().equals("INTERVAL")) {
                pairs[index++] = new BasicNameValuePair((String) entry.getKey(), (String) entry.getValue());
            }
        }
        return pairs;
    }

    private String formatHM(int time) {
        return String.format("%02d", time);
    }

    public List<TPDSBean> parseInsertData(List<Selectable> trNodes) {
        List<TPDSBean> result = new ArrayList<TPDSBean>();
        if (trNodes.size() > 0) {
            List<Selectable> tdNodes = trNodes.get(0).xpath("//td").nodes();
            if (tdNodes.size() > 1 && StringUtils.isNotBlank(tdNodes.get(0).xpath("//td/allText()").get())) {
                String[][] table = HTMLParser.tableParse(trNodes, tdNodes.size());
                for (String[] tr : table) {
                    TPDSBean bean = new TPDSBean();
                    bean.setVehicleId(tr[1]);
                    bean.setVehicleType(tr[2]);
                    bean.setAeiPos(tr[3]);
                    bean.setWheelOrder(tr[4]);
                    bean.setDetection(tr[5]);
                    bean.setStation(tr[6] + tr[9]);
                    bean.setPassTime(tr[7]);
                    bean.setTrainId(tr[8].replaceAll("[\\u00A0]+", ""));
                    bean.setDirection(tr[9]);
                    bean.setSpeed(tr[10]);
                    bean.setOrderCar(tr[11]);
                    bean.setFaultDesc(tr[12]);
                    bean.setForecast(tr[13]);
                    bean.setDetectTime(tr[14]);
                    bean.setThdStation(tr[15]);
                    bean.setRoadBureau(tr[16]);
                    bean.setTrainDepot(tr[17]);
                    bean.setOperationField(tr[18]);
                    bean.setArrivalCondition(tr[19]);
                    bean.setDealCondition(tr[20]);
                    bean.setBackFillTime(tr[21]);
                    bean.setDelay(tr[22]);
                    LOG.info(bean.toString());
                    result.add(bean);
                }
            }
        }
        if (result.size() == 0) {
            LOG.info("===============no data============");
        } else {
            SpiderProperties.writeProperties("tpds.properties", paraMap);
        }
        return result;
    }

    public Site getSite() {
        return site;
    }

    /*public static void main(String args[]) {
        Html html = new Html("");
        List<TPDSBean> result = parseInsertData(html
                .xpath("//table[@class='GridViewStyle']//tr[@class='GridViewRow' or @class='GridViewAlterna']").nodes());
        for (TPDSBean bean: result) {
            LOG.info(bean.toString());
        }
    }*/
}
