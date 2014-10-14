package ca.uwaterloo.iss4e.databases;

import java.sql.Connection;
import java.sql.SQLException;

import ca.uwaterloo.iss4e.common.SMASException;
import org.rosuda.JRI.Rengine;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
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
public class DAOUtils {

    public static synchronized Connection getDBConnection() throws SMASException {
        Connection connection = null;
        try {
            InitialContext cxt = new InitialContext();
            if (cxt == null) {
                throw new SMASException("No context!");
            }

            DataSource ds = (DataSource) cxt
                    .lookup("java:/comp/env/jdbc/postgres");

            if (ds == null) {
                throw new SMASException("Data source not found!");
            }

            connection = ds.getConnection();
        } catch (NamingException | SQLException e) {
            throw new SMASException(e);
        }
        return connection;
    }

    public static synchronized void freeConnection(Connection connection)
    {
        try
        {
            if (connection!=null)
                connection.close();
        }
        catch (Exception e)
        {
            System.err.println("Threw an exception closing a database connection");
            e.printStackTrace();
        }
    }

    public static Rengine getREngine() {
        if (engine == null) {
            engine = new Rengine(new String[]{"--no-save"}, false, null);
        }

        return engine;
    }

    static Rengine engine;
}
