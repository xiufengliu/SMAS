package ca.uwaterloo.iss4e.databases.Impl;

import ca.uwaterloo.iss4e.common.SMASException;
import ca.uwaterloo.iss4e.common.Utils;
import ca.uwaterloo.iss4e.databases.AccountDAO;
import ca.uwaterloo.iss4e.databases.DAOUtils;
import ca.uwaterloo.iss4e.dto.Account;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;

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

public class AccountDAOImpl implements AccountDAO {

    @Override
    public void read(int offset, JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            PreparedStatement pstmt = dbConn
                    .prepareStatement("SELECT  A.userid, " +
                            "A.firstname," +
                            "A.lastname," +
                            "A.username, " +
                            "B.name as rolename," +
                            "A.email, " +
                            "A.powermeters, " +
                            "A.watermeters " +
                            "FROM smas_user A LEFT JOIN smas_role B ON A.roleid=B.roleid " +
                            "ORDER BY A.userid");
            //pstmt.setInt(1, 20);
            //pstmt.setInt(2, offset);
            ResultSet rs = pstmt.executeQuery();
            JSONArray accounts = new JSONArray();
            while (rs.next()) {
                JSONObject account = new JSONObject();
                account.put("userID", rs.getInt("userid"));
                account.put("username", Utils.mapNull2Empty(rs.getString("username")));
                account.put("fullname", Utils.mapNull2Empty(rs.getString("firstname") + " " + rs.getString("lastname")));
                account.put("rolename", Utils.mapNull2Empty(rs.getString("rolename")));
                account.put("email", Utils.mapNull2Empty(rs.getString("email")));
                Array pmeterArray = rs.getArray("powermeters");
                if (pmeterArray != null) {
                    Integer[] powerMeterIDs = (Integer[]) pmeterArray.getArray();
                    int[] pMIDs = new int[powerMeterIDs.length];
                    for (int i = 0; i < powerMeterIDs.length; ++i) {
                        pMIDs[i] = powerMeterIDs[i].intValue();
                    }
                    account.put("powerMeterIDs", pMIDs);
                } else {
                    account.put("powerMeterIDs", new int[0]);
                }

                Array wmeterArray = rs.getArray("watermeters");
                if (wmeterArray != null) {
                    Integer[] waterMeterIDs = (Integer[]) (wmeterArray.getArray());
                    int[] wMIDs = new int[waterMeterIDs.length];
                    for (int i = 0; i < waterMeterIDs.length; ++i) {
                        wMIDs[i] = waterMeterIDs[i].intValue();
                    }
                    account.put("waterMeterIDs", wMIDs);
                } else {
                    account.put("waterMeterIDs", new int[0]);
                }
                accounts.put(account);
            }
            out.put("accounts", accounts);
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }


    @Override
    public void create(Account account, JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            PreparedStatement pstmt = dbConn
                    .prepareStatement("INSERT INTO smas_user(" +
                            "userid," +
                            "firstname," +
                            "lastname," +
                            "username," +
                            "password," +
                            "email" +
                            ") VALUES (nextval('seq_smas_user_userid'), ?, ?, ?, ?, ?)");
            int idx = 0;
            pstmt.setString(++idx, account.getFirstName());
            pstmt.setString(++idx, account.getLastName());
            pstmt.setString(++idx, account.getUsername());
            pstmt.setString(++idx, account.getPassword());
            pstmt.setString(++idx, account.getEmail());
            pstmt.execute();
            pstmt.close();
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }

    @Override
    public void delete(String[] userIDs, JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            StringBuffer sqlBuf = new StringBuffer("DELETE FROM smas_user WHERE userid IN (");
            for (int i = 0; i < userIDs.length; ++i) {
                sqlBuf.append("?");
                if (i < userIDs.length - 1) {
                    sqlBuf.append(",");
                }
            }
            sqlBuf.append(")");

            dbConn = DAOUtils.getDBConnection();
            PreparedStatement pstmt = dbConn.prepareStatement(sqlBuf.toString());
            for (int i = 0; i < userIDs.length; ++i) {
                pstmt.setInt(i + 1, Integer.parseInt(userIDs[i]));
            }
            pstmt.execute();
            pstmt.close();
            out.put("userIDs", userIDs);
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }

    @Override
    public void update(Account account, JSONObject out) throws SMASException {

    }

    @Override
    public int count(int userID, JSONObject out) throws SMASException {
        return 0;
    }
}
