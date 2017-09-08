package com.kthw.spider.pipeline;

import com.kthw.spider.common.db.DBLink;
import com.kthw.spider.model.TVDSBean;
import com.kthw.spider.model.TVDSFaultEnum;
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
 * Created by YFZX-WB on 2016/10/8.
 */
public class TVDSPipeline extends KTPipeline {

    private static final Logger LOG = LoggerFactory.getLogger(TVDSPipeline.class);

    private DBLink dbLink;

    private boolean check;

    public TVDSPipeline(DBLink dbLink, Map<String, String> params) {
        super();
        this.dbLink = dbLink;
        this.check = "1".equals(params.get("CHECK_VEH_OWNER"));
    }

    public void process(ResultItems resultItems, Task task) {
        if (dbLink != null) {
            List<TVDSBean> tvDataList = (List<TVDSBean>) resultItems.get("TVDataList");
            if (CollectionUtils.isNotEmpty(tvDataList)) {
                Connection conn = dbLink.open();
                Set<String> vehSet = null;
                if (check) {
                    vehSet = queryVehIdByType(1, conn);
                }
                PreparedStatement ps = null;
                String sql = "INSERT INTO VEH_FAULT (FAULT_ID, DEV_ID, TRAIN_ID, VEH_ID, FAULT_POS, PASS_TIME, VEH_LW, "
                        + "FAULT_TYPENAME, FAULT_DESC, DETECTOR_NAME, LJ_FLAG, SITE_NAME, TCZ_MC, DEV_TYPE, C11, VEH_NUMBER, FAULT_LEVEL)"
                        + "VALUES (SEQ_VEH_FAULT.nextval, ?, ?, ?, ?, to_date(?, 'yyyymmddhh24miss'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try {
                    conn.setAutoCommit(false);
                    ps = conn.prepareStatement(sql);
                    for (TVDSBean bean: tvDataList) {
                        if (check && !vehSet.contains(bean.getSCarNo())) {
                            continue;
                        }
                        int devId = queryDevId(bean.getSProbeStationName(), "V", conn);
                        ps.setInt(1, devId);
                        ps.setString(2, bean.getSTrainNo());
                        ps.setString(3, bean.getSCarNo());
                        ps.setString(4, bean.getSRcdsysTypeName());
                        ps.setString(5, bean.getSPassTime());
                        ps.setInt(6, Integer.parseInt(bean.getNCarSeq()));
                        ps.setString(7, bean.getSDisposalName());
                        ps.setString(8, bean.getSFaultCategoryName() + " " + bean.getSAnalystRemark());
                        ps.setString(9, bean.getSAnalystName());
                        ps.setInt(10, devId == 0 ? 1 : 0);
                        ps.setString(11, bean.getSProbeStationName());
                        ps.setString(12, devId == 0 ? bean.getSProbeStationName() : null);
                        ps.setString(13, "V");
                        ps.setString(14, "tvds/" + bean.getSFailureWarnId());
                        ps.setInt(15, Integer.parseInt(bean.getNMarshallingCount()));
                        ps.setInt(16, TVDSFaultEnum.getFaultLevel(bean.getSDisposalName()));
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
