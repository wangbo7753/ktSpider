package com.kthw.spider.pipeline;

import com.kthw.spider.common.db.DBLink;
import com.kthw.spider.model.TFDSBean;
import com.kthw.spider.model.TFDSFaultRandom;
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
 * Created by YFZX-WB on 2016/9/8.
 */
public class TFDSPipeline extends KTPipeline {

    private static final Logger LOG = LoggerFactory.getLogger(TFDSPipeline.class);

    private DBLink dbLink;

    public TFDSPipeline(DBLink dbLink) {
        super();
        this.dbLink = dbLink;
    }

    public void process(ResultItems resultItems, Task task) {
        if (dbLink != null) {
            List<TFDSBean> tfDataList = (List<TFDSBean>) resultItems.get("TFDataList");
            if (CollectionUtils.isNotEmpty(tfDataList)) {
                Connection conn = dbLink.open();
                PreparedStatement ps = null;
                String sql = "INSERT INTO VEH_FAULT (FAULT_ID, DEV_ID, VEH_ID, PASS_TIME, TRAIN_ID, FAULT_DESC, "
                    + "DETECTOR_NAME, CONFIRMER_NAME, VEH_LW, VEH_TYPE, LJ_FLAG, SITE_NAME, TCZ_MC, DEV_TYPE, FAULT_POS, FAULT_LEVEL)"
                    + "VALUES (SEQ_VEH_FAULT.nextval, ?, ?, to_date(?, 'yyyy-mm-dd hh24:mi:ss'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try {
                    conn.setAutoCommit(false);
                    ps = conn.prepareStatement(sql);
                    for (TFDSBean bean: tfDataList) {
                        int devId = queryDevId(bean.getCrossAddr(), "F", conn);
                        ps.setInt(1, devId);
                        ps.setString(2, bean.getCarNum());
                        ps.setString(3, bean.getCrossDate());
                        ps.setString(4, bean.getTrainNum());
                        ps.setString(5, bean.getFaultName());
                        ps.setString(6, bean.getRailGuard());
                        ps.setString(7, bean.getFaultConfirmor());
                        ps.setInt(8, bean.getGroupDigit() != null ? Integer.parseInt(bean.getGroupDigit()) : null);
                        ps.setString(9, bean.getTrainModel());
                        ps.setInt(10, devId == 0 ? 1 : 0);
                        ps.setString(11, bean.getCrossAddr());
                        ps.setString(12, devId == 0 ? bean.getCrossAddr() : null);
                        ps.setString(13, "F");
                        ps.setString(14, bean.getDetailUrl().split("\\?")[1]);
                        ps.setInt(15, TFDSFaultRandom.getFaultLevel(bean.getFaultName()));
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
