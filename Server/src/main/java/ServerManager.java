import NikandrovLab5.commands.*;
import NikandrovLab5.data.*;
import NikandrovLab5.utility.Collection;
import NikandrovLab5.utility.Database;
import NikandrovLab5.utility.TextFormatting;
import NikandrovLab5.utility.Transformation;
import com.sun.source.tree.Tree;
import src.ClientMessage;
import src.ServerMessage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.Vector;
import java.util.stream.Stream;

public class ServerManager {
    ServerSender serverSender;
    ServerReceiver serverReceiver;
    TreeMap<String, String> users = new TreeMap<>();
    Database database;

    public ServerManager(ServerReceiver serverReceiver, ServerSender serverSender, Database database){
        this.serverReceiver = serverReceiver;
        this.serverSender = serverSender;
        this.database = database;
    }

    public void start() throws SQLException {
        ResultSet resultSet = Database.statmt.executeQuery("SELECT * FROM users");
        while(resultSet.next()){
            users.put(resultSet.getString("login"), resultSet.getString("password"));
        }
    }

    public void run(Collection collection) throws IOException, ClassNotFoundException, InterruptedException, NoSuchAlgorithmException, SQLException {
        ServerMessage answer = new ServerMessage("The command was executed" + '\n');
        ByteBuffer byteBuffer = serverReceiver.receive(serverSender);
        ClientMessage clientMessage = (ClientMessage) Transformation.Deserialization(byteBuffer);
        if (clientMessage.command != null) {
            if (clientMessage.command.equals("insert")) {
                if (clientMessage.arg.contains(",")) {
                    answer.setMessage("There can be no commas in the key" + '\n');
                }
                else if(collection.collection.containsKey(clientMessage.arg)){
                    answer.setMessage("This key is already present in the collection");
                }
                else {
                    City city = (City) clientMessage.obj;
                    city.setCreationDate();

                    try {
                        Database.insertDB(city, clientMessage.arg, clientMessage.login);

                        ResultSet resultSet = Database.statmt.executeQuery("SELECT * FROM collection" + " WHERE key = " + "'" + clientMessage.arg + "'" + ";");
                        city.setId(resultSet.getInt("id"));

                        collection.collection.put(clientMessage.arg, city);
                        if (!collection.creators.containsKey(clientMessage.login)) {
                            collection.creators.put(clientMessage.login, new Vector<>());
                        }
                        collection.creators.get(clientMessage.login).add(city);
                        answer.setMessage("The object was successfully added" + '\n');
                    }
                    catch (Exception ignored){
                        answer.setMessage("Failed to add element");
                    }
                }
            } else {
                String key = null;
                for (String now : collection.collection.keySet()) {
                    if (collection.collection.get(now).getId() == clientMessage.id) {
                        key = now;
                        break;
                    }
                }
                if (key == null) {
                    answer.setMessage("There is no item with this id in the collection" + '\n');
                }
                else if(collection.creators.containsKey(clientMessage.login)){
                    boolean flag = false;

                    for(City city : collection.creators.get(clientMessage.login)){
                        if(city.equals(collection.collection.get(key))){
                            flag = true;
                            break;
                        }
                    }

                    if(!flag){
                        answer.setMessage("You did not create this object and cannot change it");
                    }
                    else {
                        switch (clientMessage.arg) {
                            case "name" -> {
                                boolean f = Database.updateDB(clientMessage.obj, "name", clientMessage.id);
                                if(f) collection.collection.get(key).setName((String) clientMessage.obj);
                            }
                            case "coordinates" -> {
                                Coordinates coordinates = (Coordinates) clientMessage.obj;
                                if(Database.updateDB(coordinates.getX(), "coordinates_of_x", clientMessage.id) &
                                Database.updateDB(coordinates.getY(), "coordinates_of_y", clientMessage.id)){
                                    collection.collection.get(key).setCoordinates(coordinates);
                                }
                            }
                            case "area" -> {
                                if(Database.updateDB(clientMessage.obj, "area", clientMessage.id)){
                                    collection.collection.get(key).setArea((Double) clientMessage.obj);
                                }
                            }
                            case "population" -> {
                                if(Database.updateDB(clientMessage.obj, "population", clientMessage.id)) {
                                    collection.collection.get(key).setPopulation((Long) clientMessage.obj);
                                }
                            }
                            case "metersabovesealevel" -> {
                                if(Database.updateDB(clientMessage.obj, "meters", clientMessage.id)) {
                                    collection.collection.get(key).setMetersAboveSeaLevel((Integer) clientMessage.obj);
                                }
                            }
                            case "climate" -> {
                                if(Database.updateDB(clientMessage.obj, "climate", clientMessage.id)) {
                                    collection.collection.get(key).setClimate((Climate) clientMessage.obj);
                                }
                            }
                            case "government" -> {
                                if(Database.updateDB(clientMessage.obj, "government", clientMessage.id)) {
                                    collection.collection.get(key).setGovernment((Government) clientMessage.obj);
                                }
                            }
                            case "standardofliving" -> {
                                if(Database.updateDB(clientMessage.obj, "standard_of_living", clientMessage.id)) {
                                    collection.collection.get(key).setStandardOfLiving((StandardOfLiving) clientMessage.obj);
                                }
                            }
                            case "governor" -> {
                                Human human = (Human) clientMessage.obj;
                                if(Database.updateDB(human.getName(), "name_of_governor", clientMessage.id) &
                                Database.updateDB(human.getHeight(), "height_of_governor", clientMessage.id) &
                                Database.updateDB(human.getBirthday(), "birthday_of_governor", clientMessage.id)) {
                                    collection.collection.get(key).setGovernor((Human) clientMessage.obj);
                                }
                            }
                        }
                        answer.setMessage("The element has been successfully replaced" + '\n');
                    }
                }
                else{
                    answer.setMessage("You did not create this object and cannot change it");
                }
            }
        }
        else if(clientMessage.commands[0].equals("register")){
            if(users.containsKey(clientMessage.login)){
                answer.setMessage("This login already exists");
            }
            else{
                answer.setMessage("You have successfully registered");

                String query = "INSERT INTO 'users' ('login', 'password') VALUES (?, ?)";
                PreparedStatement preparedStatement = Database.conn.prepareStatement(query);
                preparedStatement.setString(1, clientMessage.login);
                preparedStatement.setString(2, Arrays.toString(MessageDigest.getInstance("SHA-384").digest(clientMessage.password.getBytes())));
                preparedStatement.execute();

                users.put(clientMessage.login, Arrays.toString(MessageDigest.getInstance("SHA-384").digest(clientMessage.password.getBytes())));
            }
        }
        else if(clientMessage.commands[0].equals("check")){
            answer.setMessage("Invalid username or password");
            if(users.containsKey(clientMessage.login)){
                if(users.get(clientMessage.login).equals(Arrays.toString(MessageDigest.getInstance("SHA-384").digest(clientMessage.password.getBytes())))){
                    answer.setMessage("You have successfully logged into your account");
                }
            }
        }
        else{
            Operations operations = new Operations();
            answer.setMessage("");
            operations.run(clientMessage.commands, collection, answer, operations, clientMessage.login);
            if(answer.message.equals("")){
                answer.setMessage("The command was executed" + '\n');
            }
        }

        //Сортировка в обратном лексикографическом порядке с помощью Stream API и лямбда-выражений
        Stream<String> stream = collection.collection.keySet().stream().sorted((key1, key2) -> -key1.compareTo(key2));
        stream.forEach((s) -> collection.collection.put(s, collection.collection.remove(s)));

        int i = 0;
        for(; i + 9000 < answer.message.length(); i += 9000){
            serverSender.send(Transformation.Serialization(new ServerMessage(answer.message.substring(i, i + 9000))));
            Thread.sleep(50);
        }

        if(i < answer.message.length()){
            serverSender.send(Transformation.Serialization(new ServerMessage(answer.message.substring(i))));
            Thread.sleep(50);
        }

        serverSender.send(Transformation.Serialization(new ServerMessage("end")));
    }
}
