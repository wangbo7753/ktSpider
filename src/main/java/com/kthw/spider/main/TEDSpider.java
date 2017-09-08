package com.kthw.spider.main;

import com.kthw.spider.common.SpiderProperties;
import com.kthw.spider.common.db.DBLink;
import com.kthw.spider.downloader.KTDownLoader;
import com.kthw.spider.pipeline.TEDSImagePipeline;
import com.kthw.spider.pipeline.TEDSPipeline;
import com.kthw.spider.processor.TEDSNewPageProcessor;
import com.kthw.spider.processor.TEDSPageProcessor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Spider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by YFZX-WB on 2016/9/9.
 */
public class TEDSpider extends KTSpider {

    public static final Logger LOG = LoggerFactory.getLogger(TEDSpider.class);

    public static final String FILE_PATH = "teds.properties";

    public TEDSpider(DBLink dbLink) {
        super(dbLink);
    }

    public void run() {
        LOG.info("TEDSpider begin");
        while(isRunning()) {
            try {
                Map<String, String> params = SpiderProperties.getAllProperties(FILE_PATH);
                process(params);
                Thread.sleep(Integer.parseInt(params.get("INTERVAL")) * 60 * 1000);
            } catch (Exception e) {
                LOG.error("TEDSpider run error, please check :", e);
                break;
            }
        }
        LOG.info("TEDSpider end");
    }

    public void process(Map<String, String> params) {
        String baseUrl = params.get("BASE_URL");
        if (StringUtils.isNotBlank(baseUrl)) {
            if ("1".equals(params.get("VERSION"))) {
                baseUrl = reBuildUrl(params);
            }
            Spider.create("1".equals(params.get("VERSION")) ? new TEDSNewPageProcessor(params) : new TEDSPageProcessor(params))
                .addUrl(baseUrl)
                    .setDownloader(new KTDownLoader(getDbLink(), "E"))
                        .addPipeline(new TEDSPipeline(getDbLink(), params))
                            .addPipeline(new TEDSImagePipeline(params))
                                .thread(1).run();
        }
    }

    public String reBuildUrl(Map<String, String> params) {
        StringBuffer sb = new StringBuffer(params.get("BASE_URL"));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd+HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        String endTime = format.format(calendar.getTime());
        sb.append("&enddata=").append(endTime);
        calendar.add(Calendar.SECOND, 1);
        endTime = format.format(calendar.getTime());
        if (StringUtils.isBlank(params.get("LAST_END_TIME"))) {
            calendar.add(Calendar.MINUTE, -5);
            sb.append("&Data=").append(format.format(calendar.getTime()));
        } else {
            sb.append("&Data=").append(params.get("LAST_END_TIME"));
        }
        params.put("LAST_END_TIME", endTime);
        return sb.toString();
    }

}
