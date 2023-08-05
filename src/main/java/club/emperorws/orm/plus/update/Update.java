package club.emperorws.orm.plus.update;

import java.io.Serializable;

/**
 * SQL update查询顶层接口
 *
 * @param <Children> this
 * @param <R>        查询字段的类型（一般为String）
 * @author EmperorWS
 * @date 2022.09.18 03:00
 **/
public interface Update<Children, R> extends Serializable {

    /**
     * ignore
     */
    default Children set(R column, Object val) {
        return set(true, column, val);
    }

    /**
     * 设置 更新 SQL 的 SET 片段
     *
     * @param condition 是否加入 set
     * @param column    字段
     * @param val       值
     * @return children
     */
    default Children set(boolean condition, R column, Object val) {
        return set(condition, column, val, null);
    }

    /**
     * ignore
     */
    default Children set(R column, Object val, String mapping) {
        return set(true, column, val, mapping);
    }

    /**
     * 设置 更新 SQL 的 SET 片段
     *
     * @param condition 是否加入 set
     * @param column    字段
     * @param val       值
     * @param mapping   例: javaType=int,jdbcType=NUMERIC
     * @return children
     */
    Children set(boolean condition, R column, Object val, String mapping);

    /**
     * ignore
     */
    default Children setSql(String sql) {
        return setSql(true, sql);
    }

    /**
     * 设置 更新 SQL 的 SET 片段
     *
     * @param sql set sql
     * @return children
     */
    Children setSql(boolean condition, String sql);

    /**
     * 获取 更新 SQL 的 SET 片段
     */
    String getSqlSet();
}
