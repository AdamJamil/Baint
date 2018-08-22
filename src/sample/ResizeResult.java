package sample;

class ResizeResult
{
    double newX, newY;
    String error = "";
    // TODO: 8/19/2018 add resampling type

    ResizeResult(double x, double y)
    {
        newX = x;
        newY = y;
    }

    ResizeResult(String str)
    {
        error = str;
    }
}
