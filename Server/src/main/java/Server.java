import data.City;
import data.Coordinates;
import data.Human;
import utility.Collection;
import utility.Database;
import utility.TextFormatting;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public class Server {
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        DatagramChannel serverChannel = DatagramChannel.open();
        serverChannel.configureBlocking(false);

        { //Port Selection
            try {
                int port = Integer.parseInt(System.getenv("port"));
//                int port = 7354;
                serverChannel.bind(new InetSocketAddress("localhost", port));
            } catch (Exception ignored) {
                System.out.println(TextFormatting.getRedText("This port is busy or you entered the wrong port"));
                System.out.println(TextFormatting.getRedText("Enter the desired port via the environment variable \"port\""));
                System.exit(1);
            }
        }


        Collection collection = new Collection();

        Database database = new Database();
        database.connection();
        database.createDB();
        ResultSet resSet = Database.statmt.executeQuery("SELECT * FROM collection");
        while(resSet.next()){
            String key = resSet.getString("key");
            City city = new City();
            city.setId(resSet.getInt("id")); city.setName(resSet.getString("name"));
            city.setCoordinates(new Coordinates(resSet.getFloat("coordinates_of_x"), resSet.getLong("coordinates_of_y")));
            city.setCreationDate(resSet.getString("creation_date")); city.setArea(resSet.getDouble("area"));
            city.setPopulation(resSet.getLong("population")); city.setMetersAboveSeaLevel(resSet.getInt("meters"));
            city.setClimate(resSet.getString("climate")); city.setGovernment(resSet.getString("government"));
            city.setStandardOfLiving(resSet.getString("standard_of_living"));
            city.setGovernor(new Human(resSet.getString("name_of_governor"), resSet.getDouble("height_of_governor"), resSet.getString("birthday_of_governor")));
            city.setCreator(resSet.getString("creator"));

            collection.id = (Math.max(collection.id, city.getId()));
            String creator = resSet.getString("creator");
            if (!collection.creators.containsKey(creator)) {
                collection.creators.put(creator, new Vector<>());
            }
            collection.creators.get(creator).add(key);
            collection.collection.put(key, city);
        }
        collection.id++;

        Stream<String> stream = collection.collection.keySet().stream().sorted((key1, key2) -> -key1.compareTo(key2));
        stream.forEach((s) -> collection.collection.put(s, collection.collection.remove(s)));

        ServerReceiver serverReceiver = new ServerReceiver(serverChannel);
        ServerSender serverSender = new ServerSender(serverChannel);
        ServerManager serverManager = new ServerManager(serverReceiver, serverSender, database);

        serverManager.startServer();

        System.out.println("The server has started working");

        Selector selector;
        selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_READ);
        Runnable clientTask = () -> {
            try {
                while(true) {
                    selector.select();
                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();

                    while(it.hasNext()){
                        SelectionKey key = it.next();
                        it.remove();
                        if(key.isReadable()) {
                            serverManager.run(collection);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Runnable serverTask = () -> {
            Scanner scanner = new Scanner(System.in);
            while(true){
                try {
                    if (scanner.hasNext()) {
                        String command = scanner.nextLine();
                        if (command.equals("exit")) {
                            System.out.println(TextFormatting.getYellowText("The program is over, I hope you enjoyed it"));
                            System.exit(0);
                        } else {
                            System.out.println(TextFormatting.getRedText("Unknown command"));
                        }
                    }
                }
                catch (Exception ignored){
                    System.out.println("The program is over, I hope ou enjoyed it");
                }
            }
        };

        ExecutorService pool = Executors.newCachedThreadPool();
        pool.execute(clientTask);
        pool.execute(serverTask);

        pool.shutdown();
    }
}
