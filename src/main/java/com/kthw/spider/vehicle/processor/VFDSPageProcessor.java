package com.kthw.spider.vehicle.processor;

import com.kthw.spider.common.SpiderProperties;
import com.kthw.spider.vehicle.model.VFDSBean;
import com.kthw.spider.vehicle.spider.VFDSpider;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by YFZX-WB on 2016/12/19.
 */
public class VFDSPageProcessor implements PageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(VFDSPageProcessor.class);

    private Site site;

    private Map<String, String> params;

    public VFDSPageProcessor(Map<String, String> params) {
        this.site = Site.me().setRetryTimes(3).setSleepTime(2000).setCharset("gb2312");
        this.params = params;
    }

    public void process(Page page) {
        List<VFDSBean> parseData = parseInsertData(page.getHtml().xpath("//table[@id='report1']/tbody/tr").nodes());
        page.putField("VFDataList", parseData);
        if (loadNextPage(page.getHtml().xpath("//div/text()").get())) {
            List<Selectable> paramNodes = page.getHtml().xpath("//form[@name='report1_turnPageForm']/input").nodes();
            String nextUrl = parseNextPageUrl(paramNodes);
            page.addTargetRequest(nextUrl);
        } else {
            if (parseData.size() == 0) {
                LOG.info("===============no data============");
            } else {
                SpiderProperties.writeProperties(VFDSpider.FILE_PATH, "LAST_END_TIME",
                        params.get("E_DATE") + " " + params.get("E_H") + ":" + params.get("E_M"));
            }
        }
    }

    public List<VFDSBean> parseInsertData(List<Selectable> trNodes) {
        List<VFDSBean> result = new ArrayList<VFDSBean>();
        for (Selectable tr : trNodes) {
            // List<Selectable> tdNodes = tr.css("td.report1_1").nodes();
            List<Selectable> tdNodes = tr.xpath("//td").nodes();
            if (tdNodes != null && tdNodes.size() > 0 && StringUtils.isNotBlank(tdNodes.get(0).xpath("//td/a/text()").get())) {
                VFDSBean bean = new VFDSBean();
                bean.setPassTime(tdNodes.get(0).xpath("//td/a/text()").get());
                bean.setStationName(tdNodes.get(1).xpath("//td/text()").get());
                bean.setTrainType(tdNodes.get(2).xpath("//td/text()").get());
                bean.setTrainId(tdNodes.get(3).xpath("//td/text()").get().replaceAll("[\\u00A0]+", ""));
                bean.setTrainDirect(tdNodes.get(4).xpath("//td/text()").get());
                bean.setAvgSpeed(tdNodes.get(5).xpath("//td/text()").get());
                bean.setVehNumber(tdNodes.get(6).xpath("//td/text()").get());
                result.add(bean);
            }
        }
        return result;
    }

    private boolean loadNextPage(String pageInfo) {
        pageInfo = pageInfo.replaceAll("[\\u00A0]+", " ");
        Pattern pattern = Pattern.compile("第(\\d+)页\\s共(\\d+)页");
        Matcher matcher = pattern.matcher(pageInfo);
        if (matcher.find()) {
            int currentPage = Integer.parseInt(matcher.group(1));
            int totalPage = Integer.parseInt(matcher.group(2));
            return currentPage < totalPage;
        }
        return false;
    }

    private String parseNextPageUrl(List<Selectable> paramNodes) {
        StringBuffer sb = new StringBuffer(params.get("BASE_URL")).append("?");
        for (Selectable node : paramNodes) {
            String name = node.xpath("//input/@name").get();
            String value = node.xpath("//input/@value").get();
            if (name.equals("report1_currPage")) {
                value = (Integer.parseInt(value) + 1) + "";
            }
            sb.append(name).append("=").append(value).append("&");
        }
        return sb.substring(0, sb.length() - 1).toString();
    }

    public Site getSite() {
        return site;
    }

}
