package commands;

import utility.Collection;
import utility.TextFormatting;
import src.ServerMessage;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

public class ExecuteScript {
    public int checkingTheCycle(String path, Operations paths, ServerMessage answer) {
        for (String temporary : paths.paths) {
            if (temporary.equals(path)) {
                answer.plusMessage("In your file with the path: \"" +
                        paths.paths.get(paths.paths.size() - 1) + "\" there is a call to the file that was called before" + '\n');
                paths.paths.clear();
                return 2;
            }
        }
        paths.paths.add(path);
        return 0;
    }

    public int check(String[] concatenation, Collection collection, FileReader file, Operations operations, ServerMessage answer, String login) throws SQLException, IOException {
        System.out.println("check main");
        if (concatenation[0].equals("insert") & concatenation.length == 2) {
//            try {
            System.out.println("insert for file check");
                if (!concatenation[1].contains(",")) {
                    FileOutput.insert(collection, concatenation[1], file, login);
                }
//            } catch (IOException | SQLException ignored) {
//                System.out.println("insert_check");
//            }
        } else if (concatenation[0].equals("update") & concatenation.length == 2) {
            try {
                FileOutput.updateId(collection, Integer.parseInt(concatenation[1]), file);
            } catch (NumberFormatException | IOException | SQLException ignored) {
                System.out.println("update_check");
            }
        } else {
            int result = operations.run(concatenation, collection, answer, operations, login);
            if (result == 2 | result == 3) {
                return 2;
            }
        }
        return 0;
    }

    public int executeScript(String path, Collection collection, Operations operations, ServerMessage answer, String login) {
        System.out.println("Execute script main");
        try {
            File checkFile = new File(path);
            if (!checkFile.canRead()) {
                answer.plusMessage("There is no access to read the file with the path " + path + '\n');
                return 2;
            }
            FileReader file = new FileReader(path);
            String string = "";
            String[] concatenation;
            while (file.ready()) {
                char c = (char) file.read();
                if (c == '\n') {
                    concatenation = string.toLowerCase().trim().split(" ");
                    int result = 0;
                    if (!concatenation[0].equals("")) {
                        result = check(concatenation, collection, file, operations, answer, login);
                    }
                    if (result == 2 | result == 3) {
                        return 2;
                    }
                    string = "";
                } else {
                    string += String.valueOf(c);
                }
            }
            int result = 0;
            if (!string.equals("")) {
                result = check(string.toLowerCase().trim().split(" "), collection, file, operations, answer, login);
            }
            file.close();
            return result;
        } catch (IOException | SQLException e) {
            answer.plusMessage("Invalid path" + '\n');
        }
        return 0;
    }
}
