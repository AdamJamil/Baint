package sample;

public class PixelSelection
{
    int width, height;
    boolean[][] selected;
    boolean[][] boundaryUp;
    boolean[][] boundaryRight;
    boolean[][] boundaryDown;
    boolean[][] boundaryLeft;
    boolean dispose = false;
    int blink = 0;

    void addPixel(int x, int y)
    {
        if (selected[x][y])
            return;

        boundaryUp[x][y] = true;
        boundaryDown[x][y] = true;
        boundaryLeft[x][y] = true;
        boundaryRight[x][y] = true;

        if (y != 0)
            boundaryDown[x][y - 1] = false;

        if (y != height)
            boundaryUp[x][y + 1] = false;

        if (x != 0)
            boundaryLeft[x][y - 1] = false;

        if (x != width)
            boundaryRight[x + 1][y] = false;
    }

    PixelSelection(Controller controller)
    {
        width = controller.info.image.getWidth();
        height = controller.info.image.getHeight();
        selected = new boolean[height][width];
        boundaryUp = new boolean[height][width];
        boundaryDown = new boolean[height][width];
        boundaryLeft = new boolean[height][width];
        boundaryRight = new boolean[height][width];

        new Thread(this::repaintListener).start();
    }

    void repaintListener()
    {
        long time = System.nanoTime();

        while (!dispose)
        {
            if (System.nanoTime() - time > 750000000)
            {
                time = System.nanoTime();

                blink++;
                blink %= 3;


            }
        }
    }
}
