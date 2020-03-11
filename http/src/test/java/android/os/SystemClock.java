package android.os;

/**
 * @Description: TODO
 * @Author: mingqiang.xu mingqiang.xu@lickincoffee.com
 * @Date: 2019-07-02 19:48
 */
public class SystemClock {

    public static long elapsedRealtime() {
        return System.currentTimeMillis();
    }
}
