package com.kthw.spider.vehicle.pipeline;

import com.kthw.spider.common.db.DBLink;
import com.kthw.spider.pipeline.KTPipeline;
import com.kthw.spider.vehicle.model.VEDSBean;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by YFZX-WB on 2016/12/16.
 */
public class VEDSPipeline extends KTPipeline {

    private static final Logger LOG = LoggerFactory.getLogger(VEDSPipeline.class);

    private DBLink dbLink;

    public VEDSPipeline(DBLink dbLink) {
        super();
        this.dbLink = dbLink;
    }

    public void process(ResultItems resultItems, Task task) {
        if (dbLink != null) {
            List<VEDSBean> veDataList = (List<VEDSBean>) resultItems.get("VEDataList");
            if (CollectionUtils.isNotEmpty(veDataList)) {
                Connection conn = dbLink.open();
                PreparedStatement ps = null;
                String sql = "INSERT INTO TRAIN (ID, PASS_TIME, DEV_ID, SITE_NAME, TRAIN_ID, DEV_TYPE, BUREAU_ID, SPEED, "
                        + "VEH_NUMBER, TRAIN_GROUPID, VEH_TYPE, VEH_DIRECT)"
                        + "VALUES (SEQ_TRAIN.nextval, to_date(?, 'yyyy-mm-dd hh24:mi:ss'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try {
                    conn.setAutoCommit(false);
                    ps = conn.prepareStatement(sql);
                    for (VEDSBean bean: veDataList) {
                        ps.setString(1, bean.getSPasstime());
                        ps.setInt(2, queryDevId(bean.getSStationname(), "E", conn));
                        ps.setString(3, bean.getSStationname());
                        ps.setString(4, bean.getSTrainno());
                        ps.setString(5, "E");
                        ps.setInt(6, queryBureauId(bean.getSBureaname(), conn));
                        ps.setInt(7, bean.getSTagspeed());
                        ps.setInt(8, bean.getSMarshltype());
                        ps.setString(9, bean.getSTrainsetno());
                        ps.setString(10, bean.getSTrainsettype());
                        ps.setInt(11, bean.getCDirectionflag().equals("D") ? 0 : 1);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                    conn.commit();
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
