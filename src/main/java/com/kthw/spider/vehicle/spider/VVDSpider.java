package com.kthw.spider.vehicle.spider;

import com.kthw.spider.common.SpiderProperties;
import com.kthw.spider.common.db.DBLink;
import com.kthw.spider.main.KTSpider;
import com.kthw.spider.vehicle.pipeline.VVDSPipeline;
import com.kthw.spider.vehicle.processor.VVDSPageProcessor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Spider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by YFZX-WB on 2016/12/19.
 */
public class VVDSpider extends KTSpider {

    private static final Logger LOG = LoggerFactory.getLogger(VVDSpider.class);

    public static final String FILE_PATH = "vvds.properties";

    private DateFormat df;

    public VVDSpider(DBLink dbLink) {
        super(dbLink);
        df = new SimpleDateFormat("yyyy-MM-dd");
    }

    public void run() {
        LOG.info("VVDSpider begin");
        while (isRunning()) {
            try {
                Map<String, String> params = SpiderProperties.getAllProperties(FILE_PATH);
                process(params);
                Thread.sleep(Integer.valueOf(params.get("INTERVAL")) * 60 * 1000);
            } catch (Exception e) {
                LOG.error("VVDSpider run error, please check :" , e);
                break;
            }
        }
        LOG.info("VVDSpider end");
    }

    public void process(Map<String, String> params) {
        StringBuffer baseUrl = new StringBuffer(params.get("BASE_URL"));
        if (StringUtils.isNotBlank(baseUrl.toString())) {
            baseUrl.append("&Time=").append(df.format(new Date()));
            Spider.create(new VVDSPageProcessor(params))
                .addUrl(baseUrl.toString())
                    .addPipeline(new VVDSPipeline(getDbLink()))
                        .thread(1).run();
        }
    }

}
