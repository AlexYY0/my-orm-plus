package club.emperorws.orm.plus.dialect.dialects;

import club.emperorws.orm.plus.exception.OrmPlusException;

/**
 * GBase 8s V8.8方言处理器
 *
 * @author: EmperorWS
 * @date: 2023/5/19 15:14
 * @description: GBase8sDialect:
 */
public class GBase8sDialect implements IDialect {

    /**
     * 分页的方言SQL生成
     *
     * @param originalSql 原SQL语句
     * @param pageNum     页数
     * @param pageSize    每页的数量
     * @return 方言版的分页SQL语句
     */
    @Override
    public String buildPaginationSql(String originalSql, long pageNum, long pageSize) {
        throw new OrmPlusException("Dialect buildPaginationSql has no implement,later...");
    }

    /**
     * 获取InsertOrUpdate的Sql语句（暂未实现，后续实现）
     *
     * @param entityClass 实体的Class类型
     * @param <T>         实体的类型
     * @return InsertOrUpdate的Sql语句
     */
    @Override
    public <T> String buildInsertOrUpdateSql(Class<T> entityClass) {
        throw new UnsupportedOperationException("will come soon");
    }
}
