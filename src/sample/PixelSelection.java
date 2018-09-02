package sample;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;

class PixelSelection
{
    private int width, height;
    private boolean[][] selected;
    private boolean[][] boundaryUp;
    private boolean[][] boundaryRight;
    private boolean[][] boundaryDown;
    private boolean[][] boundaryLeft;
    boolean lifted = false;
    boolean dispose = false;
    private int blink = 0;
    BufferedImage image;

    private int upBound = -1;
    private int downBound = -1;
    private int leftBound = -1;
    private int rightBound = -1;
    int dragX = -1, dragY = -1;

    Controller controller;

    int[] liftedSection;
    BufferedImage liftedImage;

    void drag(int x, int y)
    {
        dragX += x;
        dragY += y;
    }

    void bound()
    {
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                if (selected[j][i])
                {
                    rightBound = i;
                    break;
                }

        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                if (selected[i][j])
                {
                    downBound = i;
                    break;
                }

        for (int i = width - 1; i >= 0; i--)
            for (int j = height - 1; j >= 0; j--)
                if (selected[j][i])
                {
                    leftBound = i;
                    break;
                }

        for (int i = height - 1; i >= 0; i--)
            for (int j = width - 1; j >= 0; j--)
                if (selected[i][j])
                {
                    upBound = i;
                    break;
                }
    }

    void lift()
    {
        lifted = true;
        bound();

        int sectionWidth = downBound - upBound + 1;
        int sectionHeight = rightBound - leftBound + 1;
        liftedSection = new int[sectionWidth * sectionHeight];

        BufferedImage image = controller.info.image;
        int imageWidth = image.getWidth();
        int[] imageArray = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        double zoom = controller.info.zoom;

        liftedImage = new BufferedImage((int) (sectionHeight * zoom), (int) (sectionWidth * zoom), BufferedImage.TYPE_INT_ARGB);
        controller.info.displayImage.getSubimage((int) (leftBound * zoom) + 2, (int) (upBound * zoom) + 2, (int) (sectionHeight * zoom), (int) (sectionWidth * zoom)).copyData(liftedImage.getRaster());

        for (int i = upBound; i <= downBound; i++)
            for (int j = leftBound; j <= rightBound; j++)
                if (selected[i][j])
                {
                    liftedSection[(i - upBound) * sectionWidth + j - leftBound] = imageArray[i * imageWidth + j];
                    image.setRGB(j, i, 0x00000000);
                }

        controller.pipeline.processDisplayImage(controller.info);
    }

    void place()
    {
        BufferedImage image = controller.info.image;
        int offsetX = leftBound + ((int) (dragX / controller.info.zoom));
        int offsetY = upBound + ((int) (dragY / controller.info.zoom));

        System.out.println(offsetX + " " + offsetY);

        for (int i = Math.max(offsetY, 0); i <= Math.min(offsetY + downBound - upBound, image.getHeight()); i++)
            for (int j = Math.max(offsetX, 0); j <= Math.min(offsetX + rightBound - leftBound, image.getWidth()); j++)
                if (selected[j - offsetX][i - offsetY])
                    image.setRGB(j, i, liftedSection[(i - offsetY) * (downBound - upBound + 1) + (j - offsetX)]);
    }

    void addPixel(int y, int x)
    {
        if (selected[x][y])
            return;

        selected[x][y] = true;

        boundaryUp[x][y] = true;
        boundaryDown[x][y] = true;
        boundaryLeft[x][y] = true;
        boundaryRight[x][y] = true;

        if (y != 0 && boundaryRight[x][y - 1])
        {
            boundaryRight[x][y - 1] = false;
            boundaryLeft[x][y] = false;
        }

        if (y != height - 1 && boundaryLeft[x][y + 1])
        {
            boundaryLeft[x][y + 1] = false;
            boundaryRight[x][y] = false;
        }

        if (x != 0 && boundaryDown[x - 1][y])
        {
            boundaryDown[x - 1][y] = false;
            boundaryUp[x][y] = false;
        }

        if (x != width - 1 && boundaryUp[x + 1][y])
        {
            boundaryUp[x + 1][y] = false;
            boundaryDown[x][y] = false;
        }
    }

    void setRectangle(int y, int x, int endY, int endX)
    {
        for (int i = y; i <= endY; i++)
        {
            for (int j = x; j <= endX; j++)
                selected[j][i] = true;
            boundaryUp[x][i] = true;
            boundaryDown[endX][i] = true;
        }

        for (int i = x; i <= endX; i++)
        {
            boundaryLeft[i][y] = true;
            boundaryRight[i][endY] = true;
        }
    }

    void removePixel(int y, int x)
    {
        if (!selected[x][y])
            return;

        selected[x][y] = false;

        boundaryUp[x][y] = false;
        boundaryDown[x][y] = false;
        boundaryLeft[x][y] = false;
        boundaryRight[x][y] = false;

        if (y != 0 && selected[x][y - 1])
            boundaryRight[x][y - 1] = true;

        if (y != height - 1 && selected[x][y + 1])
            boundaryLeft[x][y + 1] = true;

        if (x != 0 && selected[x - 1][y])
            boundaryDown[x - 1][y] = true;

        if (x != width - 1 && selected[x + 1][y])
            boundaryUp[x + 1][y] = true;
    }

    void reset()
    {
        selected = new boolean[height][width];
        boundaryUp = new boolean[height][width];
        boundaryDown = new boolean[height][width];
        boundaryLeft = new boolean[height][width];
        boundaryRight = new boolean[height][width];
    }

    PixelSelection(Controller controller)
    {
        this.controller = controller;

        width = controller.info.image.getWidth();
        height = controller.info.image.getHeight();
        selected = new boolean[height][width];
        boundaryUp = new boolean[height][width];
        boundaryDown = new boolean[height][width];
        boundaryLeft = new boolean[height][width];
        boundaryRight = new boolean[height][width];

        image = new BufferedImage(controller.info.displayImage.getWidth() + 6, controller.info.displayImage.getHeight() + 6, BufferedImage.TYPE_INT_ARGB);

        new Thread(this::repaintListener).start();
    }

    void repaintListener()
    {
        long time = System.nanoTime();

        while (!dispose)
        {
            if (System.nanoTime() - time > 30000000)
            {
                time = System.nanoTime();

                ImageInfo info = controller.info;
                Graphics graphics = image.getGraphics();
                graphics.setColor(Color.GRAY);
                graphics.fillRect(3, 3, info.displayImage.getWidth(), info.displayImage.getHeight());
                graphics.drawImage(info.displayImage, 3, 3, null);

                graphics.setColor(Color.getHSBColor(0.605555556f, ((float) blink + 7) / 22, 1));

                int zoomDragX = (int) ((int) (dragX / info.zoom) * info.zoom);
                int zoomDragY = (int) ((int) (dragY / info.zoom) * info.zoom);

                for (int i = 0; i < height; i++)
                {
                    for (int j = 0; j < width; j++)
                    {
                        if (boundaryUp[i][j])
                            graphics.fillRect((int) (j * info.zoom) + 5 + zoomDragX, (int) (i * info.zoom) + 5 - 2 + zoomDragY, (int) Math.ceil(info.zoom), 2);

                        if (boundaryDown[i][j])
                            graphics.fillRect((int) (j * info.zoom) + 5 + zoomDragX, (int) ((i + 1) * info.zoom) + 5 + zoomDragY, (int) Math.ceil(info.zoom), 2);

                        if (boundaryLeft[i][j])
                            graphics.fillRect((int) (j * info.zoom) + 5 - 2 + zoomDragX, (int) (i * info.zoom) + 5 + zoomDragY, 2, (int) Math.ceil(info.zoom));

                        if (boundaryRight[i][j])
                            graphics.fillRect((int) ((j + 1) * info.zoom) + 5 + zoomDragX, (int) (i * info.zoom) + 5 + zoomDragY, 2, (int) Math.ceil(info.zoom));
                    }
                }

                if (liftedImage != null)
                    graphics.drawImage(liftedImage, zoomDragX + (int) (leftBound * controller.info.zoom) + 5, zoomDragY + (int) (upBound * controller.info.zoom) + 5, null);

                blink++;
                blink %= 15;

                controller.pipeline.draw();
            }

            try
            {
                Thread.sleep(2);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
