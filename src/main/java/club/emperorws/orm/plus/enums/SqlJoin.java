package club.emperorws.orm.plus.enums;

import club.emperorws.orm.plus.ISqlSegment;

/**
 * sql join枚举
 *
 * @author: 892638
 * @date: 2023/4/10 16:01
 * @description: SqlJoin: sql join枚举
 */
public enum SqlJoin implements ISqlSegment {

    /**
     * 无实际意义，仅标记作用，用于from嵌套子查询的标识
     */
    FROM(null),

    /**
     * 左连接
     */
    LEFT_JOIN("LEFT JOIN"),

    /**
     * 右连接
     */
    RIGHT_JOIN("RIGHT JOIN"),

    /**
     * 内连接
     */
    INNER_JOIN("INNER JOIN"),

    /**
     * 全连接
     */
    FULL_JOIN("FULL JOIN");

    private final String joinWord;

    SqlJoin(String joinWord) {
        this.joinWord = joinWord;
    }

    public String getSqlSegment() {
        return joinWord;
    }
}
