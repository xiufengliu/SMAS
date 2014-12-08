import ca.uwaterloo.iss4e.algorithm.PARX;
import ca.uwaterloo.iss4e.common.SMASException;
import ca.uwaterloo.iss4e.common.Utils;
import org.junit.Test;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.*;
import java.util.Properties;

/**
 * Created by xiliu on 20/05/14.
 */
public class SampleTest {

    public void test() {
        String url = "jdbc:postgresql://localhost/essex";
        Properties props = new Properties();
        props.setProperty("user", "xiliu");
        props.setProperty("password", "Abcd1234");
        PrintStream out = new PrintStream(new FileOutputStream(FileDescriptor.out));
        try {
            Connection conn = DriverManager.getConnection(url, props);
            PreparedStatement pstmt = conn.prepareStatement("select readtime from smas_power_hourlyreading limit 10");

            ResultSet rs = pstmt.executeQuery();


            while (rs.next()) {
                String hireDate = rs.getString(1);

                // System.out.println(" Hire Date: " + hireDate);
                System.out.println(" Hire Date: " + hireDate + "  " + Utils.getTime(hireDate, "UTC"));
            }
        } catch (Exception e) {
            out.println(e);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {


        try {
            System.out.println(Utils.toTimestampOnedayBeforeWithHour(353411232, 23));
        } catch (SMASException e) {
            e.printStackTrace();
        }
    }

}