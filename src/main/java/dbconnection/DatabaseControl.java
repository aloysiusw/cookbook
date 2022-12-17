package dbconnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseControl
{
    protected String dbURL;
    protected String dbUser;
    protected String dbPass;
    protected Connection dbConnection;

    //getter -> return connection
    //setter -> establish connection
    //nvm, consolidate
    public Connection establishConnection(String url, String user, String pass) throws SQLException //setter, needs url input
    {
        try
        {
            this.dbURL = "jdbc:mysql://" + url;
            this.dbUser = "?&user=" + user;
            this.dbPass = "&pass=" + pass;
        }
        catch (Exception e)
        {
            System.out.println("Error in setting connection values: " + e);
        }
        //jdbc:mysql://localhost:3306/?user=root
        String connectionString = this.dbURL + this.dbUser + this.dbPass;
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            dbConnection = DriverManager.getConnection(connectionString);
        }
        catch (Exception e)
        {
            System.out.println("Error in establishing connection: " + e);
        }
        System.out.println(dbConnection);
        if(dbConnection != null)
        {
            return dbConnection;
        }
        else
        {
            System.out.println("No connection established");
            return null;
        }
    }
    /* redundant, mixed with connection
    public Connection establishConnection() //getter, return the connection value
    {
        if(dbConnection != null)
        {
            return dbConnection;
        }
        else
        {
            System.out.println("No connection established");
            return null;
        }
    }*/
}
