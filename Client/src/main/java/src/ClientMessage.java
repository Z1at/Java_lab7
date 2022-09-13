package src;

import java.io.Serializable;

public class ClientMessage implements Serializable {
    public String command;
    public String login;
    public String password;
    public Object obj;
    public int id;
    public String arg;
    public String[] commands;

    public ClientMessage(String [] commands, String login, String password){
        this.commands = commands;
        this.login = login;
        this.password = password;
    }

    public ClientMessage(String command, Object obj, String arg, String login, String password){
        this.command = command;
        this.obj = obj;
        this.arg = arg;
        this.password = password;
        this.login = login;
    }

    public ClientMessage(String command, Object obj, int id, String arg, String login, String password){
        this.command = command;
        this.arg = arg;
        this.obj = obj;
        this.id = id;
        this.login = login;
        this.password = password;
    }
}
