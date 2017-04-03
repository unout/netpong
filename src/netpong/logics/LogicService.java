package netpong.logics;

import netpong.form.NetPongGeneralForm;
import netpong.network.NetworkMessageParser;
import netpong.network.PacketBuilder;
import netpong.network.PacketSender;
import netpong.network.SocketListener;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class LogicService {

    //singleton
    private static volatile LogicService instance;

    public static LogicService getInstance() {
        LogicService localInstance = instance;
        if (localInstance == null) {
            synchronized (LogicService.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new LogicService();
                }
            }
        }
        return localInstance;
    }


    private Ball ball;
    private ArrayList<Player> players = new ArrayList<>();

    private InfoObject infoObject;

    private volatile boolean isActive;
    private volatile boolean isStarted;

    private volatile boolean isNetwork;
    private volatile boolean isServer;
    private volatile boolean isConnected;

    private NetPongGeneralForm form;
    public NetPongGeneralForm getForm() {
        return form;
    }

    private LogicService() {
        form = new NetPongGeneralForm();

        infoObject = new InfoObject();
        setInfoObjectAssociations();

        ball = new Ball();

        Area area = new Area();
        form.getGamePanel().add(area);

        form.getFrame().addKeyListener(new UserService());
    }

    private synchronized void initPlayers() {
        players.clear();
        Dimension size = form.getGamePanel().getSize();
        Player firstPlayer = new Player('w','x','s','d');

        firstPlayer.setLength((int) (0.3 * size.getHeight()));
        firstPlayer.setIsServer(true);
        firstPlayer.setWidth(10);
        firstPlayer.setX(0);
        firstPlayer.setIsReady(false);
        firstPlayer.setY((int) (size.getHeight() - firstPlayer.getLength()) / 2);
        players.add(firstPlayer);


        Player secondPlayer = new Player('i','m','k','j');
        secondPlayer.setLength((int) (0.3 * size.getHeight()));
        secondPlayer.setIsServer(false);
        secondPlayer.setWidth(10);
        secondPlayer.setX((int) size.getWidth() - 13);
        secondPlayer.setIsReady(false);
        secondPlayer.setY((int) (size.getHeight() - secondPlayer.getLength()) / 2);
        players.add(secondPlayer);
    }

    private void setBallToCenterArea() {
        int height = (int) form.getGamePanel().getSize().getHeight();
        int width = (int) form.getGamePanel().getSize().getWidth();

        ball.setX((width - ball.RADIUS) * 0.5);
        ball.setY((height - ball.RADIUS) * 0.5);

    }

    private void setBallRandomSpeed() {
        setBallRandomSpeed(false);
    }

    private void setBallRandomSpeed(boolean isInit) {
        double angle = Math.PI * 0.35 * Math.random();
        double speed = Ball.MIN_SPEED + (Ball.MAX_SPEED - Ball.MIN_SPEED) * Math.random();
        if (isInit) {
            if (speed > 1.5) {
                speed--;
            }
            int rand = ((int) (Math.random() * 100)) % 2;
            speed *= rand == 0 ? 1 : -1;
        }

        ball.setSpeed(speed);
        ball.setSpeedX(speed * Math.cos(angle));
        ball.setSpeedY(speed * Math.sin(angle));
    }

    public boolean isNetwork() {
        return isNetwork;
    }
    public boolean isServer()  { return isServer;  }
    public boolean isActive()  { return isActive;  }

    public Ball getBall() {
        return ball;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    private SocketListener socketListener;

    public void startNetworkAsClient() {
        infoObject.setRole("client");

        if (isNetwork && socketListener != null) {
            socketListener.stop();
        }

        socketListener = new SocketListener(8889, (socket -> {
            try {
                DataInputStream in = new DataInputStream(socket.getInputStream());
                String message = in.readUTF();
                NetworkMessageParser.parse(message);
                String ip = socket.getInetAddress().toString().replace("/", "");
                LogicService
                        .getInstance()
                        .getPlayers()
                        .stream()
                        .filter(Player::isServer)
                        .findFirst()
                        .get()
                        .setIP(ip);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        isNetwork = true;
        isServer = false;
        isConnected = false;
        start();
    }

    public void startNetwork() {
        infoObject.setRole("server");

        if (isNetwork && socketListener != null) {
            socketListener.stop();
        }

        socketListener = new SocketListener(8888, (socket -> {

            try {
                DataInputStream in = new DataInputStream(socket.getInputStream());
                String message = in.readUTF();
                NetworkMessageParser.parse(message);
                String ip = socket.getInetAddress().toString().replace("/", "");
                LogicService
                        .getInstance()
                        .getPlayers()
                        .stream()
                        .filter(x -> !x.isServer())
                        .findFirst()
                        .get()
                        .setIP(ip);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        isNetwork = true;
        isServer = true;
        isConnected = false;
        start();
    }

    public void startLocal() {
        infoObject.setRole("local");
        socketListener = null;
        isNetwork = false;
        isServer = false;
        start();
    }

    public void setIsConnected(boolean isConnected) {

        if (this.isConnected == isConnected) {
            return;
        }

        infoObject.setIsConected(isConnected);
        if (isNetwork && isServer && isConnected) {
            PacketBuilder builder
                    = new PacketBuilder()
                    .setServer()
                    .setOk();
            PacketSender.sendToClient(builder.toString());
        }
        this.isConnected = isConnected;
    }

    private void start() {
        timer.stop();

        infoObject.stopGame();
        infoObject.startGame();
        infoObject.setSecondPlayerPoints(0);
        infoObject.setFirstPlayerPoints(0);

        initPlayers();

        setBallRandomSpeed(true);
        setBallToCenterArea();
        isActive = true;
        isStarted = true;

        if (isNetwork && socketListener != null) {
            socketListener.start();
        }
        timer.start();
    }

    public void pause() {
        if (!isStarted) {
            return;
        }
        timer.stop();
        infoObject.pause();
        isActive = false;
    }

    public void resume() {
        if (!isStarted) {
            return;
        }
        infoObject.startGame();
        isActive = true;
        timer.start();
    }

    public void stop() {
        infoObject.stopGame();
        timer.stop();
        isActive = false;
        isStarted = false;
        socketListener.stop();
    }

    private Timer timer = new Timer(1000 / 24, (e) -> tick());

    private void tick() {
        if (!isActive) {
            return;
        }

        if (isNetwork && !isConnected) {
            return;
        }

        infoObject.setTime();
        redrawPlayers();
        redrawBall();

        if (isNetwork && isServer) {
            int y  = players.stream()
                    .filter(Player::isServer)
                    .findFirst()
                    .get()
                    .getY();
            int x  = players.stream()
                    .filter(Player::isServer)
                    .findFirst()
                    .get()
                    .getX();
            PacketBuilder builder
                    = new PacketBuilder()
                    .setServer()
                    .setBallX((int)ball.getX())
                    .setBallY((int)ball.getY())
                    .setPlayerY(y)
                    .setPlayerX(x);
            PacketSender.sendToClient(builder.toString());
        }

        if (isNetwork && !isServer) {
            int y  = players.stream()
                    .filter(p -> !p.isServer())
                    .findFirst()
                    .get()
                    .getY();
            int x  = players.stream()
                    .filter(p -> !p.isServer())
                    .findFirst()
                    .get()
                    .getX();
            PacketBuilder builder
                    = new PacketBuilder()
                    .setClient()
                    .setPlayerY(y)
                    .setPlayerX(x);
            PacketSender.sendToServer(builder.toString());
        }

        form.getFrame().repaint();
    }

    private void redrawPlayers() {

        int height = (int) form.getGamePanel().getSize().getHeight();
        int  width = (int) form.getGamePanel().getSize().getWidth();
        players.stream().filter(player ->
                !isNetwork ||
                        (isNetwork && isServer == player.isServer()))
                .forEach(player -> {
            if (player.isDownPressed() && (player.getY() + player.getLength()) < height) {
                player.setY(player.getY() + player.getSpeed());
            }
            if (player.isUpPressed() && player.getY() > 0) {
                player.setY(player.getY() - player.getSpeed());
            }
            if (player.getBackChar() == 's' && player.isBackPressed() &&
                    player.getX() > 0) {
                player.setX(player.getX() - player.getSpeed());
            }
            if (player.getForwardChar() == 'd' && player.isForwardPressed() &&
                    player.getX() < width / 2 - 2*player.getWidth()) {
                player.setX(player.getX() + player.getSpeed());
            }
            if (player.getBackChar() == 'k' && player.isBackPressed() &&
                    (player.getX() + player.getWidth()) < width - 3) {
                player.setX(player.getX() + player.getSpeed());
            }
            if (player.getForwardChar() == 'j' && player.isForwardPressed() &&
                    player.getX() > width / 2 + player.getWidth()) {
                player.setX(player.getX() - player.getSpeed());
            }
        });
    }

    private void redrawBall() {

        if (isNetwork && !isServer) {
            return;
        }
        if (!isActive) {
            return;
        }

        boolean isReady = true;
        for (Player player : players) {
            isReady &= player.isReady();
        }

        if (!isReady) { return; }

        int height = (int) form.getGamePanel().getSize().getHeight();
        int  width = (int) form.getGamePanel().getSize().getWidth();

        int playerWidth = 10;
        int playerHeight = 10;

        int shift = 3;//borders

        // dont trouble troubles and troubles dont trouble you

        // first player ************************************************************
        if (ball.getX() < width / 2) {
            Player p = players.get(0);
            if (      (p.getY() < ball.getY() // forward
                    && p.getY() + p.getLength() > ball.getY() + Ball.RADIUS
                    && p.getX() + p.getWidth() >= ball.getX() + ball.getSpeedX()
                    && p.getX() + p.getWidth() / 2 < ball.getX())
                    ||
                      (p.getY() < ball.getY() // back
                    && p.getY() + p.getLength() > ball.getY() + Ball.RADIUS
                    && p.getX() <= ball.getX() + Ball.RADIUS + ball.getSpeedX()
                    && p.getX() + p.getWidth() / 2 > ball.getX())) {
                // setBallRandomSpeed();
                ball.setSpeedX(-ball.getSpeedX());
                ball.setY(ball.getY() + ball.getSpeedY());
            }

            if (       p.getY() <= ball.getY() + ball.getSpeedY() // up
                    && p.getX() < ball.getX()
                    && p.getX() + p.getWidth() > ball.getX() + Ball.RADIUS
                    && p.getY() + p.getWidth() > ball.getY()
                    ||
                      (p.getY() + p.getLength() >= ball.getY() + ball.getSpeedY()  // down
                    && p.getX() < ball.getX()
                    && p.getX() + p.getWidth() > ball.getX()
                    && p.getY() + p.getLength()/2 < ball.getY())) {
                // setBallRandomSpeed();
                ball.setSpeedY(-ball.getSpeedY());
                ball.setX(ball.getX() + ball.getSpeedX());
            }
        }

        // second player **********************************************
        if (ball.getX() > width / 2) {
            Player p = players.get(1);
            if (      (p.getY() < ball.getY() // forward
                    && p.getY() + p.getLength() > ball.getY() + Ball.RADIUS
                    && p.getX() <= ball.getX() + Ball.RADIUS + ball.getSpeedX()
                    && p.getX() + p.getWidth() / 2 > ball.getX())
                    ||
                      (p.getY() < ball.getY() // back
                    && p.getY() + p.getLength() > ball.getY() + Ball.RADIUS
                    && p.getX() + p.getWidth() >= ball.getX() + ball.getSpeedX()
                    && p.getX() + p.getWidth() / 2 < ball.getX())) {
                // setBallRandomSpeed();
                ball.setSpeedX(-ball.getSpeedX());
                ball.setY(ball.getY() + ball.getSpeedY());
            }
            if (       p.getY() <= ball.getY() + Ball.RADIUS + ball.getSpeedY() // up
                    && p.getX() < ball.getX()
                    && p.getX() + p.getWidth() > ball.getX() + Ball.RADIUS
                    && p.getY() + p.getLength() / 2 > ball.getY()
                    ||
                      (p.getY() + p.getLength() >= ball.getY() + ball.getSpeedY()  // down
                    && p.getX() < ball.getX()
                    && p.getX() + p.getWidth() > ball.getX()
                    && p.getY() + p.getLength() / 2 < ball.getY())) {
                // setBallRandomSpeed();
                ball.setSpeedY(-ball.getSpeedY());
                ball.setX(ball.getX() + ball.getSpeedX());
            }
        }


        if (ball.getY() + ball.getSpeedY() <= 0) {
            ball.setSpeedY(-ball.getSpeedY());
            ball.setY(0);
        } else if (ball.getY() + ball.getSpeedY() > height - Ball.RADIUS) {
            ball.setSpeedY(-ball.getSpeedY());
            ball.setY(height - playerHeight);
        } else {
            ball.setY(ball.getY() + ball.getSpeedY());
        }

        if (ball.getX() + ball.getSpeedX() <= playerWidth / 2) {
            for (Player loopPlayer : players) {
                loopPlayer.setIsReady(false);
            }
            players.get(1).setPoints(players.get(1).getPoints() + 1);
            if (isNetwork && isServer) {
                PacketBuilder builder
                        = new PacketBuilder()
                        .setServer()
                        .setClientPlayerPoint(players.get(1).getPoints());
                PacketSender.sendToClient(builder.toString());
            }
            setBallRandomSpeed(true);
            setBallToCenterArea();
        }
        if (ball.getX() + ball.getSpeedX() >= width - playerWidth / 2) {
            for (Player loopPlayer : players) {
                loopPlayer.setIsReady(false);
            }
            players.get(0).setPoints(players.get(0).getPoints() + 1);
            if (isNetwork && isServer) {
                PacketBuilder builder
                        = new PacketBuilder()
                        .setServer()
                        .setServerPlayerPoint(players.get(0).getPoints());
                PacketSender.sendToClient(builder.toString());
            }
            setBallRandomSpeed(true);
            setBallToCenterArea();
        }
        /*
        if (players.get(1).getY() <= ball.getY()
                && players.get(1).getY() + players.get(1).getLength() >= ball.getY()
                && players.get(1).getX() - ball.RADIUS <= ball.getX() + ball.getSpeedX()) {
            int yFactor = ball.getSpeedY() == Math.abs(ball.getSpeedY()) ? 1 : -1;
            setBallRandomSpeed();
            ball.setSpeedY(yFactor * ball.getSpeedY());
            ball.setSpeedX(-ball.getSpeedX());
            ball.setX(ball.getX() + ball.getSpeedX());
        }

        if (ball.getX() + ball.getSpeedX() < players.get(0).getX() + players.get(0).getWidth()) {
            Player player = players.get(0);
            if (player.getY() <= ball.getY()
                    && player.getY() + player.getLength() >= ball.getY()
                    && player.getX() + player.getWidth() >= ball.getX() + ball.getSpeedX()) {
                int yFactor = ball.getSpeedY() == Math.abs(ball.getSpeedY()) ? 1 : -1; // ???
                setBallRandomSpeed();
                ball.setSpeedY(yFactor * ball.getSpeedY());
                ball.setX(ball.getX() + ball.getSpeedX());
            } else {
                for (Player loopPlayer : players) {
                    loopPlayer.setIsReady(false);
                }
                player = players.get(1);
                player.setPoints(player.getPoints() + 1);
                //infoObject.setSecondPlayerPoints(player.getPoints());
                if (isNetwork && isServer) {
                    PacketBuilder builder
                            = new PacketBuilder()
                            .setServer()
                            .setClientPlayerPoint(player.getPoints());
                    PacketSender.sendToClient(builder.toString());
                }
                setBallRandomSpeed(true);
                setBallToCenterArea();
            }
        } else if (ball.getX() + ball.getSpeedX() > players.get(1).getX() - ball.RADIUS) {
            Player player = players.get(1);
            if (player.getY() <= ball.getY()
                    && player.getY() + player.getLength() >= ball.getY()
                    && player.getX() - ball.RADIUS <= ball.getX() + ball.getSpeedX()) {
                int yFactor = ball.getSpeedY() == Math.abs(ball.getSpeedY()) ? 1 : -1;
                setBallRandomSpeed();
                ball.setSpeedY(yFactor * ball.getSpeedY());
                ball.setSpeedX(-ball.getSpeedX());
                ball.setX(ball.getX() + ball.getSpeedX());
            } else {
                for (Player loopPlayer : players) {
                    loopPlayer.setIsReady(false);
                }
                player = players.get(0);
                player.setPoints(player.getPoints() + 1);
                //infoObject.setFirstPlayerPoints(player.getPoints());
                if (isNetwork && isServer) {
                    PacketBuilder builder
                            = new PacketBuilder()
                            .setServer()
                            .setServerPlayerPoint(player.getPoints());
                    PacketSender.sendToClient(builder.toString());
                }
                setBallRandomSpeed(true);
                setBallToCenterArea();
            }
        } else {
            ball.setX(ball.getX() + ball.getSpeedX());
        }
        */
        ball.setX(ball.getX() + ball.getSpeedX());
    }

    public InfoObject getInfoObject() {
        return infoObject;
    }

    private void setInfoObjectAssociations() {
        infoObject.setTimeLabel(form.getTimeLabel());
        infoObject.setFirstPlayerPointsLabel(form.getFirstPlayerPoints());
        infoObject.setSecondPlayerPointsLabel(form.getSecondPlayerPoints());
        infoObject.setConnectionPanel(form.getConnectionPanel());
        infoObject.setRoleLabel(form.getRoleLabel());
    }
}