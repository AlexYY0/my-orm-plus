package club.emperorws.orm.plus.config;

import pro.mickey.MickeySnowflake;

import java.util.Random;

/**
 * 雪花算法的实现
 *
 * @author EmperorWS
 * @date 2022.10.18 16:36
 **/
public class SnowFlakeId {
    private static final MickeySnowflake ME = new MickeySnowflake(new Random().nextInt(500) + new Random().nextInt(500));

    public static Long nextLong() {
        return ME.generateKey();
    }

    public static String nextString() {
        return Long.toHexString(ME.generateKey()).toUpperCase();
    }


    public static String nextString(String prefix) {
        return prefix + Long.toHexString(ME.generateKey()).toUpperCase();
    }
}
