package club.emperorws.orm.plus.toolkit;

import static club.emperorws.orm.plus.consts.StringPool.*;

/**
 * todo
 *
 * @author 892638
 * @date 2022.09.18 01:17
 **/
public class SqlScriptUtils {

    /**
     * <p>
     * 安全入参:  #{入参}
     * </p>
     *
     * @param param 入参
     * @return 脚本
     */
    public static String safeParam(final String param) {
        return safeParam(param, null);
    }

    /**
     * <p>
     * 安全入参:  #{入参,mapping}
     * </p>
     *
     * @param param   入参
     * @param mapping 映射
     * @return 脚本
     */
    public static String safeParam(final String param, final String mapping) {
        String target = HASH_LEFT_BRACE + param;
        if (StringUtils.isBlank(mapping)) {
            return target + RIGHT_BRACE;
        }
        return target + COMMA + mapping + RIGHT_BRACE;
    }
}
