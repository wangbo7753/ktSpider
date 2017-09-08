package com.kthw.spider.main;

import com.kthw.spider.common.SpiderProperties;
import com.kthw.spider.common.db.DBLink;
import com.kthw.spider.downloader.KTDownLoader;
import com.kthw.spider.param.TFDSRequestParam;
import com.kthw.spider.pipeline.TFDSImagePipeline;
import com.kthw.spider.pipeline.TFDSPipeline;
import com.kthw.spider.processor.TFDSPageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Spider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by YFZX-WB on 2016/9/7.
 */
public class TFDSpider extends KTSpider {

    private static final Logger LOG = LoggerFactory.getLogger(TFDSpider.class);

    public static final String FILE_PATH = "tfds.properties";

    public TFDSpider(DBLink dbLink) {
        super(dbLink);
    }

    public void run() {
        LOG.info("TFDSpider Begin");
        while(isRunning()) {
            try {
                Map<String, String> params = SpiderProperties.getAllProperties(FILE_PATH);
                process(params);
                Thread.sleep(Integer.parseInt(params.get("INTERVAL")) * 60 * 1000);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        LOG.info("TFDSpider End");
    }

    public void process(Map<String, String> params) {
        buildRequestParams(params);
        TFDSRequestParam initParam = new TFDSRequestParam(params);
        Spider.create(new TFDSPageProcessor(params))
            .addUrl(params.get("BASE_URL") + initParam.initRequestCode())
                .setDownloader(new KTDownLoader(getDbLink(), "F", "fault_img"))
                    .addPipeline(new TFDSPipeline(getDbLink()))
                        .addPipeline(new TFDSImagePipeline(params, getDbLink()))
                            .thread(1).run();
    }

    public void buildRequestParams(Map<String, String> params) {
        String recordTime = params.get("LAST_END_TIME");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -5);
        params.put("E_DATE", format.format(calendar.getTime()));
        params.put("E_H", formatHM(calendar.get(Calendar.HOUR_OF_DAY)));
        params.put("E_M", formatHM(calendar.get(Calendar.MINUTE)));
        Date beginTime = parseRecordTime(recordTime);
        if (beginTime == null) {
            calendar.add(Calendar.MINUTE, -5);
        } else {
            calendar.setTime(beginTime);
        }
        params.put("B_DATE", format.format(calendar.getTime()));
        params.put("B_H", formatHM(calendar.get(Calendar.HOUR_OF_DAY)));
        params.put("B_M", formatHM(calendar.get(Calendar.MINUTE)));
        LOG.info("Begin Time:" + params.get("B_DATE") + " " + params.get("B_H") + ":" + params.get("B_M"));
        LOG.info("End Time:" + params.get("E_DATE") + " " + params.get("E_H") + ":" + params.get("E_M"));
    }

    public Date parseRecordTime(String recordTime) {
        Date date = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            date = format.parse(recordTime);
        } catch (ParseException e) {
            LOG.info("parse date error!");
        }
        return date;
    }

    public String formatHM(int time) {
        return String.format("%02d", time);
    }

}
