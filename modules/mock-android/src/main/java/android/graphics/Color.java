package android.graphics;

import java.util.Locale;

public class Color
{
    public  Color() { throw new RuntimeException("Stub!"); }
    public static  int alpha(int color) { throw new RuntimeException("Stub!"); }
    public static  int red(int color) { throw new RuntimeException("Stub!"); }
    public static  int green(int color) { throw new RuntimeException("Stub!"); }
    public static  int blue(int color) { throw new RuntimeException("Stub!"); }
    public static  int rgb(int red, int green, int blue) { throw new RuntimeException("Stub!"); }
    public static  int argb(int alpha, int red, int green, int blue) { throw new RuntimeException("Stub!"); }
    public static  int parseColor(java.lang.String colorString) {
        if (colorString.charAt(0) == '#') {
            // Use a long to avoid rollovers on #ffXXXXXX
            long color = Long.parseLong(colorString.substring(1), 16);
            if (colorString.length() == 7) {
                // Set the alpha value
                color |= 0x00000000ff000000;
            } else if (colorString.length() != 9) {
                throw new IllegalArgumentException("Unknown color");
            }
            return (int) color;
        } else {
            throw new IllegalArgumentException("Unknown color");
        }
    }
    public static  void RGBToHSV(int red, int green, int blue, float[] hsv) { throw new RuntimeException("Stub!"); }
    public static  void colorToHSV(int color, float[] hsv) { throw new RuntimeException("Stub!"); }
    public static  int HSVToColor(float[] hsv) { throw new RuntimeException("Stub!"); }
    public static  int HSVToColor(int alpha, float[] hsv) { throw new RuntimeException("Stub!"); }
    public static final int BLACK = -16777216;
    public static final int DKGRAY = -12303292;
    public static final int GRAY = -7829368;
    public static final int LTGRAY = -3355444;
    public static final int WHITE = -1;
    public static final int RED = -65536;
    public static final int GREEN = -16711936;
    public static final int BLUE = -16776961;
    public static final int YELLOW = -256;
    public static final int CYAN = -16711681;
    public static final int MAGENTA = -65281;
    public static final int TRANSPARENT = 0;
}