package netpong.logics;


import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class Player {

    public Player(char up, char down, char back, char forward) {
             upChar = up;
           downChar = down;
           backChar = back;
        forwardChar = forward;
    }

    private int length;
    private int width;
    private int x;
    private int y;
    private int speed = 3;
    private int points;

    private String name;
    private int port;

    private volatile boolean isReady;
    private volatile boolean isDownPressed;
    private volatile boolean isUpPressed;
    private volatile boolean isForwardPressed;
    private volatile boolean isBackPressed;

    private volatile boolean isServer;

    private char upChar;
    private char downChar;
    private char backChar;
    private char forwardChar;

    private String IP;

    public int getLength() {
        return length;
    }
    public int getWidth()  { return width;  }

    public void setLength(int length) {
        this.length = length;
    }
    public void setWidth(int width)   { this.width = width;   }

    public int getX() {
        return x;
    }
    public int getY() { return y; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    public int getSpeed() { return speed; }
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public boolean isDownPressed()    { return isDownPressed;    }
    public boolean isUpPressed()      { return isUpPressed;      }
    public boolean isBackPressed()    { return isBackPressed;    }
    public boolean isForwardPressed() { return isForwardPressed; }

    public void setIsDownPressed(boolean isDownPressed)       { this.isDownPressed = isDownPressed;       }
    public void setIsUpPressed(boolean isUpPressed)           { this.isUpPressed = isUpPressed;           }
    public void setIsBackPressed(boolean isBackPressed)       { this.isBackPressed = isBackPressed;       }
    public void setIsForwardPressed(boolean isForwardPressed) { this.isForwardPressed = isForwardPressed; }

    public char getUpChar()      { return upChar;      }
    public char getDownChar()    { return downChar;    }
    public char getBackChar()    { return backChar;    }
    public char getForwardChar() { return forwardChar; }

    public void setUpChar(char upChar)           { this.upChar = upChar;           }
    public void setDownChar(char downChar)       { this.downChar = downChar;       }
    public void setBackChar(char backChar)       { this.backChar = backChar;       }
    public void setForwardChar(char forwardChar) { this.forwardChar = forwardChar; }

    public boolean isReady() {
        return isReady;
    }

    public void setIsReady(boolean isReady) {
        this.isReady = isReady;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
        if (isServer) {
            LogicService.getInstance().getInfoObject().setFirstPlayerPoints(points);
        } else {
            LogicService.getInstance().getInfoObject().setSecondPlayerPoints(points);
        }
    }

    public boolean isServer() {
        return isServer;
    }

    public void setIsServer(boolean isServer) {
        this.isServer = isServer;
    }

    public String getIP() {
        return IP;
    }

    public String getName() {
        return name;
    }
    public int getPort() {
        return port;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public void setIP(String IP) {
        this.IP = IP;
    }

    public Player(String name, int port, String IP) {
        this.name = name;
        this.port = port;
        this.IP = IP;
    }

    ArrayList<Player> clients = LogicService.getInstance().getPlayers();

    public synchronized void addPlayer(Player Player) {
        clients.stream().filter(PlayerIn -> clients.contains(Player)).forEach(PlayerIn ->
                System.out.println("Player with this name/port/IP already exist."));
        clients.add(Player);
    }

    private void removePlayer(Player Player)
    {
        boolean wasRegistered = false;
        synchronized (this)
        {  wasRegistered = clients.remove(Player);  }
        if (wasRegistered)
            broadcast(Player, "--- " + Player.getName() + " left ---");
    }
    private void broadcast(Player fromPlayer, String msg)
    {
        // Copy Player list (don't want to hold lock while doing IO)
        List<Player> existPlayers = null;
        synchronized (this)
        {  existPlayers = new ArrayList<>(this.clients);  }
        for (Player Player : clients)
        {
            if (Player.equals(fromPlayer))
                continue;
            try
            {  Player.write(msg + "\r\n");  }
            catch (Exception e) {

            }
        }
    }
    private Writer output;
    public void write(String msg) throws IOException
    {
        output.write(msg);
        output.flush();
    }

}