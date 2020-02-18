//STEP 1. Import required packages
import java.sql.*;
import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class SimpleJdbc {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://172.16.237.43/nifi?zeroDateTimeBehavior=convertToNull&autoReconnect=true&useSSL=false&useUnicode=true&characterEncoding=utf8";

    //  Database credentials
    static final String USER = "nifi";
    static final String PASS = "nifi";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        InetAddress ip;

        try{
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            //STEP 4: get System Resource Usage
            String val1 = "";
            String val2 = "";
            String val3 = "";
            ip = InetAddress.getLocalHost();
            String ipAddr = ip.getHostAddress();

            try {
                OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
                val1 = String.format("%.2f", osBean.getSystemCpuLoad() * 100);
                val2 = String.format("%.2f", (double)osBean.getFreePhysicalMemorySize()/1024/1024/1024);
                val3 = String.format("%.2f", (double)osBean.getTotalPhysicalMemorySize()/1024/1024/1024);
                System.out.println("HOST : "+ipAddr);
                System.out.println("CPU Usage : " + val1);
                System.out.println("Memory Free Space : " + val2 );
                System.out.println("Memory Total Space : " + val3 );

            }catch (Exception e){
                System.out.println(e.toString());
            }

            //STEP 5: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql;
            sql = "INSERT INTO SERVMON_FREQS (type, name, val1, val2, val3, regdate) "
                    + " values ('CPU_MEM', '"+ipAddr+"', '"+val1+"','"+val2+"','"+val3+"' , now())";
            //ResultSet rs = stmt.executeQuery(sql);
            stmt.executeUpdate(sql);

            //STEP 6: Clean-up environment
            //rs.close();
            stmt.close();
            conn.close();
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    stmt.close();
            }catch(SQLException se2){
            }// nothing we can do
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        }//end try
        System.out.println("Goodbye!");
    }//end main
}//end SimpleJdbc
