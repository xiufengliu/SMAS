package ca.uwaterloo.iss4e.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.uwaterloo.iss4e.command.*;
import ca.uwaterloo.iss4e.common.Constant;
import ca.uwaterloo.iss4e.common.SMASException;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.json.JSONObject;
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

public class MediatorServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(MediatorServlet.class.getName());
    Hashtable<String, Command> commands = new Hashtable<String, Command>();
    ServletContext ctx;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.ctx = config.getServletContext();
        CacheManager singletonManager = CacheManager.create();
        Cache memoryOnlyCache = new Cache(Constant.CACHE, 100, false, true, 86400, 86400);
        singletonManager.addCache(memoryOnlyCache);
        Cache cache = singletonManager.getCache(Constant.CACHE);
        ctx.setAttribute(Constant.CACHE, cache);

        commands.put(Constant.LOGIN, new AuthorizationCommand(new String[]{"/index.ftl", "/utility.ftl",  "/consult.ftl", "/customer.ftl"}));
        commands.put(Constant.ACCOUNT_MGNT, new AccountManagementCommand(new String[]{"/ajax/account/register.ftl", "/ajax/account/registersucc.ftl"}));

        commands.put(Constant.USER, new UserCommand());
        commands.put(Constant.CUSTOMER_MGNT, new PowerCustomerManagementCommand());
        commands.put(Constant.FEEDBACK_SERVICE, new FeedbackCommand());
        commands.put(Constant.MESSAGE, new MessageCommand());
        commands.put(Constant.SCHEDULE, new ScheduleCommand());

        commands.put(Constant.LOAD_ANALYSIS, new PowerLoadAnalysisCommand());
        commands.put(Constant.PROFILE_ENERGY, new PowerProfileEnergyCommand());
        commands.put(Constant.FORCASTING, new PowerForcastCommand());
        commands.put(Constant.METERLOC, new PowerMeterLocationCommand());
        commands.put(Constant.SEGMENTATION, new PowerSegmentationCommand());
        commands.put(Constant.CONSUMPTION_PATTERN, new PowerConsumptionPatternCommand());
        commands.put(Constant.WATER_LOAD_ANALYSIS, new WaterLoadAnalysisCommand());
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        String cmd = request.getParameter("cmd");
        String subCmd = request.getParameter("subCmd");
        log.log(Level.INFO, "cmd=" + cmd + "; subCmd=" + subCmd);
        JSONObject out = new JSONObject();
        try {
            if (cmd == null || subCmd == null) {
                request.getRequestDispatcher("/index.ftl").forward(request, response);
                return;
            }

            if (!cmd.equals(Constant.LOGIN) &&
                !cmd.equals(Constant.ACCOUNT_MGNT) &&
                 request.getSession().getAttribute("userinfo")==null){
                throw new SMASException("Session is timed out! Please log in again!");
            }

            Object cmdObj = commands.get(cmd);
            if (cmdObj == null) {
                request.getRequestDispatcher("/index.ftl").forward(request, response);
                return;
            }
            CommandExecutor subCmdObj = new CommandExecutor(cmdObj, subCmd, new Object[]{this.ctx, request, response, out});
            subCmdObj.execute();
        } catch (Throwable e) {
            e.printStackTrace();
            out.put("errmsg", String.valueOf(e.getMessage()));
        } finally {
            try {
                response.setContentType(Constant.CONTENT_TYPE);
                PrintWriter writer = response.getWriter();
                out.write(writer);
                writer.flush();
            } catch (Exception ee) {
                System.out.println("Flushed exception!!!");
                ee.printStackTrace();
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

}
