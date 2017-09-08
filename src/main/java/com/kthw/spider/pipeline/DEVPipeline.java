package com.kthw.spider.pipeline;

import com.kthw.spider.common.db.DBLink;
import com.kthw.spider.model.DEVBean;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DEVPipeline extends KTPipeline {

    private static final Logger LOG = LoggerFactory.getLogger(TPDSPipeline.class);

    private DBLink dbLink;

    public DEVPipeline(DBLink dbLink) {
        this.dbLink = dbLink;
    }

    public void process(ResultItems resultItems, Task task) {
        if (dbLink != null) {
            List<DEVBean> dataList = (List<DEVBean>) resultItems.get("DataList");
            if (CollectionUtils.isNotEmpty(dataList)) {
                Connection conn = dbLink.open();
                PreparedStatement ps = null;
                String sql = "INSERT INTO DEV_FAULT (FAULT_ID, DEV_ID, FAULT_DESC, DETECT_TIME, DETECTOR,ENABLED)"
                        + "VALUES (SEQ_DEV_FAULT.nextval, ?, ?,to_date(?, 'yyyy-mm-dd hh24:mi:ss'), ?,1)";
                try {
                    // conn.setAutoCommit(false);
                    ps = conn.prepareStatement(sql);
                    for (DEVBean bean: dataList) {
                        int devId = queryDevId(bean.getSiteName(), bean.getDevType(), conn);
                        ps.setInt(1, devId);
                        ps.setString(2, bean.getFaultDesc());
                        ps.setString(3, bean.getLastPassTime());
                        ps.setString(4, "5tweb");
                        ps.execute();
                    }
                    // ps.executeBatch();
                    // conn.commit();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (ps != null) {
                            ps.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    dbLink.close(conn);
                }
            }
        } else {
            LOG.error("DBLink is null, please shutdown and check!");
        }
    }

}
