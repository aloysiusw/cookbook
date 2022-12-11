package accountcontrol;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class PasswordControl
{
    private String hexadecimalString;
    public String encodePassword(String userPassword)
    {
        MessageDigest digest = null;
        try
        {
            digest = MessageDigest.getInstance("SHA-256"); //built in class to hash string into SHA-256 hash
        }
        catch (NoSuchAlgorithmException e)
        {
            System.out.println(e);
        }
        byte[] encodedHashString = digest.digest(userPassword.getBytes(StandardCharsets.UTF_8));
        byteToHexConverter(encodedHashString);
        return hexadecimalString;
    }
    private void byteToHexConverter(byte[] hash) //convert hash into string to be able to be stored
    {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++)
        {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        this.hexadecimalString = hexString.toString();
    }
}
