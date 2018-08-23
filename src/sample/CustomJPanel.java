package sample;

import javafx.embed.swing.SwingNode;

import javax.swing.*;
import java.awt.*;

class CustomJPanel extends JPanel
{
    private final Canvas canvas;
    SwingNode swingNode;
    Controller controller;
    ImageInfo imageInfo;

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        g.clearRect(0, 0, controller.displayWidth, controller.displayHeight);
        if (imageInfo != null)
            g.drawImage(imageInfo.displayImage, controller.dragOffsetX, controller.dragOffsetY, null);
    }

    CustomJPanel(Controller controller)
    {
        this.controller = controller;
        this.imageInfo = controller.imageInfo;

        setBorder(BorderFactory.createLineBorder(Color.black));
        canvas = new Canvas();
        //add(canvas);

        swingNode = new SwingNode();
        swingNode.setVisible(true);

        createSwingContent(swingNode);
    }

    Canvas getCanvas()
    {
        return canvas;
    }

    private void createSwingContent(final SwingNode swingNode)
    {
        SwingUtilities.invokeLater(() -> swingNode.setContent(this));
    }
}