package sample;

import javafx.fxml.FXML;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

class FlipRotateResize
{
    static BufferedImage applyZoom(BufferedImage input, double scaleFactorX, double scaleFactorY, int renderingType)
    {
        if (scaleFactorX == 1 && scaleFactorY == 1)
            return input;
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.scale(scaleFactorX, scaleFactorY);
        AffineTransformOp op = new AffineTransformOp(affineTransform, renderingType);
        return op.filter(input, null);
    }

    static void resizePixels(int[] in, int[] out, int w1, int h1, int w2, int h2)
    {
        int x_ratio = ((w1 << 16) / w2) + 1;
        int y_ratio = ((h1 << 16) / h2) + 1;
        int x2, y2 = 0;

        for (int i = 0; i < h2; i++)
        {
            x2 = 0;
            for (int j = 0; j < w2; j++)
            {
                out[((i + 2) * (w2 + 4)) + j + 2] = in[((y2 >> 16) * w1) + (x2 >> 16)];
                x2 += x_ratio;
            }
            y2 += y_ratio;
        }
    }

    @FXML
    void flipV(ImageInfo imageInfo)
    {
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.concatenate(AffineTransform.getScaleInstance(1, -1));
        affineTransform.concatenate(AffineTransform.getTranslateInstance(0, -imageInfo.image.getHeight()));
        AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        imageInfo.image = affineTransformOp.filter(imageInfo.image, null);
    }

    @FXML
    void flipH(ImageInfo imageInfo)
    {
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.concatenate(AffineTransform.getScaleInstance(-1, 1));
        affineTransform.concatenate(AffineTransform.getTranslateInstance(-imageInfo.image.getWidth(), 0));
        AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        imageInfo.image = affineTransformOp.filter(imageInfo.image, null);
    }

    @FXML void rotateCW(ImageInfo imageInfo)
    {
        BufferedImage temp = new BufferedImage(imageInfo.image.getHeight(), imageInfo.image.getWidth(), BufferedImage.TYPE_INT_ARGB);

        int[] in = ((DataBufferInt) imageInfo.image.getRaster().getDataBuffer()).getData();
        int[] out = ((DataBufferInt) temp.getRaster().getDataBuffer()).getData();

        int width = imageInfo.image.getWidth();
        int height = imageInfo.image.getHeight();

        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                out[(i * height) + j] = in[i + (j * width)];

        imageInfo.image = temp;

        flipH(imageInfo);
    }

    @FXML
    void rotateCCW(ImageInfo imageInfo)
    {
        //loop unrolling for efficiency
        rotateCW(imageInfo);
        rotateCW(imageInfo);
        rotateCW(imageInfo);
    }
}
