package com.kthw.spider.pipeline;

import com.kthw.spider.common.db.DBLink;
import com.kthw.spider.model.TADSBean;
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
 * Created by YFZX-WB on 2016/9/28.
 */
public class TADSPipeline extends KTPipeline {

    private static final Logger LOG = LoggerFactory.getLogger(TADSPipeline.class);

    private DBLink dbLink;

    public TADSPipeline(DBLink dbLink) {
        super();
        this.dbLink = dbLink;
    }

    public void process(ResultItems resultItems, Task task) {
        if (dbLink != null) {
            List<TADSBean> taDataList = (List<TADSBean>) resultItems.get("TADataList");
            if (CollectionUtils.isNotEmpty(taDataList)) {
                Connection conn = dbLink.open();
                PreparedStatement ps = null;
                String sql = "INSERT INTO VEH_FAULT (FAULT_ID, DEV_ID, VEH_TYPE, VEH_ID, PASS_TIME, C11, TRAIN_ID, "
                        + "FAULT_DESC, FAULT_LEVEL, VEH_LW, LJ_FLAG, SITE_NAME, TCZ_MC, DEV_TYPE, C12, C13)"
                        + "VALUES (SEQ_VEH_FAULT.nextval, ?, ?, ?, to_date(?, 'yyyy-mm-dd hh24:mi:ss'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try {
                    conn.setAutoCommit(false);
                    ps = conn.prepareStatement(sql);
                    for (TADSBean bean : taDataList) {
                        int devId = queryDevId(bean.getSite(), "A", conn);
                        ps.setInt(1, devId);
                        ps.setString(2, bean.getTrain_type());
                        ps.setString(3, bean.getVehicle_id());
                        ps.setString(4, bean.getPass_time());
                        ps.setString(5, bean.getAxle_number());
                        ps.setString(6, bean.getTrain_id());
                        ps.setString(7, bean.getFault_type());
                        ps.setString(8, bean.getFault_level());
                        ps.setString(9, bean.getVehicle_order());
                        ps.setInt(10, devId == 0 ? 1 : 0);
                        ps.setString(11, bean.getSite());
                        ps.setString(12, devId == 0 ? bean.getSite() : null);
                        ps.setString(13, "A");
                        ps.setString(14, bean.getAlarm_number());
                        ps.setString(15, bean.getLine());
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
