package londonSafeTravel.client.gui;

import javax.swing.*;
import java.awt.*;


public class GraphLine extends JPanel {
    private final int[] yCoords;
    private final int startX = 100;
    private final int startY = 100;
    private final int endX = 400;
    private final int endY = 400;
    private int prevX = startX;
    private int prevY = endY;

    public GraphLine(int[] yCoords) {
        this.yCoords = yCoords;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        //We draw in the following 2 loops the grid so it's visible what I explained before about each "unit"
        g2d.setColor(Color.BLUE);
        int unitX = (endX - startX) / 10;
        for (int i = startX; i <= endX; i += unitX) {
            g2d.drawLine(i, startY, i, endY);
        }

        int unitY = (endY - startY) / 10;
        for (int i = startY; i <= endY; i += unitY) {
            g2d.drawLine(startX, i, endX, i);
        }

        //We draw the axis here instead of before because otherwise they would become blue colored.
        g2d.setColor(Color.BLACK);
        g2d.drawLine(startX, startY, startX, endY);
        g2d.drawLine(startX, endY, endX, endY);

        //We draw each of our coords in red color
        g2d.setColor(Color.RED);
        for (int y : yCoords) {
            g2d.drawLine(prevX, prevY, prevX += unitX, prevY = endY - (y * unitY));
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(endX + 100, endY + 100);
    }
}