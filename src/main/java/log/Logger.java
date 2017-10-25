package log;

public class Logger {
    static boolean isLog = true;

    public static void log(String msg) {
        if (isLog) {
            System.out.println(msg);
        }
    }
}
