package com.kthw.spider.vehicle.spider;

import com.kthw.spider.common.SpiderProperties;
import com.kthw.spider.common.db.DBLink;
import com.kthw.spider.main.KTSpider;
import com.kthw.spider.vehicle.pipeline.VFDSPipeline;
import com.kthw.spider.vehicle.processor.VFDSPageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Spider;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by YFZX-WB on 2016/12/19.
 */
public class VFDSpider extends KTSpider {

    private static final Logger LOG = LoggerFactory.getLogger(VFDSpider.class);

    public static final String FILE_PATH = "vfds.properties";

    public VFDSpider(DBLink dbLink) {
        super(dbLink);
    }

    public void run() {
        LOG.info("VFDSpider Begin");
        while(isRunning()) {
            try {
                Map<String, String> params = SpiderProperties.getAllProperties(FILE_PATH);
                process(params);
                Thread.sleep(Integer.parseInt(params.get("INTERVAL")) * 60 * 1000);
            } catch (Exception e) {
                LOG.error("VFDSpider run error, please check :", e);
                break;
            }
        }
        LOG.info("VFDSpider End");
    }

    public void process(Map<String, String> params) {
        String baseUrl = buildRequestUrl(params);
        Spider.create(new VFDSPageProcessor(params))
            .addUrl(baseUrl)
                .addPipeline(new VFDSPipeline(getDbLink()))
                    .thread(1).run();
    }

    private String buildRequestUrl(Map<String, String> params) {
        StringBuffer sb = new StringBuffer(params.get("BASE_URL"));
        sb.append("?station_j=").append(params.get("station_j"))
            .append("&station_d=").append(params.get("station_d"));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -5);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        params.put("E_DATE", format.format(calendar.getTime()));
        params.put("E_H", String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
        params.put("E_M", String.valueOf(calendar.get(Calendar.MINUTE)));
        sb.append("&E_DATE=").append(params.get("E_DATE"))
            .append("&E_H=").append(params.get("E_H"))
                .append("&E_M=").append(params.get("E_M"));
        Date beginTime = parseRecordTime(params.get("LAST_END_TIME"));
        if (beginTime == null) {
            calendar.add(Calendar.MINUTE, -5);
        } else {
            calendar.setTime(beginTime);
        }
        sb.append("&B_DATE=").append(format.format(calendar.getTime()))
            .append("&B_H=").append(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)))
                .append("&B_M=").append(String.valueOf(calendar.get(Calendar.MINUTE)));
        return sb.toString();
    }

    private Date parseRecordTime(String recordTime) {
        Date date = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            date = format.parse(recordTime);
        } catch (ParseException e) {
            LOG.info("parse date error!");
        }
        return date;
    }

}
