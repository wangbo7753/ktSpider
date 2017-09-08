package com.kthw.spider.vehicle.pipeline;

import com.kthw.spider.common.db.DBLink;
import com.kthw.spider.pipeline.KTPipeline;
import com.kthw.spider.vehicle.model.VFDSBean;
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
public class VFDSPipeline extends KTPipeline {

    private static final Logger LOG = LoggerFactory.getLogger(VFDSPipeline.class);

    private DBLink dbLink;

    private static final String DOWN = "下行";

    public VFDSPipeline(DBLink dbLink) {
        super();
        this.dbLink = dbLink;
    }

    public void process(ResultItems resultItems, Task task) {
        if (dbLink != null) {
            List<VFDSBean> veDataList = (List<VFDSBean>) resultItems.get("VFDataList");
            if (CollectionUtils.isNotEmpty(veDataList)) {
                Connection conn = dbLink.open();
                PreparedStatement ps = null;
                String sql = "INSERT INTO TRAIN (ID, PASS_TIME, DEV_ID, SITE_NAME, TRAIN_ID, DEV_TYPE, BUREAU_ID, SPEED, "
                        + "VEH_NUMBER, TRAIN_TYPE, VEH_DIRECT)"
                        + "VALUES (SEQ_TRAIN.nextval, to_date(?, 'yyyy-mm-dd hh24:mi:ss'), ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try {
                    conn.setAutoCommit(false);
                    ps = conn.prepareStatement(sql);
                    for (VFDSBean bean: veDataList) {
                        ps.setString(1, bean.getPassTime());
                        ps.setInt(2, queryDevId(bean.getStationName(), "F", conn));
                        ps.setString(3, bean.getStationName());
                        ps.setString(4, bean.getTrainId());
                        ps.setString(5, "F");
                        ps.setInt(6, 7);
                        ps.setInt(7, Integer.parseInt(bean.getAvgSpeed()));
                        ps.setInt(8, Integer.parseInt(bean.getVehNumber()));
                        ps.setInt(9, queryTrainType(bean.getTrainType(), conn));
                        ps.setInt(10, DOWN.equals(bean.getTrainDirect()) ? 0 : 1);
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
