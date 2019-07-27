package newtest.Classes;

import java.io.IOException;
import java.sql.*;

public class DB {
    private static Connection connection;
    private static ResultSet resultSet;
    private static Statement statement;

    public static void setConnection(){
        try{
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:"+Config.ConfigRead());
            createTables();
        }
        catch (SQLException | ClassNotFoundException | IOException e){
            Alerts.Error(e.getMessage());
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    private static void createTables(){
        try {
            DatabaseMetaData metadata = connection.getMetaData();
            statement = connection.createStatement();
            resultSet = metadata.getTables(null,null, "subjects", null);
            if (!resultSet.next())
                statement.executeUpdate("CREATE TABLE subjects (idSub INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "nameSub TEXT)");
            resultSet = metadata.getTables(null,null,"topics", null);
            if (!resultSet.next())
                statement.executeUpdate("CREATE TABLE topics " +
                        "(idTopic INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "idSub INTEGER NOT NULL, " +
                        "nameTopic TEXT(50), " +
                        "FOREIGN KEY (idSub) REFERENCES subjects (idSub))");
            resultSet = metadata.getTables(null,null,"questions",null);
            if (!resultSet.next())
                statement.executeUpdate("CREATE TABLE questions " +
                        "(idQuestion INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "idTopic INTEGER NOT NULL," +
                        "nameQuestion TEXT, correctAnswer TEXT, " +
                        "FOREIGN KEY (idTopic) REFERENCES topics(idTopic))");
            resultSet = metadata.getTables(null,null,"answers",null);
            if (!resultSet.next())
                statement.executeUpdate("CREATE TABLE answers " +
                        "(idAnswer INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "idQuestion INTEGER NOT NULL, " +
                        "nameAnswer TEXT, " +
                        "isCorrect INTEGER, " +
                        "FOREIGN KEY (idQuestion) REFERENCES questions (idQuestion))");
        } catch (SQLException e){
            Alerts.Error(e.getMessage());
        }
    }
    public static ResultSet Select (String tableName, String Condition){
        try {
            if (Condition == null) {
                return statement.executeQuery("SELECT * FROM " + tableName);
            } else {
                return statement.executeQuery("SELECT * FROM "+tableName+" WHERE "+ Condition);
            }
        }
        catch (SQLException e){
            Alerts.Error(e.getMessage());
            return null;
        }
    }
    public static void Insert (String tableName, String fieldName, String Values){
        try{
            String sql = "INSERT INTO "+tableName+" ("+fieldName+") VALUES (\""+
                    Values+"\")";
            statement.executeUpdate(sql);
        } catch (SQLException e){
            Alerts.Error (e.getMessage());
        }
    }
    public static void Update (String tableName, String updates, String Condition){
        try {
            String sql = "UPDATE "+tableName+" SET "+updates+" WHERE "+Condition;
            statement.executeUpdate(sql);
        }catch (SQLException e){
            Alerts.Error(e.getMessage());
        }
    }
    public static void Delete (String table, String field, int id){
        try {
            String sql = "DELETE FROM "+table+" WHERE "+field+" = "+id;
            statement.executeUpdate(sql);
        } catch (SQLException e){
            Alerts.Error(e.getMessage());
        }
    }
}
