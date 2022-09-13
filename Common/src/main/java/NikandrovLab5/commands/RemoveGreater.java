package NikandrovLab5.commands;

import NikandrovLab5.utility.Collection;

import java.sql.SQLException;

public class RemoveGreater {
    public void removeGreater(Collection collection, String key) throws SQLException {
        RemoveGreaterKey removeGreaterKey = new RemoveGreaterKey();
        removeGreaterKey.removeGreaterKey(key, collection);
    }
}
