package ca.uwaterloo.iss4e.command;

import ca.uwaterloo.iss4e.common.SMASException;
import ca.uwaterloo.iss4e.common.Utils;
import ca.uwaterloo.iss4e.databases.FeedbackDAO;
import ca.uwaterloo.iss4e.databases.Impl.FeedbackDAOImpl;
import ca.uwaterloo.iss4e.databases.Impl.MessageDAOImpl;
import ca.uwaterloo.iss4e.databases.MessageDAO;
import ca.uwaterloo.iss4e.dto.Message;
import ca.uwaterloo.iss4e.dto.Rule;
import ca.uwaterloo.iss4e.dto.UserInfo;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
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
 */public class MessageCommand implements Command{
    private static final Logger log = Logger.getLogger(MessageCommand.class.getName());
    MessageDAO dao = new MessageDAOImpl();

    public void read(ServletContext ctx, HttpServletRequest request,
                       HttpServletResponse response, JSONObject out) throws SMASException, ParseException {

        UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userinfo");
        Message message = new Message();
        message.setRecvID(userInfo.getUserid());
        dao.read(message, out);
    }

    public void create(ServletContext ctx, HttpServletRequest request,
                    HttpServletResponse response, JSONObject out) throws SMASException, ParseException {

        UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userinfo");


    }

    public void delete(ServletContext ctx, HttpServletRequest request,
                       HttpServletResponse response, JSONObject out) throws SMASException, ParseException {
        int ruleID = Integer.parseInt(request.getParameter("ruleID"));
        UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userinfo");

    }




    public void update(ServletContext ctx, HttpServletRequest request,
                       HttpServletResponse response, JSONObject out) throws SMASException, ParseException {

        int ruleID = Integer.parseInt(request.getParameter("ruleID"));
        UserInfo userInfo = (UserInfo)request.getSession().getAttribute("userinfo");

    }


}
