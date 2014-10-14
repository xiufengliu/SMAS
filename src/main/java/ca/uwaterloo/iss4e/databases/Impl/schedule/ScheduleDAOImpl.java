package ca.uwaterloo.iss4e.databases.Impl.schedule;

import ca.uwaterloo.iss4e.common.RandomStringUtils;
import ca.uwaterloo.iss4e.common.SMASException;
import ca.uwaterloo.iss4e.common.Utils;
import ca.uwaterloo.iss4e.databases.DAOUtils;
import ca.uwaterloo.iss4e.databases.ScheduleDAO;
import ca.uwaterloo.iss4e.dto.Rule;
import org.quartz.*;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;

/**
 * Copyright (c) 2014 Xiufeng Liu ( xiufeng.liu@uwaterloo.ca )
 * <p/>
 * This file is free software: you may copy, redistribute and/or modify it
 * under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 * <p/>
 * This file is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 */
public class ScheduleDAOImpl implements ScheduleDAO {

    @Override
    public Map<JobDetail, Set<? extends Trigger>> createJobForRule(Rule rule) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            Map<JobDetail, Set<? extends Trigger>> jobProfiles = new HashMap<JobDetail, Set<? extends Trigger>>();
            int ruleID = rule.getRuleID();
            int creatorID = rule.getCreatorID();
            String sql = "SELECT " +
                    "smas_feedback_rule.typeid as ruleType," +
                    "smas_feedback_rule.name AS rulename," +
                    "smas_feedback_ruletype.outputtemplate AS template," +
                    "parameters," +
                    "message," +
                    "repeatinterval," +
                    "nextstarttime," +
                    "recvids " +
                    "FROM smas_feedback_rule, smas_feedback_ruletype " +
                    "WHERE smas_feedback_rule.typeid=smas_feedback_ruletype.typeid AND ruleid=? AND creatorid=?";
            PreparedStatement pstmt = dbConn.prepareStatement(sql);
            pstmt.setInt(1, ruleID);
            pstmt.setInt(2, creatorID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Class jobClass;
                switch (rs.getInt("ruleType")) {
                    case ScheduleType.DAILY_POWER_USAGE:
                        jobClass = DailyPowerUsageJob.class;
                        break;
                    default:
                        throw new SMASException("Cannot find the schedule type");
                }
                JobDetail jobDetail = newJob(jobClass)
                        .withIdentity("Job-" + ruleID, Scheduler.DEFAULT_GROUP)
                        .build();
                jobDetail.getJobDataMap().put("ruleID", ruleID);
                jobDetail.getJobDataMap().put("ruleName", rs.getString("ruleName"));
                jobDetail.getJobDataMap().put("parameters", (String[]) rs.getArray("parameters").getArray());
                jobDetail.getJobDataMap().put("senderID", creatorID);
                jobDetail.getJobDataMap().put("template", rs.getString("template"));
                jobDetail.getJobDataMap().put("recvIDs", Utils.toPrimitiveArray((Integer[]) rs.getArray("recvids").getArray()));
                jobDetail.getJobDataMap().put("message", rs.getString("message"));

                //////////////
                Trigger trigger = newTrigger()
                        .withIdentity(triggerKey("Trigger-" + ruleID, Scheduler.DEFAULT_GROUP))
                        .startAt(rs.getTimestamp("nextstarttime"))
                        .withSchedule(
                                simpleSchedule().
                                        withRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY).
                                        withIntervalInHours(rs.getInt("repeatinterval")).withMisfireHandlingInstructionIgnoreMisfires()
                                //withMisfireHandlingInstructionFireNow()
                        )
                        .build();
                Set<Trigger> triggers = new HashSet<Trigger>();
                triggers.add(trigger);
                jobProfiles.put(jobDetail, triggers);
            }
            return jobProfiles;
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }
}
