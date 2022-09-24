package commands;

import data.*;
import utility.Collection;
import utility.FieldReceiver;
import utility.TextFormatting;

import java.time.ZonedDateTime;

public class InsertKey {
    public void insert(String key, Collection collection) {
        String name = FieldReceiver.getName();
        Coordinates coordinates = FieldReceiver.getCoordinates();
        ZonedDateTime creationDate = ZonedDateTime.now();
        double area = FieldReceiver.getArea();
        Long population = FieldReceiver.getPopulation();
        Integer metersAboveSeaLevel = FieldReceiver.getMetersAboveSeaLevel();
        Climate climate = FieldReceiver.getClimate();
        Government government = FieldReceiver.getGovernment();
        StandardOfLiving standardOfLiving = FieldReceiver.getStandardOfLiving();
        Human governor = FieldReceiver.getGovernor();
        collection.collection.put(key, new City(name, coordinates, area, population, metersAboveSeaLevel,
                climate, government, standardOfLiving, governor));
        collection.id++;
        System.out.println(TextFormatting.getGreenText("City added to collection"));
        System.out.println();
    }
}
