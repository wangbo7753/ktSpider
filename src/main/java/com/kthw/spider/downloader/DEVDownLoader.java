package com.kthw.spider.downloader;

import com.kthw.spider.common.db.DBLink;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.HttpClientDownloader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DEVDownLoader extends HttpClientDownloader {

    private static final Logger LOG = LoggerFactory.getLogger(KTDownLoader.class);

    private DBLink dbLink;

    private String devType;

    private String exclude;

    public DEVDownLoader(DBLink dbLink) {
        this.dbLink = dbLink;
    }
    
    public DEVDownLoader(DBLink dbLink, String devType) {
        this.dbLink = dbLink;
        this.devType = devType;
    }

    public DEVDownLoader(DBLink dbLink, String devType, String exclude) {
        this.dbLink = dbLink;
        this.devType = devType;
        this.exclude = exclude;
    }

    @Override
    public Page download(Request request, Task task) {
        long start = System.currentTimeMillis();
        Page page = super.download(request, task);
        long end = System.currentTimeMillis();
        statisticResponse(request, page, end - start);
        return page;
    }

    private void statisticResponse(Request request, Page page, long resTime) {
        if (StringUtils.isNotBlank(exclude) && page.getUrl().get().indexOf(exclude) != -1) {
            return;
        }
        if (dbLink != null) {
            Connection conn = dbLink.open();
            PreparedStatement ps = null;
            String sql = "INSERT INTO VEH_FAULT_STATISTIC (STATISTIC_ID, DEV_TYPE, DATA_SIZE, RESPONSE_TIME) "
                + "VALUES (SEQ_VEH_FAULT_STATISTIC.nextval, ?, ?, ?)";
            try {
                ps = conn.prepareStatement(sql);
                ps.setString(1, devType);
                ps.setInt(2, page != null ? page.getRawText().getBytes().length : 0);
                ps.setInt(3, (int) resTime);
                ps.executeUpdate();
            } catch (SQLException e) {
                LOG.error("statistic page " + request.getUrl() + " error", e);
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                } catch (SQLException e) {
                    LOG.error("statistic page " + request.getUrl() + " close error", e);
                }
                dbLink.close(conn);
            }
        }
    }

}
