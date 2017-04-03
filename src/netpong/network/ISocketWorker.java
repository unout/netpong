package netpong.network;


import java.net.Socket;

/**
 * Created by Администратор on 03.03.2016.
 */
public interface ISocketWorker {
    void work(Socket socket);
}
