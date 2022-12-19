package cookbook.accountcontrol;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AccountLogin //might be obsolete if we simplify the flow
{
    private String userName;
    private String userPassword;
    private Connection dbConn;

    private String retrievedPassword;
    private String retrievedUsername;

    public int loginAttempt(Connection dbConn, String name, String password)
    {
        this.userName = name;
        this.userPassword = password;
        try
        {
            Statement stm = dbConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String queryPass = "SELECT USER_NAME, USER_PASSWORD FROM USER_ACCOUNT WHERE USER_NAME = '" + name + "'";
            ResultSet queryResult = stm.executeQuery(queryPass);
            queryResult.first();
            this.retrievedUsername = queryResult.getString("USER_NAME");
            this.retrievedPassword = queryResult.getString("USER_PASSWORD");
        }
        catch (SQLException e)
        {
            System.out.println(e);
        }
        if(retrievedPassword==null)
        {
            return 1;
        }
        else if (userPassword != retrievedPassword)
        {
            return 2;
        }
        else if(userPassword.equals(retrievedPassword) && userName.equalsIgnoreCase(retrievedUsername))
        {
            return 3;
        }
        else
        {
            return 0;
        }
    }
}