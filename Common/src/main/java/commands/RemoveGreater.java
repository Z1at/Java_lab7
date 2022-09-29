package commands;


import src.ServerMessage;
import utility.Collection;

import java.sql.SQLException;

public class RemoveGreater {
    public void removeGreater(Collection collection, String key, String login, ServerMessage answer) throws SQLException {
        RemoveGreaterKey removeGreaterKey = new RemoveGreaterKey();
        removeGreaterKey.removeGreaterKey(key, collection, login, answer);
    }
}
