import utility.TextFormatting;
import utility.Transformation;
import src.ServerMessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerSender extends Thread{
    private static DatagramChannel channel = null;
    private static SocketAddress clientAddress;

    public ServerSender(DatagramChannel channel){
        ServerSender.channel = channel;
    }

    public void setClientAddress(SocketAddress clientAddress) {
        ServerSender.clientAddress = clientAddress;
    }

    public static void send(ServerMessage serverMessage) {
        ExecutorService pool = Executors.newFixedThreadPool(4);
        Runnable task = () -> {
            try {
                System.out.println(TextFormatting.getRedText(String.valueOf(Thread.currentThread())));
                int i = 0;
                for (; i + 9000 < serverMessage.message.length(); i += 9000) {
                    channel.send(Transformation.Serialization(new ServerMessage(serverMessage.message.substring(i, i + 9000))), clientAddress);
                    Thread.sleep(50);
                }

                if (i < serverMessage.message.length()) {
                    channel.send(Transformation.Serialization(new ServerMessage(serverMessage.message.substring(i))), clientAddress);
                    Thread.sleep(50);
                }

                channel.send(Transformation.Serialization(new ServerMessage("end")), clientAddress);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        };

        pool.execute(task);
        pool.shutdown();
    }
}
