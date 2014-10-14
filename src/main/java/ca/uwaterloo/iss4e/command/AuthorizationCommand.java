package ca.uwaterloo.iss4e.command;

import ca.uwaterloo.iss4e.common.SMASException;
import ca.uwaterloo.iss4e.databases.Impl.UserDAOImpl;
import ca.uwaterloo.iss4e.databases.UserDAO;
import ca.uwaterloo.iss4e.databases.Impl.MessageDAOImpl;
import ca.uwaterloo.iss4e.databases.MessageDAO;
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
public class AuthorizationCommand implements Command{
    private static final Logger log = Logger.getLogger(AuthorizationCommand.class.getName());
    private String customerPage;
    private String loginPage;
    private String utilityPage;
    private String consultPage;
    UserDAO dao = new UserDAOImpl();
    MessageDAO msgDao = new MessageDAOImpl();
    public AuthorizationCommand(String[] pages) {
        this.loginPage = pages[0];
        this.utilityPage = pages[1];
        this.consultPage = pages[2];
        this.customerPage = pages[3];
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
                request.getRequestDispatcher(userInfo.getRoleID()== 1? this.customerPage : (userInfo.getRoleID()==2?this.consultPage:this.utilityPage))
                        .forward(request, response);
            } else {
                throw new SMASException("Cannot find your user ID");
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

    public void logout(ServletContext ctx, HttpServletRequest request, HttpServletResponse response, JSONObject out)
            throws IOException, ServletException, SMASException {
        request.getSession().removeAttribute("userinfo");
        request.getRequestDispatcher(this.loginPage).forward(request, response);
    }
}
