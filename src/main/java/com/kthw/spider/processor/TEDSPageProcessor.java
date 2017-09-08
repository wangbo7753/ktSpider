package com.kthw.spider.processor;

import com.kthw.spider.common.HTMLParser;
import com.kthw.spider.common.SpiderProperties;
import com.kthw.spider.main.TEDSpider;
import com.kthw.spider.model.TEDSBean;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by YFZX-WB on 2016/9/8.
 */
public class TEDSPageProcessor implements PageProcessor {

    private static Logger LOG = LoggerFactory.getLogger(TEDSPageProcessor.class);

    private String recordTime;

    private DateFormat df;

    private Site site;

    public TEDSPageProcessor(Map<String, String> params) {
        this.site = Site.me().setRetryTimes(3).setSleepTime(2000).setCharset("utf-8")
            .addCookie("10.1.184.32", "ASP.NET_SessionId", params.get("COOKIE"));
        this.df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        this.recordTime = params.get("LAST_END_TIME");
    }

    public void process(Page page) {
        List<TEDSBean> parseData = parseInsertData(page.getHtml()
                .xpath("//table[@id='gvDetail']//tr[@class='dgdAlterntingItem' or @class='dgdItem']").nodes());
        page.putField("TEDataList", parseData);
        //if (loadNextPage(page.getHtml().xpath("//div[@id='QueryPager']"))) {
        //    page.addTargetRequest(buildPostParam(page));
        //    int nextPage = Integer.parseInt(page.getHtml().xpath("//div[@id='QueryPager']//table//span/text()").get()) + 1;
        //    page.addTargetRequest("http://localhost:8080/teds/TEDS-" + nextPage + ".html");
        // }
    }

    public List<TEDSBean> parseInsertData(List<Selectable> trNodes) {
        List<TEDSBean> result = new ArrayList<TEDSBean>();
        if (trNodes.size() > 0) {
            int colSize = trNodes.get(0).xpath("//td").nodes().size();
            if (colSize > 1) {
                String[][] table = HTMLParser.tableParse(trNodes, colSize);
                Date recordDate = buildRecordTime();
                for (int i = 0; i < table.length; i++) {
                    Date crossDate = parseTime(table[i][4]);
                    if (crossDate == null) {
                        continue;
                    }
                    if (crossDate.getTime() <= recordDate.getTime()) {
                        break;
                    }
                    TEDSBean bean = new TEDSBean();
                    bean.setTrainModel(table[i][1]);
                    bean.setTrainUnitNum(table[i][2]);
                    bean.setTrainIdentityNum(table[i][3]);
                    bean.setCrossTime(table[i][4]);
                    bean.setCheckStation(table[i][5]);
                    bean.setAlarmType(table[i][6]);
                    String[] details = table[i][7].split("_");
                    bean.setAlarmParts(details[0]);
                    bean.setManuAlertId(details.length > 1 ? details[1] : null);
                    bean.setAlarmName(table[i][8]);
                    bean.setConfirmor(table[i][9]);
                    bean.setReviewStatus(table[i][10]);
                    bean.setRepairStatus(table[i][11]);
                    LOG.info(bean.toString());
                    result.add(bean);
                }
            }
        }
        if (result.size() > 0) {
            SpiderProperties.writeProperties(TEDSpider.FILE_PATH, "LAST_END_TIME", result.get(0).getCrossTime());
        } else {
            LOG.info("===============no data============");
        }
        return result;
    }

    public Date buildRecordTime() {
        Date date = parseTime(recordTime);
        if (date == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, -5);
            date = calendar.getTime();
        }
        return date;
    }

    public Date parseTime(String time) {
        Date date = null;
        try {
            date = this.df.parse(time);
        } catch (ParseException e) {
            LOG.info("parse date error!");
        }
        return date;
    }

    public boolean loadNextPage(Selectable pagination) {
        LOG.info(pagination.get());
        try {
            int currentPage = Integer.parseInt(pagination.xpath("//table//span/text()").get());
            String endTitle = pagination.xpath("//a[4]").get();
            if (StringUtils.isNotBlank(endTitle))  {
                Pattern pattern = Pattern.compile("转到第(\\d+)页");
                Matcher matcher = pattern.matcher(endTitle);
                if (matcher.find()) {
                    int totalPage = Integer.parseInt(matcher.group(1));
                    LOG.info("currentPage is " + currentPage + "; totalPage is " + totalPage);
                    return currentPage < totalPage;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Request buildPostParam(Page page) {
        Request request = page.getRequest();
        request.setMethod(HttpConstant.Method.POST);
        NameValuePair[] nameValuePair = (NameValuePair[]) request.getExtra("nameValuePair");
        if (nameValuePair == null) {
            nameValuePair = SpiderProperties.getAllPostParams(TEDSpider.FILE_PATH);
        }
        for (int i = 0; i < nameValuePair.length; i++) {
            if (nameValuePair[i].getName().equals("__EVENTARGUMENT")) {
                int nextPage = Integer.parseInt(page.getHtml().xpath("//div[@id='QueryPager']//table//span/text()").get()) + 1;
                nameValuePair[i] = new BasicNameValuePair("__EVENTARGUMENT", nextPage + "");
            }
        }
        request.putExtra("nameValuePair", nameValuePair);
        return request;
    }

    public Site getSite() {
        return site;
    }

    /*public static void main(String args[]) {
        Html html = new Html("");
        parseInsertData(html
                .xpath("//table[@id='gvDetail']//tr[@class='dgdAlterntingItem' or @class='dgdItem']").nodes());
    }*/
}
