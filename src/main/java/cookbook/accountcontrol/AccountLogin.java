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
    private String retrievedType;

    public String loginAttempt(Connection dbConn, String name, String password)
    {
        this.userName = name;
        this.userPassword = password;
        try
        {
            Statement stm = dbConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String queryPass = "SELECT * FROM USER_ACCOUNT WHERE USER_NAME = '" + name + "'";
            ResultSet queryResult = stm.executeQuery(queryPass);
            if(queryResult.next()) //check if entry is empty, if it is then exit
            {
                queryResult.first();
                this.retrievedUsername = queryResult.getString("USER_NAME");
                this.retrievedPassword = queryResult.getString("USER_PASSWORD");
                this.retrievedType = queryResult.getString("USER_TYPE");
                //System.out.println(password);
                //System.out.println(retrievedPassword);
            }
        }
        catch (SQLException e)
        {
            System.out.println(e);
        }
        if(retrievedPassword==null)
        {
            return "USER_NOT_FOUND";
        }
        else if (!userPassword.equals(retrievedPassword))
        {
            return "PASSWORD_INCORRECT";
        }
        else if(userPassword.equals(retrievedPassword) && userName.equalsIgnoreCase(retrievedUsername))
        {
            return retrievedType;
        }
        else
        {
            return "UNKNOWN";
        }
    }
}