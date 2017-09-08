package com.kthw.spider.processor;

import com.kthw.spider.common.HTMLParser;
import com.kthw.spider.common.SpiderProperties;
import com.kthw.spider.model.DEVBean;
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

public class DEVPageProcessor implements PageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(TPDSPageProcessor.class);

    private Site site;

    private Map<String, String> paraMap;

    public DEVPageProcessor(Map<String, String> params) {
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
            request.setMethod(HttpConstant.Method.GET);
            page.addTargetRequest(request);
        } else {
            List<DEVBean> parseData = parseInsertData(page.getHtml()
                    .xpath("//table[@class='GridViewStyle']//tr[@class='GridViewRow' or @class='GridViewAlterna']").nodes());
            page.putField("DataList", parseData);
        }
    }


    private String formatHM(int time) {
        return String.format("%02d", time);
    }

    public List<DEVBean> parseInsertData(List<Selectable> trNodes) {
        List<DEVBean> result = new ArrayList<DEVBean>();
        if (trNodes.size() > 0) {
            List<Selectable> tdNodes = trNodes.get(0).xpath("//td").nodes();
            if (tdNodes.size() > 1 && StringUtils.isNotBlank(tdNodes.get(0).xpath("//td/allText()").get())) {
                String[][] table = HTMLParser.tableParseEx(trNodes, tdNodes.size());
                for (String[] tr : table) {
                    DEVBean bean = new DEVBean();
                    int startIdx = 1;
                    bean.setBureauName(tr[startIdx++]);
                    bean.setDepotName(tr[startIdx++]);
                    bean.setSiteName(tr[startIdx++]);
                    bean.setLineType(tr[startIdx++]);
                    bean.setDevType(tr[startIdx++].substring(1,2));
                    bean.setDirection(tr[startIdx++]);
                    bean.setKiloMeter(tr[startIdx++]);
                    bean.setLastPassTime(tr[startIdx++]);
                    bean.setFaultDesc(tr[startIdx++]);
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
