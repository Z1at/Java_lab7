package commands;

import data.City;
import utility.Collection;
import utility.Database;
import utility.TextFormatting;

import java.sql.SQLException;
import java.util.Vector;

public class Clear {
    public void clear(Collection collection, String login) throws SQLException {
        if(collection.creators.get(login) == null) return;
        for(String key : collection.creators.get(login)){
            Database.removeKey(key);
            collection.collection.remove(key);
        }
        collection.creators.get(login).clear();

//        collection.collection.clear();
    }
}
