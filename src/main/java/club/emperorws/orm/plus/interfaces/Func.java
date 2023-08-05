package club.emperorws.orm.plus.interfaces;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * 特殊SQL语法函数SQL拼接
 *
 * @author 892638
 * @date 2022.09.16 19:44
 **/
public interface Func<Children, R> extends Serializable {

    default Children isNull(R column) {
        return isNull(true, column);
    }

    /**
     * IS NULL拼接
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @return this
     */
    Children isNull(boolean condition, R column);

    default Children isNotNull(R column) {
        return isNotNull(true, column);
    }

    /**
     * IS NOT NULL拼接
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @return this
     */
    Children isNotNull(boolean condition, R column);

    default Children in(R column, Collection<?> coll) {
        return in(true, column, coll);
    }

    /**
     * IN 条件拼接
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @param coll      in的数据集合
     * @return this
     */
    Children in(boolean condition, R column, Collection<?> coll);

    default Children in(R column, Object... values) {
        return in(true, column, values);
    }

    /**
     * IN 条件拼接
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @param values    in的数据集合
     * @return this
     */
    Children in(boolean condition, R column, Object... values);

    default Children notIn(R column, Collection<?> coll) {
        return notIn(true, column, coll);
    }

    /**
     * NOT IN 条件拼接
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @param coll      in的数据集合
     * @return this
     */
    Children notIn(boolean condition, R column, Collection<?> coll);

    default Children notIn(R column, Object... values) {
        return notIn(true, column, values);
    }

    /**
     * NOT IN 条件拼接
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @param values    in的数据集合
     * @return this
     */
    Children notIn(boolean condition, R column, Object... values);

    default Children inSql(R column, String inValue) {
        return inSql(true, column, inValue);
    }

    /**
     * IN SQL拼接
     * 例：inSql("id","1,2,3,4,5,6")
     * 例：inSql("id","select id from user where age < 18")
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @param inValue   in的SQL语句
     * @return this
     */
    Children inSql(boolean condition, R column, String inValue);

    default Children notInSql(R column, String inValue) {
        return notInSql(true, column, inValue);
    }

    /**
     * NOT IN SQL拼接
     * 例：notInSql("id","1,2,3,4,5,6")
     * 例：notInSql("id","select id from user where age < 18")
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @param inValue   in的SQL语句
     * @return this
     */
    Children notInSql(boolean condition, R column, String inValue);

    default Children gtSql(R column, String gtValue) {
        return gtSql(true, column, gtValue);
    }

    /**
     * > SQL拼接
     * 例：gtSql("id","1,2,3,4,5,6")
     * 例：gtSql("id","select id from user where age = 18")
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @param gtValue   > 的SQL语句
     * @return this
     */
    Children gtSql(boolean condition, R column, String gtValue);

    default Children geSql(R column, String geValue) {
        return geSql(true, column, geValue);
    }

    /**
     * >= SQL拼接
     * 例：geSql("id","1,2,3,4,5,6")
     * 例：geSql("id","select id from user where age = 18")
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @param geValue   > 的SQL语句
     * @return this
     */
    Children geSql(boolean condition, R column, String geValue);

    default Children ltSql(R column, String geValue) {
        return ltSql(true, column, geValue);
    }

    /**
     * < SQL拼接
     * 例：ltSql("id","1,2,3,4,5,6")
     * 例：ltSql("id","select id from user where age = 18")
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @param geValue   < 的SQL语句
     * @return this
     */
    Children ltSql(boolean condition, R column, String geValue);

    default Children leSql(R column, String leValue) {
        return leSql(true, column, leValue);
    }

    /**
     * <= SQL拼接
     * 例：leSql("id","1,2,3,4,5,6")
     * 例：leSql("id","select id from user where age = 18")
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @param leValue   > 的SQL语句
     * @return this
     */
    Children leSql(boolean condition, R column, String leValue);

    default Children groupBy(R column) {
        return groupBy(true, column);
    }

    /**
     * GROUP BY 条件拼接
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @return this
     */
    Children groupBy(boolean condition, R column);

    default Children groupBy(List<R> columns) {
        return groupBy(true, columns);
    }

    /**
     * GROUP BY 条件拼接
     *
     * @param condition 查询条件是否生效
     * @param columns   列名称集合
     * @return this
     */
    Children groupBy(boolean condition, List<R> columns);

    default Children groupBy(R column, R... columns) {
        return groupBy(true, column, columns);
    }

    /**
     * GROUP BY 条件拼接
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @param columns   更多的列名称
     * @return this
     */
    Children groupBy(boolean condition, R column, R... columns);

    default Children orderByAsc(R column) {
        return orderByAsc(true, column);
    }

    /**
     * ORDER BY 字段 ASC 条件拼接
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @return this
     */
    default Children orderByAsc(boolean condition, R column) {
        return orderBy(true, true, column);
    }

    default Children orderByAsc(List<R> columns) {
        return orderByAsc(true, columns);
    }

    /**
     * ORDER BY 字段 ASC 条件拼接
     *
     * @param condition 查询条件是否生效
     * @param columns   列名称集合
     * @return this
     */
    default Children orderByAsc(boolean condition, List<R> columns) {
        return orderBy(true, true, columns);
    }

    default Children orderByAsc(R column, R... columns) {
        return orderByAsc(true, column, columns);
    }

    /**
     * ORDER BY 字段 ASC 条件拼接
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @param columns   更多的列名称
     * @return this
     */
    default Children orderByAsc(boolean condition, R column, R... columns) {
        return orderBy(true, true, column, columns);
    }

    default Children orderByDesc(R column) {
        return orderByDesc(true, column);
    }

    /**
     * ORDER BY 字段 DESC 条件拼接
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @return this
     */
    default Children orderByDesc(boolean condition, R column) {
        return orderBy(true, false, column);
    }

    default Children orderByDesc(List<R> columns) {
        return orderByDesc(true, columns);
    }

    /**
     * ORDER BY 字段 DESC 条件拼接
     *
     * @param condition 查询条件是否生效
     * @param columns   列名称集合
     * @return this
     */
    default Children orderByDesc(boolean condition, List<R> columns) {
        return orderBy(true, false, columns);
    }

    default Children orderByDesc(R column, R... columns) {
        return orderByDesc(false, column, columns);
    }

    /**
     * ORDER BY 字段 DESC 条件拼接
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @param columns   更多的列名称
     * @return this
     */
    default Children orderByDesc(boolean condition, R column, R... columns) {
        return orderBy(true, false, column, columns);
    }

    /**
     * ORDER BY 条件拼接
     *
     * @param condition 查询条件是否生效
     * @param isAsc     是否是ASC排序
     * @param column    列名称
     * @return this
     */
    Children orderBy(boolean condition, boolean isAsc, R column);

    /**
     * ORDER BY 条件拼接
     *
     * @param condition 查询条件是否生效
     * @param isAsc     是否是ASC排序
     * @param columns   列名称集合
     * @return this
     */
    Children orderBy(boolean condition, boolean isAsc, List<R> columns);

    /**
     * ORDER BY 条件拼接
     *
     * @param condition 查询条件是否生效
     * @param isAsc     是否是ASC排序
     * @param column    列名称
     * @param columns   更多的列名称
     * @return this
     */
    Children orderBy(boolean condition, boolean isAsc, R column, R... columns);

    default Children having(String sqlHaving, Object... params) {
        return having(true, sqlHaving, params);
    }

    /**
     * HAVING 条件拼接
     * 例：having("sum(age) > 100")
     * 例：having("sum(age) > {0}",10)
     *
     * @param condition 查询条件是否生效
     * @param sqlHaving having的SQL语句
     * @param params    拼接SQL语句的参数
     * @return this
     */
    Children having(boolean condition, String sqlHaving, Object... params);

    default Children func(Consumer<Children> func) {
        return func(true, func);
    }

    /**
     * 自定义消费（避免断链）
     * 例：func(i->if(true){i.eq("id","1")} else{i.ne("id","1")})
     *
     * @param condition 查询条件是否生效
     * @param func      消费函数
     * @return this
     */
    Children func(boolean condition, Consumer<Children> func);
}
