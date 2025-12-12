public class Utils {
    public static String generateRandomIP() {
        return (int)(Math.random() * 256) + "." +
                (int)(Math.random() * 256) + "." +
                (int)(Math.random() * 256) + "." +
                (int)(Math.random() * 256);
    }
}
