package ca.uwaterloo.iss4e.command;

import ca.uwaterloo.iss4e.common.SMASException;
import ca.uwaterloo.iss4e.databases.Impl.UserDAOImpl;
import ca.uwaterloo.iss4e.databases.UserDAO;
import ca.uwaterloo.iss4e.dto.UserInfo;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
public class UserCommand implements Command {
    private static final Logger log = Logger.getLogger(UserCommand.class.getName());
    UserDAO dao = new UserDAOImpl();

    public void create(ServletContext ctx, HttpServletRequest request, HttpServletResponse response, JSONObject out) throws SMASException {
        try {
            UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userinfo");
        } catch (Exception e) {
            throw new SMASException(e);
        }
    }

    public void read(ServletContext ctx, HttpServletRequest request, HttpServletResponse response, JSONObject out) throws SMASException {
        try {
            dao.readUsernames(out);
        } catch (Exception e) {
            throw new SMASException(e);
        }
    }

    public void update(ServletContext ctx, HttpServletRequest request, HttpServletResponse response, JSONObject out) throws SMASException {
        try {
            UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userinfo");
        } catch (Exception e) {
            throw new SMASException(e);
        }
    }

    public void delete(ServletContext ctx, HttpServletRequest request, HttpServletResponse response, JSONObject out) throws SMASException {
        try {
            UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userinfo");
        } catch (Exception e) {
            throw new SMASException(e);
        }
    }

}
