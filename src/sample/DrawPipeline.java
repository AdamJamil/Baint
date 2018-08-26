package sample;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

class DrawPipeline
{
    CustomJPanel jPanel;

    void resetDisplayImage(ImageInfo info)
    {
        info.displayImage = new BufferedImage(4 + (int) (info.zoom * info.image.getWidth()), 4 + (int) (info.zoom * info.image.getHeight()), BufferedImage.TYPE_INT_ARGB);

        Graphics graphics = info.displayImage.getGraphics();
        graphics.setColor(new java.awt.Color(0xFFD575EA));
        graphics.drawRect(0, 0, info.displayImage.getWidth() - 1, info.displayImage.getHeight() - 1);
        graphics.setColor(new java.awt.Color(0xFFDA9BE8));
        graphics.drawRect(1, 1, info.displayImage.getWidth() - 3, info.displayImage.getHeight() - 3);

        processDisplayImage(info);
    }

    void processDisplayImage(ImageInfo info)
    {
        if (info.zoomImage == null || info.zoomImage.getWidth() != (int) (info.zoom * info.image.getWidth()))
            info.zoomImage = new BufferedImage((int) (info.image.getWidth() * info.zoom), (int) (info.image.getHeight() * info.zoom), BufferedImage.TYPE_INT_ARGB);

        FlipRotateResize.resizePixels(((DataBufferInt) info.image.getRaster().getDataBuffer()).getData(), ((DataBufferInt) info.displayImage.getRaster().getDataBuffer()).getData(),
                info.image.getWidth(), info.image.getHeight(), (int) (info.image.getWidth() * info.zoom), (int) (info.image.getHeight() * info.zoom));

        if (info.selection != null)
            info.selection.image = new BufferedImage(info.displayImage.getWidth() + 6, info.displayImage.getHeight() + 6, BufferedImage.TYPE_INT_ARGB);

        draw();
    }

    void draw()
    {
        jPanel.repaint();
    }

    DrawPipeline(CustomJPanel jPanel)
    {
        this.jPanel = jPanel;
    }
}