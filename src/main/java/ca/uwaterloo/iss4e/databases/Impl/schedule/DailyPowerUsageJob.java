package ca.uwaterloo.iss4e.databases.Impl.schedule;

import ca.uwaterloo.iss4e.common.RandomStringUtils;
import ca.uwaterloo.iss4e.common.SMASException;
import ca.uwaterloo.iss4e.common.Utils;
import ca.uwaterloo.iss4e.databases.DAOUtils;
import org.quartz.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
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
@DisallowConcurrentExecution
public class DailyPowerUsageJob implements Job {

    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            JobDataMap dataMap = context.getJobDetail().getJobDataMap();
            int ruleID = dataMap.getInt("ruleID");
            int[] recvIDs = (int[]) dataMap.get("recvIDs");
            java.util.Date dateOfData = context.getNextFireTime();
            for (int i = 0; i < recvIDs.length; ++i) {
                PreparedStatement pstmt = dbConn.prepareStatement("SELECT powermeters FROM smas_user WHERE userid=?");
                pstmt.setInt(1, recvIDs[i]);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    Integer[] meterIDs = (Integer[]) rs.getArray("powermeters").getArray();
                    for (int j = 0; j < meterIDs.length; ++j) {
                        checkIfDailyUsageLargerThanNeighhors(dataMap, dateOfData , meterIDs[j].intValue(), recvIDs[i]);
                    }
                }
            }
            PreparedStatement updateStmt = dbConn.prepareStatement("UPDATE smas_feedback_rule SET nextstarttime=? WHERE ruleid=?");
            updateStmt.setTimestamp(1, new Timestamp(context.getNextFireTime().getTime()));
            updateStmt.setInt(2, ruleID);
            updateStmt.execute();
            updateStmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }


    private void checkIfDailyUsageLargerThanNeighhors(JobDataMap dataMap, java.util.Date dateOfData, int meterID, int recvID) throws SMASException {
        Connection dbConn = null;
        try {
            int ruleID = dataMap.getInt("ruleID");
            int senderID = dataMap.getInt("senderID");
            String[] parameters = (String[]) dataMap.get("parameters");
            String ruleName = dataMap.getString("ruleName");
            String template = dataMap.getString("template");
            String message = dataMap.getString("message");

            dbConn = DAOUtils.getDBConnection();
            double radius;
            double percentage = Double.parseDouble(parameters[0]);
            try {
                radius = Double.parseDouble(parameters[1]);
            } catch (Exception ee) {
                radius = 10.0;
            }
            int startHour = Integer.parseInt(parameters[2]);
            int endHour = Integer.parseInt(parameters[3]);

            PreparedStatement selectStmt = dbConn.prepareStatement("SELECT smas_feedback_powerdailyusage(?, ?, ?, ?)");
            if (startHour>endHour){
                endHour += 24;
            }
            Timestamp startTimeOfTimeseries = Utils.toTimestampOnedayBeforeWithHour(dateOfData.getTime(), startHour);
            Timestamp endTimeOfTimeseries = Utils.toTimestampOnedayBeforeWithHour(dateOfData.getTime(), endHour);
            int idx = 0;
            selectStmt.setInt(++idx, meterID);
            selectStmt.setDouble(++idx, radius);
            selectStmt.setTimestamp(++idx, startTimeOfTimeseries);
            selectStmt.setTimestamp(++idx, endTimeOfTimeseries);

            //System.out.println(String.format("SELECT smas_feedback_powerdailyusage(%d, %f, %s, %s)", meterID, radius, String.valueOf(startTimeOfTimeseries), String.valueOf(endTimeOfTimeseries)));
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                Object array = rs.getArray(1).getArray();
                double[] result = Utils.toPrimitiveArray((Double[]) array);
                if (result != null) {
                    double mine = result[0];
                    double neighavg = result[1];
                    double higher = mine - neighavg;

                    if (higher/neighavg*100>=percentage) {
                        PreparedStatement insertStmt = dbConn.prepareStatement("INSERT INTO smas_message VALUES(nextval('seq_smas_message_msgid'),?,?,?,?,?,?,?)");
                        idx = 0;
                        insertStmt.setInt(++idx, ruleID);
                        insertStmt.setInt(++idx, recvID);
                        insertStmt.setInt(++idx, senderID);
                        insertStmt.setTimestamp(++idx, new Timestamp(dateOfData.getTime()));//sendTime
                        insertStmt.setInt(++idx, 0); //Label
                        insertStmt.setString(++idx, String.format("Meter-%d: %s", meterID, ruleName)); //Title
                        insertStmt.setString(++idx, String.format(template,
                                                                    String.valueOf(startTimeOfTimeseries),
                                                                    String.valueOf(endTimeOfTimeseries),
                                                                    result[0],
                                                                    radius,
                                                                    result[1],
                                                                    (int) result[2],
                                                                    (int) result[3],
                                                                    result[4],
                                                                    (int) result[5],
                                                                    (int) result[6],
                                                                    message)); //Content
                        insertStmt.execute();
                        insertStmt.close();
                    }
                }
            }
            selectStmt.close();
        } catch (Exception e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }
}