package netpong.logics;

import javax.swing.*;
import java.awt.*;

/* 27.02.2016. */
public class Area extends JPanel {

    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        LogicService service = LogicService.getInstance();
        if (!service.isActive()) {
            return;
        }

        Graphics2D graphics2D = (Graphics2D) g;

        Graphics2D graphics2DClone = (Graphics2D) g.create();
        graphics2DClone.setColor(Color.LIGHT_GRAY);
        Stroke dashed = new BasicStroke(1,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL,
                0, new float[]{9}, 0); // what THIS?
        graphics2DClone.setStroke(dashed);
        Dimension size = this.getSize();
        int playerWidth = 10;
        int shift = 1;//border

        graphics2DClone.drawLine(
                playerWidth, 0,
                playerWidth,
                (int)size.getHeight());

        graphics2DClone.drawLine(
                (int)size.getWidth() - playerWidth - shift, 0,
                (int)size.getWidth() - playerWidth - shift,
                (int)size.getHeight());
        graphics2DClone.dispose();

        g.setColor(Color.BLACK);

        for (Player player : service.getPlayers()) {
            graphics2D.drawRect(
                    player.getX(),
                    player.getY(),
                    player.getWidth(),
                    player.getLength());
        }

        graphics2D.drawOval(
                (int) service.getBall().getX(),
                (int) service.getBall().getY(),
                Ball.RADIUS,
                Ball.RADIUS);

    }
}