package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

class CanvasPane extends Pane
{
    private final Canvas canvas;

    CanvasPane(double width, double height)
    {
        canvas = new Canvas(width, height);
        getChildren().add(canvas);
    }

    Canvas getCanvas()
    {
        return canvas;
    }

    @Override
    protected void layoutChildren()
    {
        super.layoutChildren();
        final double x = snappedLeftInset();
        final double y = snappedTopInset();
        final double w = snapSize(getWidth()) - x - snappedRightInset();
        final double h = snapSize(getHeight()) - y - snappedBottomInset();
        canvas.setLayoutX(x);
        canvas.setLayoutY(y);
        canvas.setWidth(w);
        canvas.setHeight(h);
    }
}