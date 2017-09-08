package com.kthw.spider.vehicle.pipeline;

import com.kthw.spider.common.db.DBLink;
import com.kthw.spider.pipeline.KTPipeline;
import com.kthw.spider.vehicle.model.VVDSBean;
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
 * Created by YFZX-WB on 2016/12/19.
 */
public class VVDSPipeline extends KTPipeline {

    private static final Logger LOG = LoggerFactory.getLogger(VVDSPipeline.class);

    private DBLink dbLink;

    public VVDSPipeline(DBLink dbLink) {
        super();
        this.dbLink = dbLink;
    }

    public void process(ResultItems resultItems, Task task) {
        if (dbLink != null) {
            List<VVDSBean> vvDataList = (List<VVDSBean>) resultItems.get("VVDataList");
            if (CollectionUtils.isNotEmpty(vvDataList)) {
                Connection conn = dbLink.open();
                PreparedStatement ps = null;
                String sql = "INSERT INTO TRAIN (ID, PASS_TIME, DEV_ID, SITE_NAME, TRAIN_ID, DEV_TYPE, BUREAU_ID, SPEED, "
                        + "VEH_NUMBER, VEH_DIRECT, C1, C11)"
                        + "VALUES (SEQ_TRAIN.nextval, to_date(?, 'yyyy-mm-dd hh24:mi:ss'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try {
                    conn.setAutoCommit(false);
                    ps = conn.prepareStatement(sql);
                    for (VVDSBean bean: vvDataList) {
                        ps.setString(1, bean.getSPassTime());
                        ps.setInt(2, queryDevId(bean.getSProbeStationName(), "V", conn));
                        ps.setString(3, bean.getSProbeStationName());
                        ps.setString(4, bean.getSTrainNo());
                        ps.setString(5, "V");
                        ps.setInt(6, queryBureauId(bean.getSBureaName(), conn));
                        ps.setInt(7, bean.getSAverageSpeed());
                        ps.setInt(8, bean.getNMarshallingCount());
                        ps.setInt(9, bean.getSRunningDirection().equals("D") ? 0 : 1);
                        ps.setInt(10, bean.getNAxleCount());
                        ps.setString(11, bean.getSWorkTypeName());
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
