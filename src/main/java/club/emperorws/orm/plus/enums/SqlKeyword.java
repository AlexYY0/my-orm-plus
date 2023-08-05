package club.emperorws.orm.plus.enums;

import club.emperorws.orm.plus.ISqlSegment;
import club.emperorws.orm.plus.consts.StringPool;

/**
 * SQL保留关键字枚举值
 *
 * @author EmperorWS
 * @date 2022.09.16 17:51
 **/
public enum SqlKeyword implements ISqlSegment {

    AND("AND"),
    OR("OR"),
    NOT("NOT"),
    IN("IN"),
    NOT_IN("NOT IN"),
    LIKE("LIKE"),
    NOT_LIKE("NOT LIKE"),
    EQ(StringPool.EQUALS),
    NE("<>"),
    GT(StringPool.RIGHT_CHEV),
    GE(">="),
    LT(StringPool.LEFT_CHEV),
    LE("<="),
    IS_NULL("IS NULL"),
    IS_NOT_NULL("IS NOT NULL"),
    GROUP_BY("GROUP BY"),
    HAVING("HAVING"),
    ORDER_BY("ORDER BY"),
    EXISTS("EXISTS"),
    NOT_EXISTS("NOT EXISTS"),
    BETWEEN("BETWEEN"),
    NOT_BETWEEN("NOT BETWEEN"),
    ASC("ASC"),
    DESC("DESC"),
    ON("ON");


    private final String keyword;

    SqlKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String getSqlSegment() {
        return this.keyword;
    }
}
