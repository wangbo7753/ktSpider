package com.kthw.spider.processor;

import com.kthw.spider.common.SpiderProperties;
import com.kthw.spider.main.TFDSpider;
import com.kthw.spider.model.TFDSBean;
import com.kthw.spider.param.TFDSImageParam;
import com.kthw.spider.param.TFDSRequestParam;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by YFZX-WB on 2016/9/7.
 */
public class TFDSPageProcessor implements PageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(TFDSPageProcessor.class);

    private Map<String, String> params;

    private Site site;

    public TFDSPageProcessor(Map<String, String> params) {
        this.site = Site.me().setRetryTimes(3).setSleepTime(2000).setTimeOut(30000).setCharset("gb2312");
        this.params = params;
    }

    public void process(Page page) {
        String currentUrl = page.getUrl().get();
        if (currentUrl.indexOf("fault_list") != -1) {
            List<TFDSBean> parseData = parseInsertData(page.getHtml().xpath("//table[@id='report1']/tbody/tr").nodes());
            page.putField("TFDataList", parseData);
            page.addTargetRequests(buildDetailRequests(parseData));
            if (loadNextPage(page.getHtml().xpath("//div/text()").get())) {
                List<Selectable> paramNodes = page.getHtml().xpath("//form[@name='report1_turnPageForm']/input").nodes();
                TFDSRequestParam nextParam = parseNextPageParams(paramNodes);
                LOG.info(params.get("BASE_URL") + nextParam.toString());
                page.addTargetRequest(params.get("BASE_URL") + nextParam.toString());
                // int currPage = Integer.parseInt(page.getHtml().xpath("//form[@name='report1_turnPageForm']//input[@name='report1_currPage']/@value").get()) + 1;
                // page.addTargetRequest("http://localhost:8080/tfds/fault_list-" + currPage + ".html");
            } else {
                if (parseData.size() == 0) {
                    LOG.info("===============no data============");
                } else {
                    SpiderProperties.writeProperties(TFDSpider.FILE_PATH, "LAST_END_TIME",
                            params.get("E_DATE") + " " + params.get("E_H") + ":" + params.get("E_M"));
                }
            }
        } else if (currentUrl.indexOf("fault_img") != -1) {
            TFDSImageParam param = new TFDSImageParam();
            param.setDetailUrl(currentUrl.substring(currentUrl.indexOf("?") + 1));
            param.setImageUrl(page.getHtml().xpath("//img/@src").get());
            page.putField("TFImageParam", param);
        }
    }

    public List<TFDSBean> parseInsertData(List<Selectable> trNodes) {
        List<TFDSBean> result = new ArrayList<TFDSBean>();
        for (Selectable tr : trNodes) {
            // List<Selectable> tdNodes = tr.css("td.report1_2").nodes();
            List<Selectable> tdNodes = tr.xpath("//td").nodes();
            if (tdNodes != null && tdNodes.size() > 1 && StringUtils.isNotBlank(tdNodes.get(0).xpath("//td/text()").get())) {
                if (StringUtils.isNotBlank(tdNodes.get(6).xpath("//td/a/text()").get())) {
                    TFDSBean bean = new TFDSBean();
                    bean.setCrossDate(tdNodes.get(0).xpath("//td/text()").get());
                    bean.setCrossAddr(tdNodes.get(1).xpath("//td/text()").get());
                    bean.setGroupDigit(tdNodes.get(2).xpath("//td/text()").get());
                    bean.setTrainNum(tdNodes.get(3).xpath("//td/text()").get().replaceAll("[\\u00A0]+", ""));
                    bean.setCarNum(tdNodes.get(4).xpath("//td/text()").get());
                    bean.setTrainModel(tdNodes.get(5).xpath("//td/text()").get().replaceAll("[\\u00A0]+", ""));
                    bean.setFaultName(tdNodes.get(6).xpath("//td/a/text()").get());
                    bean.setRailGuard(tdNodes.get(7).xpath("//td/text()").get());
                    bean.setFaultConfirmor(tdNodes.get(8).xpath("//td/text()").get());
                    bean.setDetailUrl(tdNodes.get(6).xpath("//td/a/@href").get());
                    LOG.info(bean.toString());
                    result.add(bean);
                }
            }
        }
        return result;
    }

    public List<String> buildDetailRequests(List<TFDSBean> parseData) {
        List<String> requests = new ArrayList<String>();
        for (TFDSBean bean : parseData) {
            requests.add(bean.getDetailUrl());
        }
        return requests;
    }

    public boolean loadNextPage(String pageInfo) {
        pageInfo = pageInfo.replaceAll("[\\u00A0]+", " ");
        Pattern pattern = Pattern.compile("第(\\d+)页\\s共(\\d+)页");
        Matcher matcher = pattern.matcher(pageInfo);
        if (matcher.find()) {
            int currentPage = Integer.parseInt(matcher.group(1));
            int totalPage = Integer.parseInt(matcher.group(2));
            LOG.info("currentPage is " + currentPage + "; totalPage is " + totalPage);
            return currentPage < totalPage;
        }
        return false;
    }

    public TFDSRequestParam parseNextPageParams(List<Selectable> paramNodes) {
        Map<String, String> map = new HashMap<String, String>();
        for (Selectable node : paramNodes) {
            String name = node.xpath("//input/@name").get();
            String value = node.xpath("//input/@value").get();
            if (name.equals("report1_currPage")) {
                value = (Integer.parseInt(value) + 1) + "";
            }
            map.put(name, value);
        }
        TFDSRequestParam param = new TFDSRequestParam(map);
        return param;
    }

    public Site getSite() {
        return site;
    }

//    public static void main(String args[]) {
//        Html html = new Html("");
//        parseInsertData(html.xpath("//table[@id='report1']/tbody/tr").nodes());
//        for (String str : html.xpath("//table[@id='report1']/tbody/tr").css("td.report1_2").all()) {
//            LOG.info(str);
//        }
//    }
}
