package com.kthw.spider.pipeline;

import com.kthw.spider.common.db.DBLink;
import com.kthw.spider.model.TCDSBean;
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
 * Created by YFZX-WB on 2017/4/10.
 */
public class TCDSPipeline extends KTPipeline {

    private static final Logger LOG = LoggerFactory.getLogger(TCDSPipeline.class);

    private DBLink dbLink;

    public TCDSPipeline(DBLink dbLink) {
        super();
        this.dbLink = dbLink;
    }

    public void process(ResultItems resultItems, Task task) {
        if (dbLink != null) {
            List<TCDSBean> tcDataList = (List<TCDSBean>) resultItems.get("TCDataList");
            if (CollectionUtils.isNotEmpty(tcDataList)) {
                Connection conn = dbLink.open();
                PreparedStatement ps = null;
                String sql = "INSERT INTO VEH_FAULT (FAULT_ID, VEH_ID, DETECT_TIME, TRAIN_ID, FAULT_DESC, FAULT_LEVEL, "
                        + "VEH_LW, FAULT_TYPENAME, C11, C12, C13, DEV_TYPE, TRANSACT_FLAG, PASS_TIME, C15)"
                        + "VALUES (SEQ_VEH_FAULT.nextval, ?, to_date(?, 'yyyy-mm-dd hh24:mi:ss'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, to_date(?, 'yyyy-mm-dd hh24:mi:ss'), ?)";
                try {
                    conn.setAutoCommit(false);
                    ps = conn.prepareStatement(sql);
                    for (TCDSBean bean: tcDataList) {
                        ps.setString(1, bean.getCarNo());
                        ps.setString(2, bean.getDetectTime());
                        ps.setString(3, bean.getTrainNo());
                        ps.setString(4, bean.getFaultType() + "-" + bean.getFaultDesc());
                        ps.setInt(5, 1);
                        ps.setInt(6, Integer.parseInt(bean.getCarOrder()));
                        ps.setString(7, bean.getFaultType());
                        ps.setString(8, bean.getRunState());
                        ps.setString(9, bean.getState());
                        ps.setString(10, bean.getCarDepot());
                        ps.setString(11, "C");
                        ps.setInt(12, 4);
                        ps.setString(13, bean.getDetectTime());
                        ps.setString(14, bean.getMonitorNo());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                    conn.commit();
                } catch (SQLException e) {
                    e.printStackTrace();
                    try {
                        if (conn != null) {
                            conn.rollback();
                        }
                    } catch (SQLException e1) {
                        LOG.error("TCDS rollback error :", e1);
                    }
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
