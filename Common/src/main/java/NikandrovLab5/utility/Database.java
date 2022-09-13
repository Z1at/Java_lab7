package NikandrovLab5.utility;

import NikandrovLab5.data.City;

import java.sql.*;


public class Database {
    public static Connection conn;
    public static Statement statmt;

    // --------ПОДКЛЮЧЕНИЕ К БАЗЕ ДАННЫХ--------
    public void connection() throws ClassNotFoundException, SQLException
    {
        conn = null;
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:TstDataBase");

        System.out.println("База Подключена!");
    }

    // --------Создание таблицы--------
    public void createDB() throws ClassNotFoundException, SQLException
    {
        statmt = conn.createStatement();
        statmt.execute("CREATE TABLE if not exists 'collection' ('key' text, 'id' INTEGER PRIMARY KEY AUTOINCREMENT, 'name' text, " +
                "'coordinates_of_x' INTEGER, 'coordinates_of_y' INTEGER, 'creation_date' text, 'area' INTEGER, 'population' INTEGER, 'meters' INTEGER, " +
                "'climate' text, 'government' text, 'standard_of_living' text, 'name_of_governor' text, 'height_of_governor' REAL, 'birthday_of_governor' text, 'creator' text);");

        statmt.execute("CREATE TABLE if not exists 'users' ('login' text, 'password' text);");

        System.out.println("Таблица создана или уже существует.");
    }

    public static boolean updateDB(Object obj, String field, int id) throws SQLException {
        int flag = statmt.executeUpdate("UPDATE collection SET " + field + " = " + "'" + obj + "'" + " WHERE id = " + id + ";");
        return (flag > 0);
    }

    public static void insertDB(City city, String key, String login) throws SQLException {
        String query = "INSERT INTO 'collection' ('key', 'name', 'coordinates_of_x', 'coordinates_of_y'," +
                "'creation_date', 'area', 'population', 'meters', 'climate', 'government', 'standard_of_living'," +
                "'name_of_governor', 'height_of_governor', 'birthday_of_governor', 'creator') VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setString(1, key); statement.setString(2, city.getName());
        statement.setFloat(3, city.getCoordinates().getX()); statement.setDouble(4, city.getCoordinates().getY());
        statement.setString(5, city.getCreationDate().toString()); statement.setDouble(6, city.getArea());
        statement.setLong(7, city.getPopulation()); statement.setInt(8, city.getMetersAboveSeaLevel());
        statement.setString(9, city.getClimate().toString()); statement.setString(10, city.getGovernment().toString());
        statement.setString(11, city.getStandardOfLiving().toString()); statement.setString(12, city.getGovernor().getName());
        statement.setDouble(13, city.getGovernor().getHeight()); statement.setString(14, city.getGovernor().getBirthday().toString());
        statement.setString(15, login);
        statement.execute();
    }

    public static void removeDB(String key) throws SQLException {
        statmt.executeUpdate("DELETE FROM collection where key = " + "'" + key + "'");
    }

    public static void clear() throws SQLException {
        statmt.execute("DELETE from collection;");
    }
}
