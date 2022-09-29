package commands;

import src.ServerMessage;
import utility.Collection;
import utility.Database;
import utility.TextFormatting;

import java.sql.SQLException;
import java.util.Vector;

public class RemoveLower {
    public void removeLower(Collection collection, String key, String login, ServerMessage answer) throws SQLException {
        Vector<String> keys = new Vector<>();
        for (String temporary : collection.collection.keySet()) {
            if (temporary.compareTo(key) < 0) {
                keys.add(temporary);
            }
        }
        for (String temporary : keys) {
            RemoveKey removeKey = new RemoveKey();
            removeKey.removeKey(temporary, collection, login, answer);
//            collection.collection.remove(temporary);
//            Database.removeKey(temporary);
        }
    }
}
