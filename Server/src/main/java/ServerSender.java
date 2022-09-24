import utility.Transformation;
import src.ServerMessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerSender {
    private final DatagramChannel channel;
    private SocketAddress clientAddress;

    public ServerSender(DatagramChannel channel){
        this.channel = channel;
    }

    public void setClientAddress(SocketAddress clientAddress) {
        this.clientAddress = clientAddress;
    }

    public void send(ServerMessage serverMessage) throws IOException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(4);
        Runnable task = () -> {
            try {
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
