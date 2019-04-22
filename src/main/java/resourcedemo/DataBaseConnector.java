package resourcedemo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DataBaseConnector {

    static final String username = "postgres";
    static final String password = "Rd368446";
    static final String database = "jdbc:postgresql://localhost:5432/db_test";

    private Statement statement;

    DataBaseConnector() throws Exception{
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(database, username, password);
            statement = connection.createStatement();
    }

    public ResultSet getResultSet(String SQLQuery) throws Exception{
        return statement.executeQuery(SQLQuery);
    }

}
