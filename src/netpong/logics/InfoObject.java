package netpong.logics;

import javax.swing.*;
import java.awt.*;
import java.util.Formatter;

public class InfoObject {

    public InfoObject() {
        stopGame();
    }

    public void startGame() {
        startTick = System.currentTimeMillis();
    }
    public void stopGame() {

        startTick = 0;
        beforePauseTick = 0;
        previousTick = 0;
    }

    private long startTick;
    private long beforePauseTick;
    private volatile long previousTick;

    private JPanel connectionPanel;

    private JLabel timeLabel;
    private JLabel roleLabel;
    private JLabel firstPlayerPointsLabel;
    private JLabel secondPlayerPointsLabel;

    public void setFirstPlayerPointsLabel(JLabel firstPlayerPointsLabel) {
        this.firstPlayerPointsLabel = firstPlayerPointsLabel;
    }

    public void setSecondPlayerPointsLabel(JLabel secondPlayerPointsLabel) {
        this.secondPlayerPointsLabel = secondPlayerPointsLabel;
    }

    public void setFirstPlayerPoints(int firstPlayerPoints) {
        if (firstPlayerPointsLabel != null) {
            firstPlayerPointsLabel.setText(String.valueOf(firstPlayerPoints));
        }
    }

    public void setSecondPlayerPoints(int secondPlayerPoints) {
        if (secondPlayerPointsLabel != null) {
            secondPlayerPointsLabel.setText(String.valueOf(secondPlayerPoints));
        }
    }

    public void setConnectionPanel(JPanel connectionPanel) {
        this.connectionPanel = connectionPanel;
    }

    public void setRoleLabel(JLabel roleLabel) {
        this.roleLabel = roleLabel;
    }

    public void setRole(String role) {
        role = role.toUpperCase();
        if (connectionPanel != null && !role.equals("LOCAL")) {
            connectionPanel.setVisible(true);
        }
        roleLabel.setText(role);
    }

    public void setIsConected(boolean isConected) {
        if (connectionPanel != null) {
            connectionPanel.setBackground(isConected ? Color.GREEN : Color.RED);
            connectionPanel.repaint();
        }
    }


    public void setTimeLabel(JLabel timeLabel) {
        this.timeLabel = timeLabel;
    }

    public void setTime() {
        if (System.currentTimeMillis() - previousTick > 500) {
            previousTick = System.currentTimeMillis();
            long seconds = (previousTick - startTick + beforePauseTick)/1000;
            if (timeLabel != null) {
                String text = new Formatter().format(
                        "%02d:%02d:%02d",
                         seconds / 3600,
                        (seconds % 3600) / 60,
                         seconds % 60)
                        .toString();
                timeLabel.setText(text);
            }
        }
    }

    public void pause() {
        beforePauseTick = System.currentTimeMillis() - startTick + beforePauseTick;
    }
}