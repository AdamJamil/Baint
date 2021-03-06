package sample;

import javafx.embed.swing.SwingNode;

import javax.swing.*;
import java.awt.*;

class CustomJPanel extends JPanel
{
    private final Canvas canvas;
    SwingNode swingNode;
    Controller controller;

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        g.clearRect(0, 0, controller.displayWidth, controller.displayHeight);
        if (controller.info != null)
        {
            if (controller.info.selection == null)
                g.drawImage(controller.info.displayImage, controller.dragOffsetX, controller.dragOffsetY, null);
            else
                g.drawImage(controller.info.selection.image, controller.dragOffsetX - 3, controller.dragOffsetY - 3, null);
        }
        else
            System.out.println("lol");
    }

    CustomJPanel(Controller controller)
    {
        this.controller = controller;

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