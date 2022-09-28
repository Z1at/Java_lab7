package utility;

import data.City;

import java.sql.*;


public class Database {
    public static Connection conn;
    public static Statement statmt;

    // --------ПОДКЛЮЧЕНИЕ К БАЗЕ ДАННЫХ--------
    public void connection() throws ClassNotFoundException, SQLException
    {
        conn = null;
//        Class.forName("org.sqlite.JDBC");
//        conn = DriverManager.getConnection("jdbc:sqlite:TstDataBase");



        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
//            бросить ошибку
        }
        String login = System.getenv("DB_LOGIN");
        String password = System.getenv("DB_PASSWORD");
        String host = System.getenv("DB_HOST");
        if (login == null || password == null) {
//            Бросить ошибку
        }
        if (host == null) {
            host = "jdbc:postgresql://pg:5432/studs";
        }

        conn = DriverManager.getConnection(host, login, password);
//
        System.out.println("Database is connect");
    }

    // --------Создание таблицы--------
    public void createDB() throws ClassNotFoundException, SQLException
    {
        statmt = conn.createStatement();

        statmt.execute("CREATE TABLE if not exists collection (key text, id INTEGER, name text, " +
                "coordinates_of_x INTEGER, coordinates_of_y INTEGER, creation_date text, area INTEGER, population INTEGER, meters INTEGER, " +
                "climate text, government text, standard_of_living text, name_of_governor text, height_of_governor REAL, birthday_of_governor text, creator text);");

        statmt.execute("CREATE TABLE if not exists users (login text, password text);");

        System.out.println("The table has been created or already exists");
    }

    public static boolean updateIntDB(Object obj, String field, int id) throws SQLException {
        int flag = statmt.executeUpdate("UPDATE collection SET " + field + " = " + obj + " WHERE id = " + id + ";");
        return (flag > 0);
    }

    public static boolean updateStringDB(Object obj, String field, int id) throws SQLException {
        int flag = statmt.executeUpdate("UPDATE collection SET " + field + " = " + "'" + obj + "'" + " WHERE id = " + id + ";");
        return (flag > 0);
    }

    public static void insertDB(City city, String key, String login) throws SQLException {
        int i = 0;
        while(true) {
            try {
                String query = "INSERT INTO collection (key, id, name, coordinates_of_x, coordinates_of_y," +
                        "creation_date, area, population, meters, climate, government, standard_of_living," +
                        "name_of_governor, height_of_governor, birthday_of_governor, creator) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement statement = conn.prepareStatement(query);
                statement.setString(1, key);
                statement.setInt(2, i);
                statement.setString(3, city.getName());
                statement.setFloat(4, city.getCoordinates().getX());
                statement.setDouble(5, city.getCoordinates().getY());
                statement.setString(6, city.getCreationDate().toString());
                statement.setDouble(7, city.getArea());
                statement.setLong(8, city.getPopulation());
                statement.setInt(9, city.getMetersAboveSeaLevel());
                statement.setString(10, city.getClimate().toString());
                statement.setString(11, city.getGovernment().toString());
                statement.setString(12, city.getStandardOfLiving().toString());
                statement.setString(13, city.getGovernor().getName());
                statement.setDouble(14, city.getGovernor().getHeight());
                statement.setString(15, city.getGovernor().getBirthday().toString());
                statement.setString(16, login);
                statement.execute();
                break;
            } catch (Exception ignored) {
                i++;
            }
        }
    }

    public static void removeKey(String key) throws SQLException {
        statmt.executeUpdate("DELETE FROM collection where key = " + "'" + key + "'");
    }

    public static void removeId(int id) throws SQLException {
        statmt.executeUpdate("DELETE FROM collection where id = " + "'" + id + "'");
    }

//    public static void clear() throws SQLException {
//        statmt.execute("DELETE from collection;");
//    }
}
