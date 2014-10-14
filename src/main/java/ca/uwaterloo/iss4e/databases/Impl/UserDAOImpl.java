package ca.uwaterloo.iss4e.databases.Impl;

import ca.uwaterloo.iss4e.common.SMASException;
import ca.uwaterloo.iss4e.common.Utils;
import ca.uwaterloo.iss4e.databases.UserDAO;
import ca.uwaterloo.iss4e.databases.DAOUtils;
import ca.uwaterloo.iss4e.dto.UserInfo;
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
public class UserDAOImpl implements UserDAO {

    @Override
    public UserInfo read(String username, String password) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            PreparedStatement pstmt = dbConn
                    .prepareStatement("SELECT userid, " +
                            "firstname," +
                            "lastname, " +
                            "agender," +
                            "birthday," +
                            "address," +
                            "roleID," +
                            "powermeters," +
                            "watermeters " +
                            "FROM smas_user " +
                            "WHERE username=? and password=?");
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                UserInfo userInfo = new UserInfo();
                userInfo.setUserid(rs.getInt("userid"));
                userInfo.setUsername(username);
                userInfo.setPassword(password);
                userInfo.setFirstname(rs.getString("firstname"));
                userInfo.setLastname(rs.getString("lastname"));
                userInfo.setAgender(rs.getInt("agender"));
                userInfo.setBirthdate(rs.getString("birthday"));
                userInfo.setAddress(rs.getString("address"));
                userInfo.setRoleID(rs.getInt("roleID"));
                Array powerMeterArr = rs.getArray("powermeters");
                if (powerMeterArr!=null) {
                    userInfo.setPowerMeterIDs(Utils.toPrimitiveArray((Integer[]) (powerMeterArr.getArray())));
                }
                Array waterMeterArr = rs.getArray("watermeters");
                if (waterMeterArr!=null) {
                    userInfo.setWaterMeterIDs(Utils.toPrimitiveArray((Integer[]) (waterMeterArr.getArray())));
                }
                return userInfo;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }

    @Override
    public void readUsernames(JSONObject out) throws SMASException {
        Connection dbConn = null;
        try {
            dbConn = DAOUtils.getDBConnection();
            PreparedStatement pstmt = dbConn
                    .prepareStatement("SELECT userid, username FROM smas_user WHERE roleid=1 ORDER BY 1");
            ResultSet rs = pstmt.executeQuery();
            JSONArray users = new JSONArray();
            while (rs.next()) {
                JSONObject user = new JSONObject();
                user.put("userID", rs.getInt("userid"));
                user.put("username", rs.getString("username"));
                users.put(user);
            }
            out.put("users", users);
        } catch (SQLException e) {
            throw new SMASException(e);
        } finally {
            DAOUtils.freeConnection(dbConn);
        }
    }

}

