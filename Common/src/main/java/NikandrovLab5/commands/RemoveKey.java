package NikandrovLab5.commands;

import NikandrovLab5.utility.Database;
import NikandrovLab5.utility.TextFormatting;
import NikandrovLab5.utility.Collection;

import java.sql.SQLException;

public class RemoveKey {
    public void removeKey(String key, Collection collection) throws SQLException {
        collection.collection.remove(key);
        Database.removeDB(key);
    }
}
