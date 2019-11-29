public class Hasher {
    public static void main(String[] args) {
        String message = "Group 1 : Introduction to Cryptography";
        System.out.println("\n\nOriginal Message to hash   ==> \'" + message + "\'\n");
        String manualHash = MD5ManualComputer.computeMD5(message);
        System.out.println("Computed Hash by task code ==> " + manualHash + "\n");
    }
}
