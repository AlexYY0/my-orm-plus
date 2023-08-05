package club.emperorws.orm.plus.dialect.dialects;

/**
 * 数据库方言SQL语句的处理顶层接口
 *
 * @author: 892638
 * @date: 2023/5/19 14:43
 * @description: IDialect: 数据库方言SQL语句的处理顶层接口
 */
public interface IDialect {

    /**
     * 分页的方言SQL生成
     *
     * @param originalSql 原SQL语句
     * @param offset      偏移量
     * @param limit       截至的数量
     * @return 方言版的分页SQL语句
     */
    String buildPaginationSql(String originalSql, long offset, long limit);

    /**
     * 获取InsertOrUpdate的Sql语句（）
     *
     * @param entityClass 实体的Class类型
     * @param <T>         实体的类型
     * @return InsertOrUpdate的Sql语句
     */
    <T> String buildInsertOrUpdateSql(Class<T> entityClass);
}
