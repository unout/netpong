package netpong.logics;

/* 27.02.2016. */
public class Ball {

    public static final int RADIUS = 10;

    public static final int MAX_SPEED = 10;
    public static final int MIN_SPEED = 5;

    public double getX() {
        return x;
    }
    public double getY() { return y; }

    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }

    public double getSpeed()  { return speed;  }
    public double getSpeedX() { return speedX; }
    public double getSpeedY() { return speedY; }

    public void setSpeed(double speed)   { this.speed = speed;   }
    public void setSpeedX(double speedX) {
        this.speedX = speedX;
    }
    public void setSpeedY(double speedY) {
        this.speedY = speedY;
    }


    private double x;
    private double y;

    private double speed;
    private double speedX;
    private double speedY;

}
