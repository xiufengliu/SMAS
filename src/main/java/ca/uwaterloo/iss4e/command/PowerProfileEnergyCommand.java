package ca.uwaterloo.iss4e.command;

import ca.uwaterloo.iss4e.common.SMASException;
import ca.uwaterloo.iss4e.common.Utils;
import ca.uwaterloo.iss4e.databases.Impl.PowerDAOImpl;
import ca.uwaterloo.iss4e.databases.PowerDAO;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
public class PowerProfileEnergyCommand implements Command {
    private static final Logger log = Logger.getLogger(PowerProfileEnergyCommand.class.getName());
    PowerDAO dao = new PowerDAOImpl();

    public void getOneAndHomeIDs(ServletContext ctx, HttpServletRequest request,
                                 HttpServletResponse response, JSONObject out) throws IOException, SMASException {

            /*Cache cache = (Cache) ctx.getAttribute(Constant.CACHE);
            List<Integer> homeIDs = super.getHomeIDs(ctx, request);
            int homeID = homeIDs.get(0);
            DAO feedbackDao = DAOFactory.createDAO(request);
            Chart chart = feedbackDao.getWidthHistogram(homeID, Integer.parseInt(request.getParameter("nbuckets")));
            Gson gson = new Gson();
            response.setContentType(Constant.CONTENT_TYPE);
            PrintWriter out = response.getWriter();
            out.print(gson.toJson(new Object[]{homeIDs, chart}));
            out.flush();*/
    }

    public void getThreel(ServletContext ctx, HttpServletRequest request,
                          HttpServletResponse response, JSONObject out) throws SMASException, ParseException {

        //Cache cache = (Cache) ctx.getAttribute(Constant.CACHE);
        //List<Integer> homeIDs = super.getHomeIDs(ctx, request);
        int meterid = Integer.parseInt(request.getParameter("value"));
        Date startDate = Utils.toSqlDate(request.getParameter("startDate"));
        Date endDate = Utils.toSqlDate(request.getParameter("endDate"));
         dao.getDataPointsByID(meterid, startDate, endDate, out);
        dao.getThreelinesByID(meterid, startDate, endDate, out);
    }

    public void getHistogram(ServletContext ctx, HttpServletRequest request,
                             HttpServletResponse response, JSONObject out) throws IOException, SMASException, ParseException {

        int meterid = Integer.parseInt(request.getParameter("value"));
        Date startDate = Utils.toSqlDate(request.getParameter("startDate"));
        Date endDate = Utils.toSqlDate(request.getParameter("endDate"));

        int nbuckets = Integer.parseInt(request.getParameter("nbuckets"));
        dao.getWidthHistogram(meterid, nbuckets, startDate, endDate, out);
    }

    public void getAvgHourlyActivityLoad(ServletContext ctx, HttpServletRequest request,
                                         HttpServletResponse response, JSONObject out) throws SMASException, IOException, ParseException {
        int meterid = Integer.parseInt(request.getParameter("value"));
        Date startDate = Utils.toSqlDate(request.getParameter("startDate"));
        Date endDate = Utils.toSqlDate(request.getParameter("endDate"));
        int order = Integer.parseInt(request.getParameter("order"));

        dao.getAvgHourlyActivityLoad(meterid, order, startDate, endDate, out);
    }

}
