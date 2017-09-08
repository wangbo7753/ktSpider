package com.kthw.spider.pipeline;

import com.kthw.spider.common.db.DBLink;
import com.kthw.spider.model.TEDSBean;
import com.kthw.spider.model.TEDSFaultEnum;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by YFZX-WB on 2016/9/10.
 */
public class TEDSPipeline extends KTPipeline {

    private static final Logger LOG = LoggerFactory.getLogger(TEDSPipeline.class);

    private DBLink dbLink;

    private boolean check;

    public TEDSPipeline(DBLink dbLink, Map<String, String> params) {
        super();
        this.dbLink = dbLink;
        this.check = "1".equals(params.get("CHECK_VEH_OWNER"));
    }

    public void process(ResultItems resultItems, Task task) {
        if (dbLink != null) {
            List<TEDSBean> teDataList = (List<TEDSBean>) resultItems.get("TEDataList");
            if (CollectionUtils.isNotEmpty(teDataList)) {
                Connection conn = dbLink.open();
                Set<String> vehSet = null;
                if (check) {
                    vehSet = queryVehIdByType(3, conn);
                }
                PreparedStatement ps = null;
                String sql = "INSERT INTO VEH_FAULT (FAULT_ID, DEV_ID, VEH_TYPE, TRAIN_GROUPID, VEH_ID, PASS_TIME, FAULT_TYPENAME, "
                        + "FAULT_POS, FAULT_DESC, CONFIRMER_NAME, LJ_FLAG, SITE_NAME, TCZ_MC, DEV_TYPE, C11, FAULT_LEVEL)"
                        + "VALUES (SEQ_VEH_FAULT.nextval, ?, ?, ?, ?, to_date(?, 'yyyy-mm-dd hh24:mi:ss'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try {
                    conn.setAutoCommit(false);
                    ps = conn.prepareStatement(sql);
                    for (TEDSBean bean: teDataList) {
                        if (check && !vehSet.contains(bean.getTrainIdentityNum())) {
                            continue;
                        }
                        int devId = queryDevId(bean.getCheckStation(), "E", conn);
                        ps.setInt(1, devId);
                        ps.setString(2, bean.getTrainModel());
                        ps.setString(3, bean.getTrainUnitNum());
                        ps.setString(4, bean.getTrainIdentityNum());
                        ps.setString(5, bean.getCrossTime());
                        ps.setString(6, bean.getAlarmType());
                        ps.setString(7, bean.getAlarmParts());
                        ps.setString(8, bean.getAlarmName());
                        ps.setString(9, bean.getConfirmor());
                        ps.setInt(10, devId == 0 ? 1 : 0);
                        ps.setString(11, bean.getCheckStation());
                        ps.setString(12, devId == 0 ? bean.getCheckStation() : null);
                        ps.setString(13, "E");
                        ps.setString(14, "teds/" + bean.getManuAlertId());
                        ps.setInt(15, TEDSFaultEnum.getFaultLevel(bean.getAlarmType()));
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
