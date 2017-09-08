package com.kthw.spider.pipeline;

import com.kthw.spider.common.db.DBLink;
import com.kthw.spider.model.TPDSBean;
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
 * Created by YFZX-WB on 2016/9/12.
 */
public class TPDSPipeline extends KTPipeline {

    private static final Logger LOG = LoggerFactory.getLogger(TPDSPipeline.class);

    private DBLink dbLink;

    public TPDSPipeline(DBLink dbLink) {
        this.dbLink = dbLink;
    }

    public void process(ResultItems resultItems, Task task) {
        if (dbLink != null) {
            List<TPDSBean> tpDataList = (List<TPDSBean>) resultItems.get("TPDataList");
            if (CollectionUtils.isNotEmpty(tpDataList)) {
                Connection conn = dbLink.open();
                PreparedStatement ps = null;
                String sql = "INSERT INTO VEH_FAULT (FAULT_ID, DEV_ID, VEH_ID, VEH_TYPE, C11, C12, LJ_FLAG, SITE_NAME, TCZ_MC, PASS_TIME, "
                        + "TRAIN_ID, FAULT_DESC, FAULT_LEVEL, DETECT_TIME, DEV_TYPE, VEH_LW)"
                        + "VALUES (SEQ_VEH_FAULT.nextval, ?, ?, ?, ?, ?, ?, ?, ?, to_date(?, 'yyyy-mm-dd hh24:mi:ss'), ?, ?, ?, to_date(?, 'yyyy-mm-dd hh24:mi:ss'), ?, ?)";
                try {
                    // conn.setAutoCommit(false);
                    ps = conn.prepareStatement(sql);
                    for (TPDSBean bean: tpDataList) {
                        int devId = queryDevId(bean.getStation(), "P", conn);
                        ps.setInt(1, devId);
                        ps.setString(2, bean.getVehicleId());
                        ps.setString(3, bean.getVehicleType());
                        ps.setString(4, bean.getAeiPos());
                        ps.setString(5, bean.getWheelOrder());
                        ps.setInt(6, devId == 0 ? 1 : 0);
                        ps.setString(7, bean.getStation());
                        ps.setString(8, devId == 0 ? bean.getStation() : null);
                        ps.setString(9, bean.getPassTime());
                        ps.setString(10, bean.getTrainId());
                        ps.setString(11, bean.getFaultDesc());
                        int faultLevel = Integer.parseInt(bean.getFaultDesc().replace("当量", ""));
                        if (faultLevel <= 20) {
                            faultLevel = 3;
                        } else if (faultLevel >= 23) {
                            faultLevel = 1;
                        } else {
                            faultLevel = 2;
                        }
                        ps.setInt(12, faultLevel);
                        ps.setString(13, bean.getDetectTime());
                        ps.setString(14, "P");
                        ps.setInt(15, Integer.parseInt(bean.getOrderCar()));
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
