package com.kthw.spider.processor;

import com.kthw.spider.common.SpiderProperties;
import com.kthw.spider.main.TADSpider;
import com.kthw.spider.model.TADSBean;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class TADSPageProcessor implements PageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(TADSPageProcessor.class);

    private Site site;

    private Map<String, String> params;

    private DateFormat df;

    public TADSPageProcessor(Map<String, String> params) {
        site = Site.me().setRetryTimes(3).setSleepTime(2000).setCharset("utf-8");
        this.params = params;
        df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

    public void process(Page page) {
        if (page.getRequest().getUrl().indexOf("login") != -1) {
            page.addTargetRequest(buildRequestUrl(params));
        } else {
            // LOG.info(page.getHtml().xpath("//table[@id='TrainGrid']").get());
            List<TADSBean> parseData = parseInsertData(page.getHtml().xpath("//table[@id='TrainGrid']//tr").nodes());
            page.putField("TADataList", parseData);
        }
    }

    public String buildRequestUrl(Map<String, String> params) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd+HH:mm");
        Calendar calendar = Calendar.getInstance();
        params.put("EndTime", format.format(calendar.getTime()));
        if (StringUtils.isBlank(params.get("LAST_END_TIME"))) {
            calendar.add(Calendar.MINUTE, -5);
            params.put("LAST_END_TIME", format.format(calendar.getTime()));
        }
        StringBuffer sb = new StringBuffer();
        try {
            sb.append(String.format(params.get("REQUEST_URL"), params.get("randomCode")));
            sb.append("?StartTime=" + params.get("LAST_END_TIME"));
            sb.append("&EndTime=" + params.get("EndTime"));
            sb.append("&Order=pass_time");
            sb.append("&OrderMode=desc");
            sb.append("&title=" + URLEncoder.encode("上海铁路局", "UTF-8"));
            sb.append("&Where=+and+LUJU_ID+%3d+%27H%27");
            sb.append("&Condition=" + URLEncoder.encode("故障类型：全部  故障等级：全部", "UTF-8"));
            sb.append("&Filtrate=True");
            sb.append("&AllowPaging=True");
            sb.append("&PageSize=35");
        } catch (IOException e) {
            LOG.error("TASpider", e);
        }
        return sb.toString();
    }

    public List<TADSBean> parseInsertData(List<Selectable> trNodes) {
        List<TADSBean> result = new ArrayList<TADSBean>();
        if (trNodes.size() > 2) {
            Date lastEnd = parseTime(params.get("LAST_END_TIME").replace("+", " "));
            for (int i = 1; i < trNodes.size() - 1; i++) {
                List<Selectable> tdNode = trNodes.get(i).xpath("//td").nodes();
                Date passDate = parseTime(tdNode.get(8).xpath("//td//a/text()").get());
                if (passDate == null) {
                    continue;
                }
                if (passDate.getTime() <= lastEnd.getTime()) {
                    break;
                }
                TADSBean bean = new TADSBean();
                bean.setBureau(tdNode.get(1).xpath("//td/allText()").get());
                bean.setLine(tdNode.get(2).xpath("//td/allText()").get());
                bean.setSite(tdNode.get(3).xpath("//td/allText()").get());
                bean.setTrain_id(tdNode.get(4).xpath("//td//a/text()").get());
                String[] temp = tdNode.get(5).xpath("//td//a/text()").get().split(" ");
                bean.setTrain_type(temp[0]);
                bean.setVehicle_id(temp[1]);
                bean.setVehicle_order(tdNode.get(6).xpath("//td/allText()").get());
                bean.setAxle_number(tdNode.get(7).xpath("//td//a/text()").get());
                bean.setPass_time(tdNode.get(8).xpath("//td//a/text()").get());
                bean.setFault_type(tdNode.get(9).xpath("//td/allText()").get());
                bean.setFault_level(tdNode.get(10).xpath("//td/allText()").get());
                bean.setAlarm_number(tdNode.get(11).xpath("//td//a/text()").get());
                bean.setCheck(tdNode.get(12).xpath("//td//a/text()").get());
                bean.setTransact(tdNode.get(13).xpath("//td//a/text()").get());
                LOG.info(bean.toString());
                result.add(bean);
            }
        }
        if (result.size() > 0) {
            SpiderProperties.writeProperties(TADSpider.FILE_PATH, "LAST_END_TIME", result.get(0).getPass_time().replace(" ", "+"));
        } else {
            LOG.info("===============no data============");
        }
        return result;
    }

    public Date parseTime(String str) {
        Date date = null;
        try {
            date = df.parse(str);
        } catch (ParseException e) {
            LOG.error("parse error!", e);
        }
        return date;
    }

    public Site getSite() {
        return site;
    }

}
