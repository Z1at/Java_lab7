package commands;

import src.ServerMessage;
import utility.Database;
import utility.TextFormatting;
import utility.Collection;

import java.sql.SQLException;

public class RemoveKey {
    public void removeKey(String key, Collection collection, String login, ServerMessage answer) throws SQLException {
        if(collection.creators.get(login) == null){
            answer.plusMessage("You didn't create this element");
            return;
        }
        boolean f = false;
        for(String tmpKey : collection.creators.get(login)){
            if(tmpKey.equals(key)){
                f = true;
                break;
            }
        }
        if(f){
            collection.creators.get(login).remove(key);
            collection.collection.remove(key);
            Database.removeKey(key);
            answer.plusMessage("The element has been deleted");
        }
        else{
            answer.plusMessage("You didn't create this element");
        }

//        collection.collection.remove(key);
//        Database.removeKey(key);
    }
}
