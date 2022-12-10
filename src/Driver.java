import dbconnection.DatabaseControl;
import dbconnection.DatabaseQuery;

import java.sql.*;
public class Driver
{
    public static void main(String[]args) throws Exception
    {
        DatabaseControl databaseConnection = new DatabaseControl();
        DatabaseQuery databaseQuery = new DatabaseQuery();

        //todo: maybe enable unique access for the server? else maybe hardcode to db connection
        String url = "localhost:3306/cookbook";
        String user = "root";
        String pass = "pass";

        Connection dbConn = databaseConnection.establishConnection(url,user,pass);
        String result = databaseQuery.queryTest(dbConn, "Arno");
        System.out.println(result);
    }
}
