package club.emperorws.orm.plus.dialect.dialects;

import club.emperorws.orm.metadata.TableModelInfo;
import club.emperorws.orm.metadata.TableModelInfoHelper;
import club.emperorws.orm.plus.consts.SqlConstants;
import club.emperorws.orm.plus.consts.StringPool;

import java.util.stream.Collectors;

import static club.emperorws.orm.plus.consts.SqlConstants.LIMIT_PARAM;
import static club.emperorws.orm.plus.consts.SqlConstants.OFFSET_PARAM;

/**
 * MySQL的方言处理器
 *
 * @author: EmperorWS
 * @date: 2023/5/19 15:00
 * @description: MySqlDialect: MySQL的方言处理器
 */
public class MySqlDialect implements IDialect {

    private static final String PAGE_SQL = "%s limit #{%s},#{%s}";

    private static final String INSERT_OR_UPDATE_SQL = "INSERT INTO %s %s VALUES %s ON DUPLICATE KEY UPDATE %s";

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
     * 获取InsertOrUpdate的Sql语句（MySQL的实现是： ON DUPLICATE KEY UPDATE）
     *
     * @param entityClass 实体的Class类型
     * @param <T>         实体的类型
     * @return InsertOrUpdate的Sql语句
     */
    @Override
    public <T> String buildInsertOrUpdateSql(Class<T> entityClass) {
        // 获取实体信息
        TableModelInfo tableInfo = TableModelInfoHelper.getTableInfo(entityClass);
        return String.format(INSERT_OR_UPDATE_SQL, tableInfo.getTableName(),
                StringPool.LEFT_BRACKET + String.join(",", tableInfo.getColumnList()) + StringPool.RIGHT_BRACKET,
                StringPool.LEFT_BRACKET + tableInfo.getPropertyList().stream().map(property -> StringPool.HASH_LEFT_BRACE + SqlConstants.ENTITY + StringPool.DOT + property + StringPool.RIGHT_BRACE).collect(Collectors.joining(",")) + StringPool.RIGHT_BRACKET,
                tableInfo.getFieldInfoList().stream()
                        .filter(fieldInfo -> !fieldInfo.getColumn().equals(tableInfo.getPkName()))
                        .map(fieldInfo -> fieldInfo.getColumn() + StringPool.EQUALS + StringPool.HASH_LEFT_BRACE + SqlConstants.ENTITY + StringPool.DOT + fieldInfo.getProperty() + StringPool.RIGHT_BRACE)
                        .collect(Collectors.joining(",")));
    }
}
