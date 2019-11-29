import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class MD5APIComputer {
    static String computeMD5(String message) {
        byte[] bytesOfMessage;
        MessageDigest md;
        try {
            bytesOfMessage = message.getBytes(StandardCharsets.UTF_8);
            md = MessageDigest.getInstance("MD5");
            byte[] theDigest = md.digest(bytesOfMessage);
            return MD5ManualComputer.toHexString(theDigest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "INVALID TO HASH DUE TO :: " + e.getMessage();
        }
    }
}
