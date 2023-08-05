package club.emperorws.orm.plus.toolkit;

import club.emperorws.orm.plus.query.QueryWrapper;
import club.emperorws.orm.plus.update.UpdateWrapper;

/**
 * Wrapper查询创建帮助类
 *
 * @author EmperorWS
 * @date 2022.09.19 09:43
 **/
public class WrapperHelper {

    public WrapperHelper() {
    }

    /**
     * 获取 QueryWrapper<T>
     *
     * @param <T> 实体类泛型
     * @return QueryWrapper<T>
     */
    public static <T> QueryWrapper<T> query() {
        return new QueryWrapper<>();
    }

    /**
     * 获取 QueryWrapper<T>
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return QueryWrapper<T>
     * @deprecated 没有实际用处，即将用#{@link #query(Class)}代替
     */
    @Deprecated
    public static <T> QueryWrapper<T> query(T entity) {
        return new QueryWrapper<>(entity);
    }

    /**
     * 获取 QueryWrapper<T>
     *
     * @param entityClass 实体类class类型
     * @param <T>         实体类泛型
     * @return QueryWrapper<T>
     */
    public static <T> QueryWrapper<T> query(Class<T> entityClass) {
        return new QueryWrapper<>(entityClass);
    }

    /**
     * 获取 UpdateWrapper<T>
     *
     * @param <T> 实体类泛型
     * @return UpdateWrapper<T>
     */
    public static <T> UpdateWrapper<T> update() {
        return new UpdateWrapper<>();
    }

    /**
     * 获取 UpdateWrapper<T>
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return UpdateWrapper<T>
     * @deprecated 没有实际用处，即将用#{@link #update(Class)}代替
     */
    @Deprecated
    public static <T> UpdateWrapper<T> update(T entity) {
        return new UpdateWrapper<>(entity);
    }

    /**
     * 获取 UpdateWrapper<T>
     *
     * @param entityClass 实体类class类型
     * @param <T>         实体类泛型
     * @return UpdateWrapper<T>
     */
    public static <T> UpdateWrapper<T> update(Class<T> entityClass) {
        return new UpdateWrapper<>(entityClass);
    }
}
