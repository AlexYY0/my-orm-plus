package club.emperorws.orm.plus.toolkit;

import club.emperorws.orm.plus.consts.SqlConstants;
import club.emperorws.orm.plus.enums.SqlLike;

/**
 * SqlUtils工具类
 *
 * @author 892638
 * @date 2022.09.18 01:52
 **/
public class SqlUtils implements SqlConstants {


    /**
     * 用%连接like
     *
     * @param str 原字符串
     * @return like 的值
     */
    public static String concatLike(Object str, SqlLike type) {
        switch (type) {
            case LEFT:
                return PERCENT + str;
            case RIGHT:
                return str + PERCENT;
            default:
                return PERCENT + str + PERCENT;
        }
    }
}
