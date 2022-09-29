package commands;

import data.City;
import data.Government;
import data.Human;
import utility.Collection;
import utility.Database;
import utility.TextFormatting;
import src.ClientMessage;
import src.ServerMessage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A class for overriding an operation to the desired thread
 */
public class Operations {
    public ArrayList<String> paths = new ArrayList<>();

    //0 - ok, 1 - exit, 2 - same path again, 3 - error in file
    public synchronized int run(String[] clientMessage, Collection collection, ServerMessage answer, Operations operations, String login) {
        System.out.println("Main operation");
        if (clientMessage[0].equals("help") & clientMessage.length == 1){
            Help.create(Help.vocabulary.size());
            answer.plusMessage(Help.help());
        }
        else if(clientMessage[0].equals("info") & clientMessage.length == 1){
            GetInfo getInfo = new GetInfo();
            answer.plusMessage(getInfo.getInfo(collection, collection.initTime));
        }
        else if(clientMessage[0].equals("show") & clientMessage.length == 1){
            Show show = new Show();
            answer.plusMessage(show.show(collection));
        }
        else if(clientMessage[0].equals("clear") & clientMessage.length == 1){
            try {
                Clear clear = new Clear();
                clear.clear(collection, login);
            }
            catch (SQLException ignored){
                System.out.println("clear_operations");
            }
//            Database.clear();
        }
        else if(clientMessage[0].equals("execute_script")){
            System.out.println("Execute script operation");
            ExecuteScript executeScript = new ExecuteScript();
            int result = executeScript.checkingTheCycle(clientMessage[1], operations, answer);
            if(result == 2){
                return 2;
            }
            else{
                int verdict = executeScript.executeScript(clientMessage[1], collection, operations, answer, login);
                if(verdict == 2){
                    return 2;
                }
                operations.paths.remove(operations.paths.size() - 1);
            }
            if(operations.paths.size() == 0){
                answer.plusMessage("All commands was executed" + '\n');
            }
        }
        else if(clientMessage[0].equals("remove_key") & clientMessage.length == 2){
            try {
                RemoveKey removeKey = new RemoveKey();
                removeKey.removeKey(clientMessage[1], collection, login, answer);
            }
            catch (SQLException ignored){
                System.out.println("remove_key_operations");
            }
        }
        else if (clientMessage[0].equals("remove_greater") & clientMessage.length == 2){
            try {
                RemoveGreater removeGreater = new RemoveGreater();
                removeGreater.removeGreater(collection, clientMessage[1], login, answer);
            }
            catch (SQLException ignored){
                System.out.println("remove_greater_operations");
            }
        }
        else if(clientMessage[0].equals("remove_lower") & clientMessage.length == 2){
            try {
                RemoveLower removeLower = new RemoveLower();
                removeLower.removeLower(collection, clientMessage[1], login, answer);
            }
            catch (SQLException ignored){
                System.out.println("remove_lower_operations");
            }
        }
        else if(clientMessage[0].equals("remove_greater_key") & clientMessage.length == 2){
            try {
                RemoveGreaterKey removeGreaterKey = new RemoveGreaterKey();
                removeGreaterKey.removeGreaterKey(clientMessage[1], collection, login, answer);
            }
            catch (SQLException ignored){
                System.out.println("remove_greater_key_operations");
            }
        }
        else if(clientMessage[0].equals("remove_any_by_governor") & clientMessage.length == 4){
            try {
                RemoveAnyByGovernor removeAnyByGovernor = new RemoveAnyByGovernor();
                removeAnyByGovernor.removeAnyByGovernor(new Human(clientMessage[1], Double.parseDouble(clientMessage[2]), LocalDate.parse(clientMessage[3])), collection, login, answer);
            }
            catch (NumberFormatException exception){
                answer.plusMessage("Wrong arguments" + '\n');
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(clientMessage[0].equals("count_less_than_government") & clientMessage.length == 2){
            if (!Government.isPresent(clientMessage[1].toUpperCase())) {
                answer.plusMessage("There is no such element" + '\n');
            } else {
                CountLessThanGovernment countLessThanGovernment = new CountLessThanGovernment();
                answer.plusMessage(String.valueOf(countLessThanGovernment.countLessThanGovernment(Government.valueOf(clientMessage[1].toUpperCase()), collection)));
            }
        }
        else if(clientMessage[0].equals("count_greater_than_government") & clientMessage.length == 2){
            if (!Government.isPresent(clientMessage[1].toUpperCase())) {
                answer.plusMessage("There is no such element" + '\n');
            } else {
                CountGreaterThanGovernment countGreaterThanGovernment = new CountGreaterThanGovernment();
                answer.plusMessage(String.valueOf(countGreaterThanGovernment.countGreaterThanGovernment(Government.valueOf(clientMessage[1].toUpperCase()), collection)));
            }
        }
        else if(clientMessage[0].equals("exit") & clientMessage.length == 1){
//            Save save = new Save();
//            answer.plusMessage(save.save(environmentVariable, collection));
        }
        else{
            if(paths.size() > 0) {
                answer.plusMessage("While executing commands from a file, " +
                        "an error occurred in a file with a path: " + paths.get(paths.size() - 1) + '\n');
                paths.clear();
                return 3;
            }
            answer.plusMessage("Unknown command" + '\n');
        }
        return 0;
    }
}
