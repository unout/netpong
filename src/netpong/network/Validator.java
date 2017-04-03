package netpong.network;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*15/03/2016. */
public class Validator {

    public static Pattern p = Pattern.compile(
        "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public static boolean validationIP(String IP) {
        Matcher m = p.matcher(IP);
        return m.matches();
    }
}
