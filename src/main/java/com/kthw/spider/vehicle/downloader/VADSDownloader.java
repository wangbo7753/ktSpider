package com.kthw.spider.vehicle.downloader;

import com.kthw.spider.common.TAUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.selector.PlainText;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by YFZX-WB on 2016/12/21.
 */
public class VADSDownloader extends HttpClientDownloader {

    private static final Logger LOG = LoggerFactory.getLogger(VADSDownloader.class);

    private static final String REQ_URL = "http://10.1.184.32/tads/(%s)/tgquery.aspx";

    private Map<String, String> params;

    private DateFormat df;

    public VADSDownloader(Map<String, String> params) {
        this.params = params;
        df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

    @Override
    public Page download(Request request, Task task) {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpClients.createDefault();
            if (StringUtils.isBlank(request.getUrl())) {
                String randomCode = TAUtils.getRandomCode();
                HttpGet httpGet = new HttpGet(params.get("BASE_URL").replace("randomCode", randomCode));
                httpClient.execute(httpGet);
                request.setUrl(String.format(REQ_URL, randomCode));
            }
            LOG.info("downloading page {}", request.getUrl());
            HttpPost httpPost = new HttpPost(request.getUrl());
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            StringEntity postParam = new StringEntity(buildPostParams(), "UTF-8");
            httpPost.setEntity(postParam);
            HttpResponse response = httpClient.execute(httpPost);
            Page page = new Page();
            page.setRawText(EntityUtils.toString(response.getEntity()));
            page.setUrl(new PlainText(request.getUrl()));
            page.setRequest(request);
            page.setStatusCode(response.getStatusLine().getStatusCode());
            return page;
        } catch (IOException e) {
            LOG.error("downloading VADS page data error: ", e);
            return null;
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                LOG.error("In VADSDownloader close httpClient error, {}", e);
            }
        }
    }

    private String buildPostParams() {
        StringBuffer sb = new StringBuffer();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -5);
        String lastEndTime = df.format(calendar.getTime());
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        sb.append("EndTime$dbDate=").append(format.format(calendar.getTime()))
            .append("&EndTime$txtHour=").append(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)))
                .append("&EndTime$txtMinute=").append(String.format("%02d", calendar.get(Calendar.MINUTE)));
        Date beginTime = parseRecordTime(params.get("LAST_END_TIME"));
        if (beginTime == null) {
            calendar.add(Calendar.MINUTE, -5);
        } else {
            calendar.setTime(beginTime);
            calendar.add(Calendar.MINUTE, 1);
        }
        sb.append("&StartTime$dbDate=").append(format.format(calendar.getTime()))
            .append("&StartTime$txtHour=").append(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)))
                .append("&StartTime$txtMinute=").append(String.format("%02d", calendar.get(Calendar.MINUTE)));
        params.put("LAST_END_TIME", lastEndTime);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!entry.getKey().equals("BASE_URL") && !entry.getKey().equals("INTERVAL") && !entry.getKey().equals("LAST_END_TIME")) {
                try {
                    sb.append("&").append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    private Date parseRecordTime(String recordTime) {
        Date date = null;
        try {
            date = df.parse(recordTime);
        } catch (ParseException e) {
            LOG.info("parse date error!");
        }
        return date;
    }

}
