package ca.uwaterloo.iss4e.command;

import ca.uwaterloo.iss4e.common.SMASException;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
public class CommandExecutor {
    private Object receiver;               // the "encapsulated" object
    private Method action;                 // the "pre-registered" request
    private Object[] args;                   // the "pre-registered" arg list

    public CommandExecutor(Object obj, String methodName, Object[] arguments) {
        receiver = obj;
        args = arguments;
        Class cls = obj.getClass();
        Class[] argTypes = new Class[args.length];
       /* for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }*/
        try {
            System.out.println("ClassName=" + cls.getName());
            action = cls.getDeclaredMethod(methodName,
                    new Class[]{
                            ServletContext.class,
                            HttpServletRequest.class,
                            HttpServletResponse.class,
                            JSONObject.class}
            );
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public Object execute()  throws Throwable {
        try {
            return action.invoke(receiver, args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
          //  throw new SMASException(e);
        } catch (IllegalAccessException e) {
            throw e.getCause();
        }
    }
}
