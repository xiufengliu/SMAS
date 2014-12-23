package ca.uwaterloo.iss4e.command;

import ca.uwaterloo.iss4e.common.Constant;
import ca.uwaterloo.iss4e.common.SMASException;
import ca.uwaterloo.iss4e.common.Utils;
import ca.uwaterloo.iss4e.databases.AccountDAO;
import ca.uwaterloo.iss4e.databases.Impl.AccountDAOImpl;
import ca.uwaterloo.iss4e.databases.Impl.UserDAOImpl;
import ca.uwaterloo.iss4e.databases.UserDAO;
import ca.uwaterloo.iss4e.databases.Impl.MessageDAOImpl;
import ca.uwaterloo.iss4e.databases.MessageDAO;
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
public class AuthorizationCommand implements Command {
    private static final Logger log = Logger.getLogger(AuthorizationCommand.class.getName());
    private String customerPage;
    private String loginPage;
    private String utilityPage;
    private String consultPage;
    private String registrationPage;
    private String registrationSuccessPage;
    UserDAO dao = new UserDAOImpl();
    MessageDAO msgDao = new MessageDAOImpl();
    AccountDAO accountDAO = new AccountDAOImpl();

    public AuthorizationCommand(String[] pages) {
        this.loginPage = pages[0];
        this.utilityPage = pages[1];
        this.consultPage = pages[2];
        this.customerPage = pages[3];
        this.registrationPage = pages[4];
        this.registrationSuccessPage = pages[5];
    }

    public void login(ServletContext ctx, HttpServletRequest request, HttpServletResponse response, JSONObject out) {
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            UserInfo userInfo = dao.read(username, password);
            if (userInfo != null) {
                request.getSession().setAttribute("userinfo", userInfo);
                request.setAttribute("userid", userInfo.getUserid());
                request.setAttribute("msgcount", msgDao.count(userInfo.getUserid(), out));
                if (userInfo.getRoleID() == Constant.ROLE_CUSTOMER) {
                    if ((userInfo.getPowerMeterIDs() == null && userInfo.getWaterMeterIDs() == null) ||
                            ((userInfo.getPowerMeterIDs().length == 1 && userInfo.getPowerMeterIDs()[0] == -1) &&
                                    (userInfo.getWaterMeterIDs().length == 1 && userInfo.getWaterMeterIDs()[0] == -1)
                            )) {
                        throw new SMASException("Please notify the system administrator (utility) to associate your account with meters!");
                    } else {
                        request.getRequestDispatcher(this.customerPage).forward(request, response);
                    }
                } else if (userInfo.getRoleID() == Constant.ROLE_CONSULTANT) {
                    request.getRequestDispatcher(this.consultPage).forward(request, response);
                } else if (userInfo.getRoleID() == Constant.ROLE_UTILITY) {
                    request.getRequestDispatcher(this.utilityPage).forward(request, response);
                } else {
                    throw new SMASException("Invalid role type");
                }
            } else {
                throw new SMASException("Incorrect username or password!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                request.setAttribute("errmsg", "<div class='alert alert-danger' data-dismiss='alert'>" + e.getMessage() + "</div>");
                request.getRequestDispatcher(this.loginPage).forward(request, response);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public void getRegistrationForm(ServletContext ctx, HttpServletRequest request, HttpServletResponse response, JSONObject out) throws ServletException, IOException {
        request.setAttribute("account", new Account());
        request.getRequestDispatcher(this.registrationPage).forward(request, response);
    }

    public void register(ServletContext ctx, HttpServletRequest request, HttpServletResponse response, JSONObject out) throws ServletException, IOException {
        Account account = new Account();
        StringBuffer buf = new StringBuffer();
        String username = request.getParameter("username");
        boolean success = true;
        buf.append("<div class='alert alert-danger' data-dismiss='alert'>");
        if (Utils.isEmpty(username)) {
            buf.append("<p>Username cannot be empty</p>");
            success = false;
        }
        String password = request.getParameter("password");
        String retypePassword = request.getParameter("retypepassword");
        if (Utils.isEmpty(password) && Utils.isEmpty(retypePassword)) {
            buf.append("<p>Password cannot be empty</p>");
            success = false;
        } else if (!password.equals(retypePassword)) {
            buf.append("<p>The password does not match</p>");
            success = false;
        }
        String firstName = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String email = request.getParameter("email");

        account.setUsername(username);
        account.setPassword(password);
        account.setRetypePassword(retypePassword);
        account.setFirstName(firstName);
        account.setLastName(lastname);
        account.setRoleID(Constant.ROLE_CUSTOMER);
        account.setEmail(email);

        try {
            if (success) {
                accountDAO.create(account, out);
            }
        } catch (Exception e) {
            buf.append(e.getMessage());
            success = false;
        }
        buf.append("</div>");
        account.setMessage(buf.toString());
        request.setAttribute("account", account);
        request.getRequestDispatcher(success ? this.registrationSuccessPage : this.registrationPage).forward(request, response);
    }


    public void logout(ServletContext ctx, HttpServletRequest request, HttpServletResponse response, JSONObject out)
            throws IOException, ServletException, SMASException {
        request.getSession().removeAttribute("userinfo");
        request.getRequestDispatcher(this.loginPage).forward(request, response);
    }
}
