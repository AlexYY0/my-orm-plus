package club.emperorws.orm.plus.dialect.dialects;

import static club.emperorws.orm.plus.consts.SqlConstants.LIMIT_PARAM;
import static club.emperorws.orm.plus.consts.SqlConstants.OFFSET_PARAM;

/**
 * Oracle新版本方言处理器
 *
 * @author: 892638
 * @date: 2023/5/19 15:08
 * @description: Oracle12cDialect: Oracle新版本方言处理器
 */
public class Oracle12cDialect implements IDialect {

    private static final String PAGE_SQL = "%s OFFSET #{%s} ROWS FETCH NEXT #{%s} ROWS ONLY";

    /**
     * 分页的方言SQL生成
     *
     * @param originalSql 原SQL语句
     * @param offset      偏移量
     * @param limit       截至的数量
     * @return 方言版的分页SQL语句
     */
    @Override
    public String buildPaginationSql(String originalSql, long offset, long limit) {
        return String.format(PAGE_SQL, originalSql, OFFSET_PARAM, LIMIT_PARAM);
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
