package sample;

import java.awt.*;
import java.awt.image.BufferedImage;

class PixelSelection
{
    private int width, height;
    private boolean[][] selected;
    private boolean[][] boundaryUp;
    private boolean[][] boundaryRight;
    private boolean[][] boundaryDown;
    private boolean[][] boundaryLeft;
    boolean dispose = false;
    private int blink = 0;
    BufferedImage image;

    Controller controller;

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
                graphics.drawImage(info.displayImage, 3, 3, null);

                graphics.setColor(Color.getHSBColor(0.605555556f, ((float) blink + 7) / 22, 1));

                for (int i = 0; i < height; i++)
                {
                    for (int j = 0; j < width; j++)
                    {
                        if (boundaryUp[i][j])
                            graphics.fillRect((int) (j * info.zoom) + 5, (int) (i * info.zoom) + 5 - 2, (int) Math.ceil(info.zoom), 2);

                        if (boundaryDown[i][j])
                            graphics.fillRect((int) (j * info.zoom) + 5, (int) ((i + 1) * info.zoom) + 5, (int) Math.ceil(info.zoom), 2);

                        if (boundaryLeft[i][j])
                            graphics.fillRect((int) (j * info.zoom) + 5 - 2, (int) (i * info.zoom) + 5, 2, (int) Math.ceil(info.zoom));

                        if (boundaryRight[i][j])
                            graphics.fillRect((int) ((j + 1) * info.zoom) + 5, (int) (i * info.zoom) + 5, 2, (int) Math.ceil(info.zoom));
                    }
                }

                blink++;
                blink %= 15;

                controller.pipeline.draw();
            }
        }
    }
}
