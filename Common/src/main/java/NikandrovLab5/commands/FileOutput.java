package NikandrovLab5.commands;

import NikandrovLab5.data.*;
import NikandrovLab5.utility.Collection;
import NikandrovLab5.utility.Database;
import NikandrovLab5.utility.FieldReceiverForFile;

import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Vector;

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

        if (name != null & coordinates.getY() != (long) -1 & area != (double) -1 & population != (long) -1 &
                metersAboveSeaLevel != null & climate != null & government != null & standardOfLiving != null & governor != null) {
            City city = new City(name, coordinates, area, population, metersAboveSeaLevel, climate, government, standardOfLiving, governor);
            city.setCreationDate();

            if(!collection.collection.containsKey(key)) {
                Database.insertDB(city, key, login);

                ResultSet resultSet = Database.statmt.executeQuery("SELECT * FROM collection" + " WHERE key = " + "'" + key + "'" + ";");
                city.setId(resultSet.getInt("id"));

                collection.collection.put(key, city);
                if (!collection.creators.containsKey(login)) {
                    collection.creators.put(login, new Vector<>());
                }
                collection.creators.get(login).add(city);
            }
        }
    }

    public static void updateId(LinkedHashMap<String, City> collection, int id, FileReader file) throws IOException, SQLException {
        String copyKey = "";
        for (String key : collection.keySet()) {
            if (collection.get(key).getId() == id) {
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
                        boolean f = Database.updateDB(name, "name", id);
                        if(f) collection.get(copyKey).setName(name);
                    }
                }
                case "coordinates" -> {
                    Coordinates coordinates = FieldReceiverForFile.getCoordinates(file);
                    if (coordinates.getY() != -1) {
                        if(Database.updateDB(coordinates.getX(), "coordinates_of_x", id) &
                                Database.updateDB(coordinates.getY(), "coordinates_of_y", id)){
                            collection.get(copyKey).setCoordinates(coordinates);
                        }
                    }
                }
                case "area" -> {
                    double area = FieldReceiverForFile.getArea(file);
                    if (area > 0) {
                        if(Database.updateDB(area, "area", id)){
                            collection.get(copyKey).setArea(area);
                        }
                    }
                }
                case "population" -> {
                    Long population = FieldReceiverForFile.getPopulation(file);
                    if (population > 0) {
                        if(Database.updateDB(population, "population", id)) {
                            collection.get(copyKey).setPopulation(population);
                        }
                    }
                }
                case "metersabvovesealevel" -> {
                    Integer metersAboveSeaLevel = FieldReceiverForFile.getMetersAboveSeaLevel(file);
                    if (metersAboveSeaLevel != null) {
                        if(Database.updateDB(metersAboveSeaLevel, "meters", id)) {
                            collection.get(copyKey).setMetersAboveSeaLevel(metersAboveSeaLevel);
                        }
                    }
                }
                case "climate" -> {
                    Climate climate = FieldReceiverForFile.getClimate(file);
                    if (climate != null) {
                        if(Database.updateDB(climate, "climate", id)) {
                            collection.get(copyKey).setClimate(climate);
                        }
                    }
                }
                case "government" -> {
                    Government government = FieldReceiverForFile.getGovernment(file);
                    if (government != null) {
                        if(Database.updateDB(government, "government", id)) {
                            collection.get(copyKey).setGovernment(government);
                        }
                    }
                }
                case "standardofliving" -> {
                    StandardOfLiving standardOfLiving = FieldReceiverForFile.getStandardOfLiving(file);
                    if (standardOfLiving != null) {
                        if(Database.updateDB(standardOfLiving, "standard_of_living", id)) {
                            collection.get(copyKey).setStandardOfLiving(standardOfLiving);
                        }
                    }
                }
                case "governor" -> {
                    Human human = FieldReceiverForFile.getGovernor(file);
                    if (human != null) {
                        if(Database.updateDB(human.getName(), "name_of_governor", id) &
                                Database.updateDB(human.getHeight(), "height_of_governor", id) &
                                Database.updateDB(human.getBirthday(), "birthday_of_governor", id)) {
                            collection.get(copyKey).setGovernor(human);
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
