package com.kthw.spider.vehicle.processor;

import com.kthw.spider.common.SpiderProperties;
import com.kthw.spider.vehicle.model.VADSBean;
import com.kthw.spider.vehicle.spider.VADSpider;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by YFZX-WB on 2016/12/20.
 */
public class VADSPageProcessor implements PageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(VADSPageProcessor.class);

    private Site site;

    private Map<String, String> params;

    public VADSPageProcessor(Map<String, String> params) {
        site = Site.me().setRetryTimes(3).setSleepTime(2000);
        this.params = params;
    }

    public void process(Page page) {
        List<Selectable> trNodes = page.getHtml().xpath("//table[@id='TrainGrid']/tbody/tr").nodes();
        if (CollectionUtils.isNotEmpty(trNodes)) {
            List<VADSBean> parseData = parseInsertData(trNodes);
            page.putField("VADataList", parseData);
            if (parseData.size() == 0) {
                LOG.info("===============no data============");
            } else {
                LOG.info("===============get {} sizes data============", parseData.size());
                SpiderProperties.writeProperties(VADSpider.FILE_PATH, params);
            }
        } else {
            LOG.info("=============sequence invalid============");
        }
    }

    private List<VADSBean> parseInsertData(List<Selectable> trNodes) {
        List<VADSBean> result = new ArrayList<VADSBean>();
        for (int i = 1; i < trNodes.size(); i++) {
            List<Selectable> tdNodes = trNodes.get(i).xpath("//td").nodes();
            VADSBean bean = new VADSBean();
            bean.setLineType(tdNodes.get(2).xpath("//td/allText()").get());
            bean.setTrainDirect(tdNodes.get(4).xpath("//td/allText()").get());
            bean.setStationName(tdNodes.get(3).xpath("//td/allText()").get() + bean.getTrainDirect());
            bean.setPassTime(tdNodes.get(5).xpath("//td//a/text()").get());
            bean.setTrainId(tdNodes.get(6).xpath("//td/allText()").get());
            bean.setAvgSpeed(tdNodes.get(7).xpath("//td/allText()").get());
            bean.setVehNumber(tdNodes.get(8).xpath("//td/allText()").get());
            bean.setAxleCount(tdNodes.get(9).xpath("//td/allText()").get());
            bean.setTrainType(tdNodes.get(12).xpath("//td/allText()").get());
            result.add(bean);
        }
        return result;
    }

    public Site getSite() {
        return site;
    }

}
