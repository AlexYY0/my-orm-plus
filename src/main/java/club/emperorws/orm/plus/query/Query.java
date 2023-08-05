package club.emperorws.orm.plus.query;

import club.emperorws.orm.metadata.TableModelFieldInfo;

import java.io.Serializable;
import java.util.function.Predicate;

/**
 * SQL select查询顶层接口
 *
 * @param <Children> this
 * @param <T>        实体类型
 * @param <R>        查询字段的类型（一般为String）
 * @author EmperorWS
 * @date 2022.09.18 02:43
 **/
public interface Query<Children, T, R> extends Serializable {

    /**
     * ignore
     */
    Children select(R... columns);

    /**
     * ignore
     * <p>注意只有内部有 entity 才能使用该方法</p>
     */
    default Children select(Predicate<TableModelFieldInfo> predicate) {
        return select(null, predicate);
    }

    /**
     * 过滤查询的字段信息(主键除外!)
     * <p>例1: 只要实体对象字段名以 "test" 开头的               -> select(i -&gt; i.getProperty().startsWith("test"))</p>
     * <p>例2: 只要数据库字段名以 "test" 开头的               -> select(i -&gt; i.getColumn().startsWith("test"))</p>
     * <p>例3: 只要 java 字段属性是 String 类型的            -> select(i -&gt; i.getField().getType().getName().equals(String.class.getName()))</p>
     * <p>例4: 要全部字段                                   -> select(i -&gt; true)</p>
     * <p>例5: 只要主键字段                                 -> select(i -&gt; false)</p>
     *
     * @param predicate 过滤方式
     * @return children
     */
    Children select(Class<T> entityClass, Predicate<TableModelFieldInfo> predicate);

    /**
     * 查询条件 SQL 片段
     */
    String getSqlSelect();
}
