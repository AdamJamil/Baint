package sample;

import java.awt.image.BufferedImage;
import java.io.File;

class ImageInfo
{
    static double[] zoomLevels = new double[]{0.01, 0.05, 0.1, 0.25, 0.33, 0.5, 0.75, 0.8, 0.9, 1, 1.1, 1.2, 1.4, 1.8, 2.5, 3, 3.5, 4, 5, 6, 7, 8, 9};

    BufferedImage image;
    BufferedImage zoomImage;
    BufferedImage displayImage;
    File file;
    int zoomLevel = 14;
    double zoom = zoomLevels[zoomLevel];
}
