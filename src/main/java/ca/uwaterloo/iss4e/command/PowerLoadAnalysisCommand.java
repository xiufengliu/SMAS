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
 */public class PowerLoadAnalysisCommand implements Command{
    private static final Logger log = Logger.getLogger(PowerLoadAnalysisCommand.class.getName());
    PowerDAO dao = new PowerDAOImpl();

    public void viewEnergyLoad(ServletContext ctx, HttpServletRequest request,
                               HttpServletResponse response, JSONObject out) throws SMASException, ParseException {

        int level = Integer.parseInt(request.getParameter("level"));
        int timeLevel = Integer.parseInt(request.getParameter("timeLevel"));
        Date startDate = Utils.toSqlDate(request.getParameter("startDate"));
        Date endDate = Utils.toSqlDate(request.getParameter("endDate"));
        if (level == 1) {
            int value = Integer.parseInt(request.getParameter("value"));
            dao.getConsumptionByMeterID(value, timeLevel, startDate, endDate, out);
        } else if (level == 2) {
            String areaCode = request.getParameter("value");
            dao.getConsumptionByArea(areaCode, timeLevel, startDate, endDate, out);
        } else if (level == 3) {
            dao.getConsumptionByAll(timeLevel, startDate, endDate, out);
        }
    }


    public void viewMyEnergyLoad(ServletContext ctx, HttpServletRequest request,
                                 HttpServletResponse response, JSONObject out) throws SMASException, ParseException {
        int level = Integer.parseInt(request.getParameter("level"));
        int timeLevel = Integer.parseInt(request.getParameter("timeLevel"));
        Date startDate = Utils.toSqlDate(request.getParameter("startDate"));
        Date endDate = Utils.toSqlDate(request.getParameter("endDate"));
        if (level == 1) {
            UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userinfo");
            if (userInfo != null) {
                int[] meterIDs = userInfo.getPowerMeterIDs();
                dao.getConsumptionByMeterID(meterIDs[0], timeLevel, startDate, endDate, out);
            }
        } else if (level == 2) {
            String areaCode = request.getParameter("value");
            dao.getConsumptionByArea(areaCode, timeLevel, startDate, endDate, out);
        } else if (level == 3) {
            dao.getConsumptionByAll(timeLevel, startDate, endDate, out);
        }
    }

    public void compareOthers(ServletContext ctx, HttpServletRequest request,
                              HttpServletResponse response, JSONObject out) throws SMASException, ParseException {

        int compareMode = Integer.parseInt(request.getParameter("compareMode"));
        UserInfo userInfo = (UserInfo) request.getSession().getAttribute("userinfo");
        int[] powerMeterIDs = userInfo.getPowerMeterIDs();
        if (compareMode == -1) {
            viewMyEnergyLoad(ctx, request, response, out);
        } else if (compareMode == 1) {
            double radius = Double.parseDouble(request.getParameter("radius"));
            int timeLevel = Integer.parseInt(request.getParameter("timeLevel"));
            Date startDate = Utils.toSqlDate(request.getParameter("startDate"));
            Date endDate = Utils.toSqlDate(request.getParameter("endDate"));
            dao.getConsumptionByMeterID(powerMeterIDs[0], timeLevel, startDate, endDate, out);
            dao.getAvgConsumptionWithinRadius(powerMeterIDs[0], timeLevel, radius, startDate, endDate, out);
        } else if (compareMode == 2) {
            int timeLevel = Integer.parseInt(request.getParameter("timeLevel"));
            String selectedIDsStr = request.getParameter("selectedIDs");
            String str = selectedIDsStr.replace("[", "");
            str = str.replace("]", "");
            String[] IDs = str.split(",");
            if (IDs.length<2) {
                throw new SMASException("You should select at least two neighbourhood on map!");
            } else{
                Date startDate = Utils.toSqlDate(request.getParameter("startDate"));
                Date endDate = Utils.toSqlDate(request.getParameter("endDate"));
                dao.getConsumptionByMeterID(powerMeterIDs[0], timeLevel, startDate, endDate, out);
                dao.getAvgConsumption(timeLevel, selectedIDsStr, startDate, endDate, out);
            }
        }
    }
}
