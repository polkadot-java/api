package test.org.polkadot;

public class Log {
    public static void e(int tag, String msg) {
        System.err.println("[" + tag + "]" + msg);
    }

    public static void d(int tag, String msg) {
        System.out.println("[" + tag + "]" + msg);
    }

    public static void log() {

    }
}
