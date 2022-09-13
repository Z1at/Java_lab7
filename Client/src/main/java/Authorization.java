import NikandrovLab5.utility.TextFormatting;
import NikandrovLab5.utility.Transformation;
import src.ClientMessage;
import src.ServerMessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

public class Authorization {
    public static String[] authorization(DatagramChannel datagramChannel, InetSocketAddress serverAddress) throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        ClientSender clientSender = new ClientSender(datagramChannel, serverAddress);
        ClientReceiver clientReceiver = new ClientReceiver(datagramChannel);

        while (true) {
            System.out.println(TextFormatting.getYellowText("If you want to register, enter \"sign up\", if you want to log in to your account, enter \"sign in\""));
            String command = scanner.nextLine().strip();
            if (command.equals("sign up")) {
                System.out.println(TextFormatting.getYellowText("Enter the login:"));
                String login = scanner.nextLine().strip();
                System.out.println(TextFormatting.getYellowText("Enter the password:"));
                String password = scanner.nextLine().strip();
                ClientMessage clientMessage = new ClientMessage("register".split(" "), login, password);
                clientSender.send(Transformation.Serialization(clientMessage));
                clientReceiver.receive();
            }
            else if(command.equals("sign in")){
                System.out.println(TextFormatting.getYellowText("Enter the login:"));
                String login = scanner.nextLine().strip();
                System.out.println(TextFormatting.getYellowText("Enter the password:"));
                String password = scanner.nextLine().strip();

                ClientMessage clientMessage = new ClientMessage("check".split(" "), login, password);
                clientSender.send(Transformation.Serialization(clientMessage));

                String messageFromServer = clientReceiver.receive();
                if(messageFromServer.equals("You have successfully logged into your account")){
                    String[] user = new String[2]; user[0] = login; user[1] = password;
                    return user;
                }
            }
            else{
                System.out.println(TextFormatting.getRedText("Invalid command"));
            }
        }
    }
}
