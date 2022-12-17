package dbconnection;
import java.sql.*;
public class DatabaseQuery
{
    public String queryTest(Connection dbConnection, String name)
    {
        String resultVal = "";
        try
        {
            Statement stm = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String queryPass = "SELECT TEST_FIELD FROM USER_ACCOUNT WHERE TEST_TEXT = '" + name + "'";
            //String queryPass = "SELECT TEST_FIELD FROM USER_ACCOUNT";
            ResultSet queryResult = stm.executeQuery(queryPass);
            queryResult.first();
            resultVal = queryResult.getString("TEST_FIELD");
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return resultVal;
    }
}
