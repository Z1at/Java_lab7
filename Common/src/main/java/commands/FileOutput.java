package commands;

import data.*;
import utility.Collection;
import utility.Database;
import utility.FieldReceiverForFile;
import utility.TextFormatting;

import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.stream.Stream;

/**
 * Class for executing two commands from a file
 */
public class FileOutput {
    public static void insert(Collection collection, String key, FileReader file, String login) throws IOException, SQLException {
        String name = FieldReceiverForFile.getName(file);
        Coordinates coordinates = FieldReceiverForFile.getCoordinates(file);
        double area = FieldReceiverForFile.getArea(file);
        Long population = FieldReceiverForFile.getPopulation(file);
        Integer metersAboveSeaLevel = FieldReceiverForFile.getMetersAboveSeaLevel(file);
        Climate climate = FieldReceiverForFile.getClimate(file);
        Government government = FieldReceiverForFile.getGovernment(file);
        StandardOfLiving standardOfLiving = FieldReceiverForFile.getStandardOfLiving(file);
        Human governor = FieldReceiverForFile.getGovernor(file);

        try {
            if (name != null & coordinates.getY() != (long) -1 & area != (double) -1 & population != (long) -1 &
                    metersAboveSeaLevel != null & climate != null & government != null & standardOfLiving != null & governor != null) {
                City city = new City(name, coordinates, area, population, metersAboveSeaLevel, climate, government, standardOfLiving, governor);
                city.setCreationDate();
                city.setCreator(login);

                Database.insertDB(city, key, login, collection);

//                System.out.println("Kek");
//                ResultSet resultSet = Database.statmt.executeQuery("SELECT * FROM collection WHERE key = '" + key + "' ;");
//                resultSet.next();
//                city.setId(resultSet.getInt("id"));
//                resultSet.close();

                collection.collection.put(key, city);
                if (!collection.creators.containsKey(login)) {
                    collection.creators.put(login, new Vector<>());
                }
                collection.creators.get(login).add(key);
            }
        }
        catch (Exception e){
//            e.printStackTrace();
        }

        //Сортировка в обратном лексикографическом порядке с помощью Stream API и лямбда-выражений
        Stream<String> stream = collection.collection.keySet().stream().sorted((key1, key2) -> -key1.compareTo(key2));
        stream.forEach((s) -> collection.collection.put(s, collection.collection.remove(s)));
    }

    public static void updateId(Collection collection, int id, FileReader file) throws IOException, SQLException {
        String copyKey = "";
        for (String key : collection.collection.keySet()) {
            if (collection.collection.get(key).getId() == id) {
                copyKey = key;
                break;
            }
        }
        if (!copyKey.equals("")) {
            String[] string = getLine(file).toLowerCase().trim().split(" ");
            StringBuilder concatenation = new StringBuilder();
            for (String temporary : string) {
                concatenation.append(temporary);
            }
            switch (concatenation.toString()) {
                case "name" -> {
                    String name = FieldReceiverForFile.getName(file);
                    if (name != null) {
                        boolean f = Database.updateStringDB(name, "name", id);
                        if(f) collection.collection.get(copyKey).setName(name);
                    }
                }
                case "coordinates" -> {
                    Coordinates coordinates = FieldReceiverForFile.getCoordinates(file);
                    if (coordinates.getY() != -1) {
                        if(Database.updateIntDB(coordinates.getX(), "coordinates_of_x", id) &
                                Database.updateIntDB(coordinates.getY(), "coordinates_of_y", id)){
                            collection.collection.get(copyKey).setCoordinates(coordinates);
                        }
                    }
                }
                case "area" -> {
                    double area = FieldReceiverForFile.getArea(file);
                    if (area > 0) {
                        if(Database.updateIntDB(area, "area", id)){
                            collection.collection.get(copyKey).setArea(area);
                        }
                    }
                }
                case "population" -> {
                    Long population = FieldReceiverForFile.getPopulation(file);
                    if (population > 0) {
                        if(Database.updateIntDB(population, "population", id)) {
                            collection.collection.get(copyKey).setPopulation(population);
                        }
                    }
                }
                case "metersabvovesealevel" -> {
                    Integer metersAboveSeaLevel = FieldReceiverForFile.getMetersAboveSeaLevel(file);
                    if (metersAboveSeaLevel != null) {
                        if(Database.updateIntDB(metersAboveSeaLevel, "meters", id)) {
                            collection.collection.get(copyKey).setMetersAboveSeaLevel(metersAboveSeaLevel);
                        }
                    }
                }
                case "climate" -> {
                    Climate climate = FieldReceiverForFile.getClimate(file);
                    if (climate != null) {
                        if(Database.updateStringDB(climate, "climate", id)) {
                            collection.collection.get(copyKey).setClimate(climate);
                        }
                    }
                }
                case "government" -> {
                    Government government = FieldReceiverForFile.getGovernment(file);
                    if (government != null) {
                        if(Database.updateStringDB(government, "government", id)) {
                            collection.collection.get(copyKey).setGovernment(government);
                        }
                    }
                }
                case "standardofliving" -> {
                    StandardOfLiving standardOfLiving = FieldReceiverForFile.getStandardOfLiving(file);
                    if (standardOfLiving != null) {
                        if(Database.updateStringDB(standardOfLiving, "standard_of_living", id)) {
                            collection.collection.get(copyKey).setStandardOfLiving(standardOfLiving);
                        }
                    }
                }
                case "governor" -> {
                    Human human = FieldReceiverForFile.getGovernor(file);
                    if (human != null) {
                        if(Database.updateStringDB(human.getName(), "name_of_governor", id) &
                                Database.updateIntDB(human.getHeight(), "height_of_governor", id) &
                                Database.updateStringDB(human.getBirthday(), "birthday_of_governor", id)) {
                            collection.collection.get(copyKey).setGovernor(human);
                        }
                    }
                }
            }
        }
    }

    public static String getLine(FileReader file) throws IOException {
        String string = "";
        while (file.ready()) {
            char c = (char) file.read();
            if (c == '\n') {
                break;
            }
            string += c;
        }
        return string;
    }
}
