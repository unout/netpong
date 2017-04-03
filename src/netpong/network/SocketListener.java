package netpong.network;

import java.net.ServerSocket;
import java.net.Socket;

/* 02.03.2016.*/
public class SocketListener {

    private final int port;
    private final ISocketWorker worker;

    public SocketListener(int port, ISocketWorker worker) {
        this.port = port;
        this.worker = worker;
    }

    private Thread listener;
    public void start() {
        //TODO if exception occurs all is died and listening not working. Propose way to resolve this problem
        //
         listener = new Thread(() -> {
             try {
                 ServerSocket ss = null;
                 ss = new ServerSocket(port); // when exception refer to this string
                 while (true) {
                     Socket s = null;
                     s = ss.accept();
                     Thread thread = new Thread(new SocketProcessor(s, worker));
                     thread.setDaemon(true);
                     thread.start();
                 }
             } catch (Throwable throwable) {
                 throwable.printStackTrace();
             }

         });
        listener.setDaemon(true);
        listener.start();
    }

    public void stop() {
        if (listener != null && listener.isAlive()) {
            listener.interrupt();
        }
    }

    private static class SocketProcessor implements Runnable {

        private final ISocketWorker socketWorker;
        private Socket s;

        private SocketProcessor(Socket socket, ISocketWorker socketWorker) throws Throwable {
            this.socketWorker = socketWorker;
            this.s = socket;
        }

        public void run() {
            socketWorker.work(s);
        }
    }
}
