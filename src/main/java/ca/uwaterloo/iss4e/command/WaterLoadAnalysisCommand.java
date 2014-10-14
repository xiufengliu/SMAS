package ca.uwaterloo.iss4e.command;

import ca.uwaterloo.iss4e.common.SMASException;
import ca.uwaterloo.iss4e.common.Utils;
import ca.uwaterloo.iss4e.databases.Impl.WaterDAOImpl;
import ca.uwaterloo.iss4e.databases.WaterDAO;
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
 */
public class WaterLoadAnalysisCommand implements Command{
    private static final Logger log = Logger.getLogger(WaterLoadAnalysisCommand.class.getName());

    WaterDAO dao = new WaterDAOImpl();

    public void getCustomers(ServletContext ctx, HttpServletRequest request,
                             HttpServletResponse response, JSONObject out) throws SMASException, ParseException {

        int custidStart = Integer.parseInt(request.getParameter("custidstart"));
        dao.getWaterCustomers(out, custidStart);
    }

    public void getTypes(ServletContext ctx, HttpServletRequest request,
                         HttpServletResponse response, JSONObject out) throws SMASException, ParseException {

        dao.getWaterCustomerTypes(out);
    }

    public void getLoadProfile(ServletContext ctx, HttpServletRequest request,
                               HttpServletResponse response, JSONObject out) throws SMASException, ParseException {

        int custId = Integer.parseInt(request.getParameter("custid"));
        String typeNameStr = request.getParameter("typenames");
        String subTypeIDStr = request.getParameter("subtypeids");
        int timeLevel = Integer.parseInt(request.getParameter("timelevel"));
        if (timeLevel == -1) {
            throw new SMASException("Please select the reading type!");
        }
        if (custId != -1) {
            dao.getWaterLoadProfileByCust(out, custId, timeLevel);
        } else {
            if (!Utils.isEmpty(typeNameStr)) {
                String[] typeNames = typeNameStr.split(",");
                int[] subTypeIDs = null;
                if (!Utils.isEmpty(subTypeIDStr)) {
                    String[] subTypeIDArray = subTypeIDStr.split(",");
                    subTypeIDs = new int[subTypeIDArray.length];
                    for (int i = 0; i < subTypeIDArray.length; ++i) {
                        subTypeIDs[i] = Integer.parseInt(subTypeIDArray[i]);
                    }
                }
                dao.getWaterLoadProfileByCustType(out, typeNames, subTypeIDs, timeLevel);
            } else {
                throw new SMASException("Please select customer type!");
            }
        }
    }

    public void getLoadProfileByExoVar(ServletContext ctx, HttpServletRequest request,
                                       HttpServletResponse response, JSONObject out) throws SMASException, ParseException {

        String exovar = request.getParameter("exovar");
        String typeNameStr = request.getParameter("typenames");
        String subTypeIDStr = request.getParameter("subtypeids");
        if (!Utils.isEmpty(typeNameStr)) {
            String[] typeNames = typeNameStr.split(",");
            int[] subTypeIDs = null;
            if (!Utils.isEmpty(subTypeIDStr)) {
                String[] subTypeIDArray = subTypeIDStr.split(",");
                subTypeIDs = new int[subTypeIDArray.length];
                for (int i = 0; i < subTypeIDArray.length; ++i) {
                    subTypeIDs[i] = Integer.parseInt(subTypeIDArray[i]);
                }
            }
            dao.getWaterDailyAvgLoadByExoVariable(out, exovar, typeNames, subTypeIDs);
        } else {
            throw new SMASException("Please select customer type!");
        }
    }

    public void getLoadProfileWithPARX(ServletContext ctx, HttpServletRequest request,
                                       HttpServletResponse response, JSONObject out) throws SMASException, ParseException {

        String typeName = request.getParameter("typenames");
        dao.getWaterLoadDisaggregaionByPARX(out, typeName);
    }


}
