package club.emperorws.orm.plus.interfaces;

import java.io.Serializable;

/**
 * SQL语句拼接
 *
 * @author EmperorWS
 * @date 2022.09.16 19:41
 **/
public interface Join<Children> extends Serializable {

    default Children or() {
        return or(true);
    }

    /**
     * OR 条件拼接
     *
     * @param condition 查询条件是否生效
     * @return this
     */
    Children or(boolean condition);

    default Children apply(String applySql, Object... values) {
        return apply(true, applySql, values);
    }

    /**
     * 拼接SQL
     * 有SQL注入的风险
     *
     * @param condition 查询条件是否生效
     * @param applySql  拼接SQL
     * @param values    SQl字符串拼接数据数组
     * @return this
     */
    Children apply(boolean condition, String applySql, Object... values);

    default Children first(String firstSql) {
        return first(true, firstSql);
    }

    /**
     * 无视规则，直接拼接SQL在起始位置
     * 例：first("selec")
     *
     * @param condition 查询条件是否生效
     * @param firstSql  第一句一句SQL语句
     * @return this
     */
    Children first(boolean condition, String firstSql);

    default Children last(String lastSql) {
        return last(true, lastSql);
    }

    /**
     * 无视规则，直接拼接SQL到最后一句
     * 例：last("limit 1)
     *
     * @param condition 查询条件是否生效
     * @param lastSql   最后一句SQL语句
     * @return this
     */
    Children last(boolean condition, String lastSql);

    default Children comment(String comment) {
        return comment(true, comment);
    }

    /**
     * SQL注释，会拼接SQL的最后
     *
     * @param condition 查询条件是否生效
     * @param comment   SQL语句注释
     * @return this
     */
    Children comment(boolean condition, String comment);

    default Children exists(String existsSql, Object... values) {
        return exists(true, existsSql, values);
    }

    /**
     * 拼接exists
     * 例：exists("select id from user where age = '18'")
     * 有SQL注入的风险
     *
     * @param condition 查询条件是否生效
     * @param existsSql 拼接SQL
     * @param values    SQl字符串拼接数据数组
     * @return this
     */
    Children exists(boolean condition, String existsSql, Object... values);

    default Children notExists(String notExistsSql, Object... values) {
        return notExists(true, notExistsSql, values);
    }

    /**
     * 拼接not exists
     * 例：notExists("select id from user where age = '18'")
     * 有SQL注入的风险
     *
     * @param condition    查询条件是否生效
     * @param notExistsSql 拼接SQL
     * @param values       SQl字符串拼接数据数组
     * @return this
     */
    Children notExists(boolean condition, String notExistsSql, Object... values);
}
