package cookbook.accountcontrol;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AccountRegister
{
    private String userName;
    private String userPassword;
    private Connection dbConnection;

    public String registerAttempt(Connection dbConn, String name, String password)
    {
        String result = "UNKNOWN";
        this.userName = name;
        this.userPassword = password;
        this.dbConnection = dbConn;
        try
        {
            Statement stm = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String queryCheckData = ("SELECT USER_NAME FROM USER_ACCOUNT WHERE USER_NAME='" + name + "'");
            ResultSet queryCDResult = stm.executeQuery(queryCheckData);
            if(queryCDResult.next())
            {
                String queryInsertData = "INSERT INTO USER_ACCOUNT(USER_NAME,USER_PASSWORD,USER_TYPE)VALUES('" + userName + "','" + userPassword + "','" + "'USER'"; //create account
                stm.executeQuery(queryInsertData);
                ResultSet queryCheckAgain = stm.executeQuery(queryCheckData);
                if(!queryCheckAgain.next())
                {
                    System.out.println("Success.");
                    result = "ACCOUNT_CREATION_SUCCESS";
                }
                else
                {
                    System.out.println("An error occurred.");
                }
            }
            else
            {
                System.out.println("Username already taken.");
                result = "USERNAME_TAKEN";
            }
        }
        catch (SQLException e)
        {
            System.out.println(e);
        }
        return result;
    }
}