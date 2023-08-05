package club.emperorws.orm.plus;

import java.io.Serializable;

/**
 * 通用SQL片段接口（最顶层的接口）
 *
 * @author EmperorWS
 * @date 2022.09.16 17:47
 **/
@FunctionalInterface
public interface ISqlSegment extends Serializable {

    /**
     * SQL片段
     *
     * @return SQL片段
     */
    String getSqlSegment();
}
