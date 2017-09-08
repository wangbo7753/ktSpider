package com.kthw.spider.vehicle.spider;

import com.kthw.spider.common.SpiderProperties;
import com.kthw.spider.common.db.DBLink;
import com.kthw.spider.main.KTSpider;
import com.kthw.spider.vehicle.pipeline.VEDSPipeline;
import com.kthw.spider.vehicle.processor.VEDSPageProcessor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Spider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by YFZX-WB on 2016/12/16.
 */
public class VEDSpider extends KTSpider {

    private static final Logger LOG = LoggerFactory.getLogger(VEDSpider.class);

    public static final String FILE_PATH = "veds.properties";

    public VEDSpider(DBLink dbLink) {
        super(dbLink);
    }

    public void run() {
        LOG.info("VEDSpider begin");
        while(isRunning()) {
            try {
                Map<String, String> params = SpiderProperties.getAllProperties(FILE_PATH);
                process(params);
                Thread.sleep(Integer.parseInt(params.get("INTERVAL")) * 60 * 1000);
            } catch (Exception e) {
                LOG.error("VEDSpider run error, please check :", e);
                break;
            }
        }
        LOG.info("VEDSpider end");
    }

    public void process(Map<String, String> params) {
        String baseUrl = params.get("BASE_URL");
        if (StringUtils.isNotBlank(baseUrl)) {
            baseUrl = reBuildUrl(params);
            Spider.create(new VEDSPageProcessor(params))
                .addUrl(baseUrl)
                    .addPipeline(new VEDSPipeline(getDbLink()))
                        .thread(1).run();
        }
    }

    public String reBuildUrl(Map<String, String> params) {
        StringBuffer sb = new StringBuffer(params.get("BASE_URL"));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd+HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -3);
        String endTime = format.format(calendar.getTime());
        sb.append("&passEndDate=").append(endTime);
        calendar.add(Calendar.SECOND, 1);
        endTime = format.format(calendar.getTime());
        if (StringUtils.isBlank(params.get("LAST_END_TIME"))) {
            calendar.add(Calendar.MINUTE, -10);
            sb.append("&passDate=").append(format.format(calendar.getTime()));
        } else {
            sb.append("&passDate=").append(params.get("LAST_END_TIME"));
        }
        params.put("LAST_END_TIME", endTime);
        return sb.toString();
    }

}
