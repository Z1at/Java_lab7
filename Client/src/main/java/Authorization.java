import utility.TextFormatting;
import utility.Transformation;
import src.ClientMessage;
import src.ServerMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

public class Authorization {
    private static String login;
    private static String password;
    private static final Thread thread = Thread.currentThread();

    public static String[] authorization(DatagramChannel datagramChannel, InetSocketAddress serverAddress) throws IOException, ClassNotFoundException, InterruptedException {
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

//                text.get();
//                synchronized (thread) {
//                    thread.wait();
//                }
                ClientMessage clientMessage = new ClientMessage("register".split(" "), login, password);
                clientSender.send(Transformation.Serialization(clientMessage));
                clientReceiver.receive();
                login = null; password = null;
            }
            else if(command.equals("sign in")){
                System.out.println(TextFormatting.getYellowText("Enter the login:"));
                String login = scanner.nextLine().strip();
                System.out.println(TextFormatting.getYellowText("Enter the password:"));
                String password = scanner.nextLine().strip();

//                text.get();
//                synchronized (thread) {
//                    thread.wait();
//                }
                ClientMessage clientMessage = new ClientMessage("check".split(" "), login, password);
                clientSender.send(Transformation.Serialization(clientMessage));

                String messageFromServer = clientReceiver.receive();
                if(messageFromServer != null && messageFromServer.equals("You have successfully logged into your account")){
                    String[] user = new String[2]; user[0] = login; user[1] = password;
                    return user;
                }
                login = null; password = null;
            }
            else{
                System.out.println(TextFormatting.getRedText("Invalid command"));
            }
        }
    }

    static class text extends JFrame implements ActionListener{
        static JTextField t;
        static JPasswordField pass;
        static JFrame f;
        static JButton b;
        static JLabel l;
        text(){}

        private static void get(){
            f = new JFrame("Authorization");
            l = new JLabel("Enter login and password");
            b = new JButton("Submit");
            text te = new text();
            b.addActionListener(te);

            t = new JTextField("Enter login", 16);
            pass = new JPasswordField(16);
            Font fo = new Font("Serif", Font.ITALIC, 20);
            t.setFont(fo);
            JPanel p = new JPanel();

            p.add(l);
            p.add(t);
            p.add(pass);
            p.add(b);

            f.add(p);
            f.setSize(300, 300);
            f.show();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String s = e.getActionCommand();
            if(s.equals("Submit")){
                login = t.getText();
                password = pass.getText();
                f.setVisible(false);
                f.dispose();
                synchronized (thread) {
                    thread.notifyAll();
                }
            }
        }
    }
}
