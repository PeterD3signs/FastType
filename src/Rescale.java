public class Rescale {

    private static final double horDiv = 48;
    private static final double vertDiv = 27;
    private static final double horMin = 384;
    private static final double vertMin = 216;

    public static double Font(double oldWidth, double newWidth, double oldHeight, double newHeight, boolean horRescale, double oldFontSize){

        double newFontSize = oldFontSize;

        if (horRescale && newWidth > horMin){

            if (oldWidth != 0)
                newFontSize *= newWidth / oldWidth;

            if (!(newWidth < oldWidth || newWidth > oldWidth && newFontSize < newHeight / vertDiv)){
                newFontSize = oldFontSize;
            }

        } else if (!horRescale && newHeight > vertMin) {        //vertical rescale

            if (oldHeight != 0)
                newFontSize *= newHeight / oldHeight;

            if (!(newHeight < oldHeight || newHeight > oldHeight && newFontSize < newWidth / horDiv)){
                newFontSize = oldFontSize;
            }

        }

        return newFontSize;

    }
}
