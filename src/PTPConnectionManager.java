public class MulticastManager {
    // the datagram packet must be sent to address 230.0.0.1 on port 2000
    private final int multicastPort = 2000;
    private final String strMulticastAddress ="230.0.0.1";

    public void sendPort(int port){
        new MulticastSender(port).start();
    }
}
