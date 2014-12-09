package ca.uwaterloo.iss4e.command;

import ca.uwaterloo.iss4e.common.Constant;
import ca.uwaterloo.iss4e.common.SMASException;
import ca.uwaterloo.iss4e.common.Utils;
import ca.uwaterloo.iss4e.databases.AccountDAO;
import ca.uwaterloo.iss4e.databases.Impl.AccountDAOImpl;
import ca.uwaterloo.iss4e.databases.Impl.MessageDAOImpl;
import ca.uwaterloo.iss4e.databases.Impl.UserDAOImpl;
import ca.uwaterloo.iss4e.databases.MessageDAO;
import ca.uwaterloo.iss4e.databases.UserDAO;
import ca.uwaterloo.iss4e.dto.Account;
import ca.uwaterloo.iss4e.dto.UserInfo;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

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


public class AccountManagementCommand implements Command {
    private static final Logger log = Logger.getLogger(AccountManagementCommand.class.getName());

    AccountDAO dao = new AccountDAOImpl();

    public void read(ServletContext ctx, HttpServletRequest request, HttpServletResponse response, JSONObject out) throws ServletException, IOException, SMASException {
        int offset = Integer.parseInt(request.getParameter("offset"));
        dao.read(0, out);
    }

    public void delete(ServletContext ctx, HttpServletRequest request, HttpServletResponse response, JSONObject out) throws ServletException, IOException, SMASException {
        String userIDStr = request.getParameter("userIDs");
        String[] userIDArray = Utils.splitToArray(userIDStr, ",", true);
        if (userIDArray.length > 0) {
            dao.delete(userIDArray, out);
        } else {
            throw new SMASException("Please select the accounts to be deleted!");
        }
    }

    public void edit(ServletContext ctx, HttpServletRequest request, HttpServletResponse response, JSONObject out) throws ServletException, IOException, SMASException {
        String userIDStr = request.getParameter("userIDs");
        String[] userIDArray = Utils.splitToArray(userIDStr, ",", true);
        if (userIDArray.length == 0) {
            throw new SMASException("Please select the account to be edited!");
        } else if (userIDArray.length > 1) {
            throw new SMASException("Only one account can be edited at a time!");
        } else {
            dao.readAccountForEdit(Integer.parseInt(userIDArray[0]), out);
        }
    }

    public void save(ServletContext ctx, HttpServletRequest request, HttpServletResponse response, JSONObject out) throws ServletException, IOException, SMASException {
        String userIDStr = request.getParameter("userIDs");
        if (Utils.isNumeric(userIDStr)) {
            Account account = new Account();
            account.setUserID(Integer.parseInt(userIDStr));
            account.setUsername(request.getParameter("username"));
            account.setPassword(request.getParameter("password"));
            account.setFirstName(request.getParameter("firstname"));
            account.setLastName(request.getParameter("lastname"));
            account.setEmail(request.getParameter("email"));
            account.setRoleID(Integer.parseInt(request.getParameter("roleid")));
            account.setRoleName(request.getParameter("rolename"));
            account.setPowerMeterIDs(new int[]{Integer.parseInt(request.getParameter("powerMeterID"))});
            account.setWaterMeterIDs(new int[]{Integer.parseInt(request.getParameter("waterMeterID"))});
            dao.update(account, out);
        } else {
            throw new SMASException("Please select the account to be edited!");
        }
    }
}
