package cookbook.accountcontrol;

import java.io.Console;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class PasswordControl
{
    private String hexadecimalString;
    static String enteredPassword;
    private String encodedPassword;
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
    public String inputPassword()
    {
        //todo: switch the commented out block when building artifacts
        //Console doesn't work for some reason in IDE

        Console console = System.console();
        System.out.println("\nPlease input your password.\n");
        enteredPassword = new String(console.readPassword("Input: "));

        /*
        Scanner passInput = new Scanner(System.in);
        System.out.print("\nPlease input your password.\nInput: ");
        enteredPassword = passInput.next();
        */

        encodedPassword = encodePassword(enteredPassword);
        return encodedPassword;
    }
}
