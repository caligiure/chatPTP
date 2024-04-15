import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.LinkedList;

public class PTPConnectionManager {
    // the datagram packets will be sent and received on address 230.0.0.1 and port 2000
    private final int multicastPort = 2000;
    private final String strMulticastAddress ="230.0.0.1";

    private final LinkedList<String[]> usersList = new LinkedList<>();

    public synchronized LinkedList<String[]> getUsersList () {
        return new LinkedList<>(usersList);
    }

    class MulticastSender extends Thread {
        private final MulticastSocket mSocket;
        InetAddress multicastAddress;
        DatagramPacket dp;

        public MulticastSender(int serverPort) {
            String strBuf = "" + serverPort;
            byte[] buf = strBuf.getBytes();
            try {
                multicastAddress = InetAddress.getByName(strMulticastAddress);
                mSocket = new MulticastSocket();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            dp = new DatagramPacket(buf, buf.length, multicastAddress, multicastPort);
        }

        public void run() {
            //noinspection InfiniteLoopStatement
            while (true) {
                // Sends a multicast datagram containing the server socket port
                try {
                    mSocket.send(dp);
                    //noinspection BusyWait
                    Thread.sleep(10000);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    public void findUsers () throws IOException {
        InetAddress group = InetAddress.getByName(strMulticastAddress);
        MulticastSocket mSocket = new MulticastSocket(multicastPort);
        //noinspection deprecation
        mSocket.joinGroup(group);
        byte[] buf = new byte[50];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        //noinspection InfiniteLoopStatement
        while (true) {
            mSocket.receive(packet);
            String[] couple = new String[2]; // {address, port}
            InetAddress remoteAddress = packet.getAddress();
            couple[0] = remoteAddress.getHostAddress();
            String received = new String(packet.getData());
            int i = 0;
            while (Character.isDigit(received.charAt(i)))
                i++;
            String receivedPort = received.substring(0, i);
            couple[1] = receivedPort;
            getUsersList().add(couple);
        }
        //mSocket.close();
    }


}
