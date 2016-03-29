package android.os;

public class Looper
{
    Looper() {  }
    public static  void prepare() {  }
    public static  void prepareMainLooper() { }
    public static  android.os.Looper getMainLooper() { return new Looper(); }
    public static  void loop() { throw new RuntimeException("Stub!"); }
    public static  android.os.Looper myLooper() { throw new RuntimeException("Stub!"); }
    public  void setMessageLogging(android.util.Printer printer) { throw new RuntimeException("Stub!"); }
    public static  android.os.MessageQueue myQueue() { throw new RuntimeException("Stub!"); }
    public  void quit() { throw new RuntimeException("Stub!"); }
    public  java.lang.Thread getThread() { return new Thread(); }
    public  void dump(android.util.Printer pw, java.lang.String prefix) { throw new RuntimeException("Stub!"); }
    public  java.lang.String toString() { throw new RuntimeException("Stub!"); }
}