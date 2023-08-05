package club.emperorws.orm.plus.interfaces;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * 嵌套查询封装
 *
 * @param <Param>    wrapper的字类，嵌套查询的函数方法所有
 * @param <Children> Nested Self
 * @author 892638
 * @date 2022.09.16 18:23
 **/
public interface Nested<Param, Children> extends Serializable {

    default Children and(Consumer<Param> consumer) {
        return and(true, consumer);
    }

    /**
     * AND 嵌套查询
     * 例如：and(i->i.eq("name","张三").ne("status","0"))
     *
     * @param condition 查询条件是否生效
     * @param consumer  嵌套查询函数本身
     * @return this
     */
    Children and(boolean condition, Consumer<Param> consumer);

    default Children or(Consumer<Param> consumer) {
        return or(true, consumer);
    }

    /**
     * OR 嵌套查询
     * 例如：or(i->i.eq("name","张三").ne("status","0"))
     *
     * @param condition 查询条件是否生效
     * @param consumer  嵌套查询函数本身
     * @return this
     */
    Children or(boolean condition, Consumer<Param> consumer);

    default Children not(Consumer<Param> consumer) {
        return not(true, consumer);
    }

    /**
     * NOT 嵌套查询
     * 例如：not(i->i.eq("name","张三").ne("status","0"))
     *
     * @param condition 查询条件是否生效
     * @param consumer  嵌套查询函数本身
     * @return this
     */
    Children not(boolean condition, Consumer<Param> consumer);

    default Children nested(Consumer<Param> consumer) {
        return nested(true, consumer);
    }

    /**
     * nested 嵌套查询，不带AND或OR
     * 例如：nested(i->i.eq("name","张三").ne("status","0"))
     *
     * @param condition 查询条件是否生效
     * @param consumer  嵌套查询函数本身
     * @return this
     */
    Children nested(boolean condition, Consumer<Param> consumer);
}
