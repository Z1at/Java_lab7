import commands.*;
import data.*;
import utility.Collection;
import utility.Database;
import utility.TextFormatting;
import utility.Transformation;
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
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public class ServerManager extends Thread{
    final ServerSender serverSender;
    ServerReceiver serverReceiver;
    TreeMap<String, String> users = new TreeMap<>();
    Database database;
    final ReentrantLock lock = new ReentrantLock();

    public ServerManager(ServerReceiver serverReceiver, ServerSender serverSender, Database database){
        this.serverReceiver = serverReceiver;
        this.serverSender = serverSender;
        this.database = database;
    }

    public void startServer() throws SQLException {
        ResultSet resultSet = Database.statmt.executeQuery("SELECT * FROM users");
        while(resultSet.next()){
            users.put(resultSet.getString("login"), resultSet.getString("password"));
        }
    }

    public void run(Collection collection) throws IOException, ClassNotFoundException, InterruptedException, NoSuchAlgorithmException, SQLException {
        lock.lock();
        try {
            System.out.println(TextFormatting.getBlueText(Thread.currentThread().getName()));
            ServerMessage answer = new ServerMessage("The command was executed" + '\n');
            ByteBuffer byteBuffer = serverReceiver.receive(serverSender);
            ClientMessage clientMessage = (ClientMessage) Transformation.Deserialization(byteBuffer);
            boolean f = true;
            if (clientMessage.command != null) {
                if (clientMessage.command.equals("insert")) {
                    if (clientMessage.arg.contains(",")) {
                        answer.setMessage("There can be no commas in the key" + '\n');
                    } else if (collection.collection.containsKey(clientMessage.arg)) {
                        answer.setMessage("This key is already present in the collection");
                    } else {
                        City city = (City) clientMessage.obj;
                        city.setCreationDate();

                        while(true) {
                            city.setId(collection.id);
                            collection.id++;
                            try {
                                Database.insertDB(city, clientMessage.arg, clientMessage.login, collection);

                                //ResultSet resultSet = Database.statmt.executeQuery("SELECT * FROM collection" + " WHERE key = " + "'" + clientMessage.arg + "'" + ";");
                                //city.setId(resultSet.getInt("id"));

                                city.setCreator(clientMessage.login);
                                collection.collection.put(clientMessage.arg, city);
                                if (!collection.creators.containsKey(clientMessage.login)) {
                                    collection.creators.put(clientMessage.login, new Vector<>());
                                }
                                collection.creators.get(clientMessage.login).add(clientMessage.arg);
                                answer.setMessage("The object was successfully added" + '\n');
                                break;
                            } catch (Exception e) {
                                answer.setMessage("Failed to add element");
                            }
                        }
                    }

                    //Сортировка в обратном лексикографическом порядке с помощью Stream API и лямбда-выражений
                    Stream<String> stream = collection.collection.keySet().stream().sorted((key1, key2) -> -key1.compareTo(key2));
                    stream.forEach((s) -> collection.collection.put(s, collection.collection.remove(s)));
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
                    } else if (collection.creators.containsKey(clientMessage.login)) {
                        boolean flag = false;

                        for (String keyNow : collection.creators.get(clientMessage.login)) {
                            if (collection.collection.get(keyNow).equals(collection.collection.get(key))) {
                                flag = true;
                                break;
                            }
                        }

                        if (!flag) {
                            answer.setMessage("You did not create this object and cannot change it");
                        } else {
                            switch (clientMessage.arg) {
                                case "name" -> {
                                    if (Database.updateStringDB(clientMessage.obj, "name", clientMessage.id)) {
                                        collection.collection.get(key).setName((String) clientMessage.obj);
                                    }
                                }
                                case "coordinates" -> {
                                    Coordinates coordinates = (Coordinates) clientMessage.obj;
                                    if (Database.updateIntDB(coordinates.getX(), "coordinates_of_x", clientMessage.id) &
                                            Database.updateIntDB(coordinates.getY(), "coordinates_of_y", clientMessage.id)) {
                                        collection.collection.get(key).setCoordinates(coordinates);
                                    }
                                }
                                case "area" -> {
                                    if (Database.updateIntDB(clientMessage.obj, "area", clientMessage.id)) {
                                        collection.collection.get(key).setArea((Double) clientMessage.obj);
                                    }
                                }
                                case "population" -> {
                                    if (Database.updateIntDB(clientMessage.obj, "population", clientMessage.id)) {
                                        collection.collection.get(key).setPopulation((Long) clientMessage.obj);
                                    }
                                }
                                case "metersabovesealevel" -> {
                                    if (Database.updateIntDB(clientMessage.obj, "meters", clientMessage.id)) {
                                        collection.collection.get(key).setMetersAboveSeaLevel((Integer) clientMessage.obj);
                                    }
                                }
                                case "climate" -> {
                                    if (Database.updateStringDB(clientMessage.obj, "climate", clientMessage.id)) {
                                        collection.collection.get(key).setClimate((Climate) clientMessage.obj);
                                    }
                                }
                                case "government" -> {
                                    if (Database.updateStringDB(clientMessage.obj, "government", clientMessage.id)) {
                                        collection.collection.get(key).setGovernment((Government) clientMessage.obj);
                                    }
                                }
                                case "standardofliving" -> {
                                    if (Database.updateStringDB(clientMessage.obj, "standard_of_living", clientMessage.id)) {
                                        collection.collection.get(key).setStandardOfLiving((StandardOfLiving) clientMessage.obj);
                                    }
                                }
                                case "governor" -> {
                                    Human human = (Human) clientMessage.obj;
                                    if (Database.updateStringDB(human.getName(), "name_of_governor", clientMessage.id) &
                                            Database.updateIntDB(human.getHeight(), "height_of_governor", clientMessage.id) &
                                            Database.updateStringDB(human.getBirthday(), "birthday_of_governor", clientMessage.id)) {
                                        collection.collection.get(key).setGovernor((Human) clientMessage.obj);
                                    }
                                }
                            }
                            answer.setMessage("The element has been successfully replaced" + '\n');
                        }
                    } else {
                        answer.setMessage("You did not create this object and cannot change it");
                    }
                }
            } else if (clientMessage.commands[0].equals("register")) {
                if (users.containsKey(clientMessage.login)) {
                    answer.setMessage("This login already exists");
                } else {
                    answer.setMessage("You have successfully registered");

                    String query = "INSERT INTO users (login, password) VALUES (?, ?)";
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
            answer.setMessage("");
//            f = false;
            Operations operations = new Operations();
            Runnable serverManagerTask = () -> {
                try {
                    operations.run(clientMessage.commands, collection, answer, operations, clientMessage.login);
                    if (answer.message.equals("")) {
                        answer.setMessage("The command was executed" + '\n');
                    }

//                    synchronized (serverSender) {
//                        serverSender.send(answer);
//                    }
                } catch (Exception e) {
                    System.out.println("run_Server_manager");
                }
            };
            ExecutorService pool = Executors.newCachedThreadPool();
            pool.execute(serverManagerTask);
            sleep(100);
        }

        if (answer.message.equals("")) answer.setMessage("The command was executed \n");
        ServerSender.send(answer);
        }
        finally {
            lock.unlock();
        }
    }
}
