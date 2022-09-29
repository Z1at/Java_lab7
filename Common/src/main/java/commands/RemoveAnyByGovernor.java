package commands;

import data.Human;
import src.ServerMessage;
import utility.Collection;
import utility.Database;
import utility.TextFormatting;

import java.sql.SQLException;
import java.util.Objects;

public class RemoveAnyByGovernor {
    public void removeAnyByGovernor(Human governor, Collection collection, String login, ServerMessage answer) throws SQLException {
        String temporary = "";
        for (String key : collection.collection.keySet()) {
            if (collection.collection.get(key).getGovernor().getName().equals(governor.getName()) &
                    Objects.equals(collection.collection.get(key).getGovernor().getHeight(), governor.getHeight()) &
                    Objects.equals(collection.collection.get(key).getGovernor().getBirthday(), governor.getBirthday())) {
                temporary = key;
                break;
            }
        }
        if (!temporary.equals("")) {
            RemoveKey removeKey = new RemoveKey();
            removeKey.removeKey(temporary, collection, login, answer);
//            collection.collection.remove(temporary);
//            Database.removeKey(temporary);
        }
    }
}
