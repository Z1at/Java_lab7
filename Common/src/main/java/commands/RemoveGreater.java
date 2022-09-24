package commands;


import utility.Collection;

import java.sql.SQLException;

public class RemoveGreater {
    public void removeGreater(Collection collection, String key, String login) throws SQLException {
        RemoveGreaterKey removeGreaterKey = new RemoveGreaterKey();
        removeGreaterKey.removeGreaterKey(key, collection, login);
    }
}
