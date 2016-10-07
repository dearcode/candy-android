package net.dearcode.candy.util;

/**
 *  * Created by c-wind on 2016/9/21 16:11
 *  * mail：root@codecn.org
 *  
 */
public class Common {
    public static String LOG_TAG = "Candy";
    public static String GetString(CharSequence cs) {
        if (cs == null)
            return "";

        if (cs.length() == 0)
            return "";

        return cs.toString();
    }
}
