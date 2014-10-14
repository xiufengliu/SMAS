package ca.uwaterloo.iss4e.command;

import ca.uwaterloo.iss4e.common.SMASException;
import ca.uwaterloo.iss4e.common.Utils;
import ca.uwaterloo.iss4e.databases.Impl.PowerDAOImpl;
import ca.uwaterloo.iss4e.databases.PowerDAO;
import ca.uwaterloo.iss4e.dto.UserInfo;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Date;
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
 */
public class PowerForcastCommand implements Command{
    private static final Logger log = Logger.getLogger(PowerLoadAnalysisCommand.class.getName());
    PowerDAO dao = new PowerDAOImpl();

    public void byHoltWinters(ServletContext ctx, HttpServletRequest request,
                              HttpServletResponse response, JSONObject out) throws SMASException, ParseException {
        int forcastLevel = Integer.parseInt(request.getParameter("level"));
        int meterid = Integer.parseInt(request.getParameter("value"));
        int forcastTime = Integer.parseInt(request.getParameter("forcasttime"));
        int timeUnit = Integer.parseInt(request.getParameter("forcasttimeunit"));
        dao.getForcast(meterid, forcastLevel, forcastTime, timeUnit, out);
    }


    public void byPARX(ServletContext ctx, HttpServletRequest request,
                       HttpServletResponse response, JSONObject out) throws SMASException, ParseException {
        int compareTarget = Integer.parseInt(request.getParameter("compareTarget"));
        System.out.println("compareTarget=" + compareTarget);
        if (compareTarget != -1) {

            int timeType = Integer.parseInt(request.getParameter("timeType"));
            String selectedIDsStr = request.getParameter("selectedIDs");
            if (compareTarget == 1) {
                int numOfSelectedIDs = Utils.countSpecialCharacter(selectedIDsStr, ',');
                if (selectedIDsStr.length() > 2) {
                    UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userinfo");
                    Date startDate = Utils.toSqlDate(request.getParameter("startDate"));
                    Date endDate = Utils.toSqlDate(request.getParameter("endDate"));
                    dao.getConsumptionByMeterID(userInfo.getUserid(), timeType, startDate, endDate, out);
                    dao.getAvgConsumption(timeType, selectedIDsStr, startDate, endDate, out);
                } else
                    throw new SMASException("Please pick the households on map!");
            } else {
                throw new SMASException("Under construction!");
            }
        } else {
            // viewEnergy(ctx, request, response, out);
        }
    }

    public void byARIMA(ServletContext ctx, HttpServletRequest request,
                        HttpServletResponse response, JSONObject out) throws SMASException, ParseException {

        int timeType = Integer.parseInt(request.getParameter("timeType"));
        int meterid = Integer.parseInt(request.getParameter("value"));
        Date startDate = Utils.toSqlDate(request.getParameter("startDate"));
        Date endDate = Utils.toSqlDate(request.getParameter("endDate"));
        dao.getConsumptionByMeterID(meterid, timeType, startDate, endDate, out);
    }

    public void byNeuralNetwork(ServletContext ctx, HttpServletRequest request,
                                HttpServletResponse response, JSONObject out) throws SMASException, ParseException {
         int timeType = Integer.parseInt(request.getParameter("timeType"));
        int meterid = Integer.parseInt(request.getParameter("value"));
        Date startDate = Utils.toSqlDate(request.getParameter("startDate"));
        Date endDate = Utils.toSqlDate(request.getParameter("endDate"));
        dao.getConsumptionByMeterID(meterid, timeType, startDate, endDate, out);
    }


}
