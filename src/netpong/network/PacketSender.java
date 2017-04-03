package netpong.network;


import netpong.logics.LogicService;
import netpong.logics.Player;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ������������� on 03.03.2016.
 */
// TODO maybe make PacketSender`s fields non static and create instance for each players
public class PacketSender {

    // TODO suggest better way to check last packet
    private volatile static String lastClientPacket;
    private volatile static String lastServerPacket;

    public static void sendToServer(String message) {
        if (lastServerPacket == null || !lastServerPacket.equals(message)) {
            String ip = LogicService
                    .getInstance()
                    .getPlayers()
                    .stream()
                    .filter(Player::isServer)
                    .findFirst()
                    .get()
                    .getIP();
            send(message, ip, 8888);
        }
        lastServerPacket = message;
    }

    public static void sendToClient(String message) {
        if (lastClientPacket == null || !lastClientPacket.equals(message)) {
            String ip = LogicService
                    .getInstance()
                    .getPlayers()
                    .stream()
                    .filter(x -> !x.isServer())
                    .findFirst()
                    .get()
                    .getIP();
            send(message, ip, 8889);
        }
        lastClientPacket = message;
    }
    // TODO now new instance of Socket is used for each sending. We need to rebuild logics and use one instance for each sending

    private static Map<String, Socket> dictionary = new HashMap<>();
    /*
    private static volatile Socket instance;
    public static Socket getInstance()
        Socket socketInstance = instance;
        if (socketInstance == null) {
            synchronized (Socket.class) {
                socketInstance = instance;
                if (socketInstance == null) {
                    instance = socketInstance = new Socket();
                }
            }
        }
        return socketInstance;
    }

    public static void conn(InetAddress ip, int port) {
        try {
            Socket socket = new Socket(ip, port);
        }catch (IOException e) {
            System.out.println("Error connecting to server.");
        }
    }
    */

    private static synchronized void send(String message, String address, int port) {
        Thread thread = new Thread(() -> {
            //String address = "127.0.0.1";
            // synchronized (Socket) ???

             try {
                Socket socket;
                InetAddress ipAddress = InetAddress.getByName(address);

                if (dictionary.containsKey(address + ":" + String.valueOf(port))) {
                    socket = dictionary.get(address + ":" + String.valueOf(port));
                    System.out.println(address + ":" + String.valueOf(port) + " 2 " + socket);

                } else {
                    dictionary.put(address + ":" + String.valueOf(port), socket = new Socket(ipAddress, port));
                    //socket = dictionary.get(ipAddress.toString() + ":" + String.valueOf(port)); // deja vu: socket = null
                    System.out.println(address + ":" + String.valueOf(port) + " 3 " + socket);
                }
                OutputStream sout = socket.getOutputStream();
                DataOutputStream out = new DataOutputStream(sout); // here
                out.writeUTF(message);
                out.flush();
                //out.close(); // or not? I create each time when sending
                sout.flush();
                //sout.close();
                //socket.close();

            } catch (Exception e) {
                System.out.println("Exception " + e);
            }
        });
        thread.start();
    }

    private static void send1(String message, String address, int port) {
        Thread thread = new Thread(() -> {
            //String address = "127.0.0.1";
            try {
                InetAddress ipAddress = InetAddress.getByName(address);
                Socket socket = new Socket(ipAddress, port);
                OutputStream sout = socket.getOutputStream();
                DataOutputStream out = new DataOutputStream(sout);
                out.writeUTF(message);
                out.flush();
                out.close();
                sout.flush();
                sout.close();
                socket.close();

            } catch (Exception ignored) {

            }
        });
        thread.start();
    }
}
