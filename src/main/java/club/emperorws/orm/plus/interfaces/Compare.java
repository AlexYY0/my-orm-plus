package club.emperorws.orm.plus.interfaces;

import java.io.Serializable;
import java.util.Map;
import java.util.function.BiPredicate;

/**
 * 比较查询条件封装
 *
 * @author 892638
 * @date 2022.09.16 18:42
 **/
public interface Compare<Children, R> extends Serializable {

    default <V> Children allEq(Map<R, V> params) {
        return allEq(params, true);
    }

    default <V> Children allEq(Map<R, V> params, boolean null2IsNull) {
        return allEq(true, params, true);
    }

    /**
     * map所有非空属性 =
     *
     * @param condition   查询条件是否生效
     * @param params      所有条件，key是字段名，value是匹配的值
     * @param null2IsNull 是否参数为null时自动执行IS NULL 语句。false则不执行
     * @param <V>         value类型
     * @return this
     */
    <V> Children allEq(boolean condition, Map<R, V> params, boolean null2IsNull);

    default <V> Children allEq(BiPredicate<R, V> filter, Map<R, V> params) {
        return allEq(filter, params, true);
    }

    default <V> Children allEq(BiPredicate<R, V> filter, Map<R, V> params, boolean null2IsNull) {
        return allEq(true, filter, params, true);
    }

    /**
     * map所有非空属性 =
     *
     * @param condition   查询条件是否生效
     * @param filter      返回true，则允许字段查询条件生效
     * @param params      所有条件，key是字段名，value是匹配的值
     * @param null2IsNull 是否参数为null时自动执行IS NULL 语句。false则不执行
     * @param <V>         value类型
     * @return this
     */
    <V> Children allEq(boolean condition, BiPredicate<R, V> filter, Map<R, V> params, boolean null2IsNull);

    default Children eq(R column, Object val) {
        return eq(true, column, val);
    }

    /**
     * 等于 =
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @param val       列值
     * @return this
     */
    Children eq(boolean condition, R column, Object val);

    default Children ne(R column, Object val) {
        return ne(true, column, val);
    }

    /**
     * 不等于 < >
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @param val       列值
     * @return this
     */
    Children ne(boolean condition, R column, Object val);

    default Children gt(R column, Object val) {
        return gt(true, column, val);
    }

    /**
     * 大于 >
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @param val       列值
     * @return this
     */
    Children gt(boolean condition, R column, Object val);

    default Children ge(R column, Object val) {
        return ge(true, column, val);
    }

    /**
     * 大于等于 >=
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @param val       列值
     * @return this
     */
    Children ge(boolean condition, R column, Object val);

    default Children lt(R column, Object val) {
        return lt(true, column, val);
    }

    /**
     * 小于 <
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @param val       列值
     * @return this
     */
    Children lt(boolean condition, R column, Object val);

    default Children le(R column, Object val) {
        return le(true, column, val);
    }

    /**
     * 小于等于 <=
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @param val       列值
     * @return this
     */
    Children le(boolean condition, R column, Object val);

    default Children between(R column, Object val1, Object val2) {
        return between(true, column, val1, val2);
    }

    /**
     * between 值1 and 值2
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @param val1      列值1
     * @param val2      列值2
     * @return this
     */
    Children between(boolean condition, R column, Object val1, Object val2);

    default Children notBetween(R column, Object val1, Object val2) {
        return notBetween(true, column, val1, val2);
    }

    /**
     * not between 值1 and 值2
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @param val1      列值1
     * @param val2      列值2
     * @return this
     */
    Children notBetween(boolean condition, R column, Object val1, Object val2);

    default Children like(R column, Object val) {
        return like(true, column, val);
    }

    /**
     * like '%值%'
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @param val       列值
     * @return this
     */
    Children like(boolean condition, R column, Object val);

    default Children notLike(R column, Object val) {
        return notLike(true, column, val);
    }

    /**
     * not like '%值%'
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @param val       列值
     * @return this
     */
    Children notLike(boolean condition, R column, Object val);

    default Children likeLeft(R column, Object val) {
        return likeLeft(true, column, val);
    }

    /**
     * like '%值'
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @param val       列值
     * @return this
     */
    Children likeLeft(boolean condition, R column, Object val);

    default Children likeRight(R column, Object val) {
        return likeRight(true, column, val);
    }

    /**
     * like '值%'
     *
     * @param condition 查询条件是否生效
     * @param column    列名称
     * @param val       列值
     * @return this
     */
    Children likeRight(boolean condition, R column, Object val);
}
