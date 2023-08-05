package club.emperorws.orm.plus.dialect.dialects;

import club.emperorws.orm.annotations.AnnModel;
import club.emperorws.orm.metadata.TableModelFieldInfo;
import club.emperorws.orm.metadata.TableModelInfo;
import club.emperorws.orm.metadata.TableModelInfoHelper;
import club.emperorws.orm.plus.consts.SqlConstants;

import java.util.List;

import static club.emperorws.orm.plus.consts.SqlConstants.LIMIT_PARAM;
import static club.emperorws.orm.plus.consts.SqlConstants.OFFSET_PARAM;
import static club.emperorws.orm.plus.consts.StringPool.*;

/**
 * Oracle方言处理器
 *
 * @author: EmperorWS
 * @date: 2023/5/19 15:05
 * @description: OracleDialect: Oracle方言处理器
 */
public class OracleDialect implements IDialect {

    private static final String PAGE_SQL = "with subTable as (%s) " +
            "  select * from ( select tt.*, ROWNUM as rowno  from(select * from subTable) tt where ROWNUM <= #{%s} )tableAlias " +
            "where tableAlias.rowno >= #{%s}";

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
        return String.format(PAGE_SQL, originalSql, LIMIT_PARAM, OFFSET_PARAM);
    }

    /**
     * 获取InsertOrUpdate的Sql语句（oracle的实现是：merge into）
     *
     * @param entityClass 实体的Class类型
     * @param <T>         实体的类型
     * @return InsertOrUpdate的Sql语句
     */
    @Override
    public <T> String buildInsertOrUpdateSql(Class<T> entityClass) {
        // 获取实体信息
        TableModelInfo tableInfo = TableModelInfoHelper.getTableInfo(entityClass);
        // 生成SQL语句
        StringBuilder mergeColumn = new StringBuilder();
        StringBuilder mergeCondition = new StringBuilder();
        StringBuilder mergeUpdate = new StringBuilder();
        StringBuilder mergeInsertColum = new StringBuilder();
        StringBuilder mergeInsertValue = new StringBuilder();
        StringBuilder columnSql = new StringBuilder();
        // 生成sql语句
        makeInsertOrUpdateSqlBuilder(mergeColumn, mergeCondition, mergeUpdate, mergeInsertColum, mergeInsertValue, columnSql, tableInfo);
        return makeSqlBuilder2Sql(tableInfo.getTableName(), mergeColumn, mergeCondition, mergeUpdate, mergeInsertColum, mergeInsertValue, columnSql);
    }


    /**
     * 生成数据入库sql
     *
     * @param mergeColumn      merge into和insert都可以使用
     * @param mergeCondition   merge into的 on condition使用
     * @param mergeUpdate      merge into的update语句
     * @param mergeInsertColum merge into的insert列名部分
     * @param mergeInsertValue merge into的insert列值部分
     * @param columnSql        纯insert语句使用
     * @param tableInfo        实体信息
     */
    private static void makeInsertOrUpdateSqlBuilder(StringBuilder mergeColumn, StringBuilder mergeCondition, StringBuilder mergeUpdate, StringBuilder mergeInsertColum, StringBuilder mergeInsertValue, StringBuilder columnSql, TableModelInfo tableInfo) {
        // 获取实体信息
        List<TableModelFieldInfo> fieldInfoList = tableInfo.getFieldInfoList();

        for (TableModelFieldInfo tableModelFieldInfo : fieldInfoList) {
            AnnModel.AnnField annoField = tableModelFieldInfo.getField().getAnnotation(AnnModel.AnnField.class);
            String annoColumn = annoField.column();
            String annoType = annoField.jdbcType();
            columnSql.append(annoColumn).append(",");
            mergeColumn.append(" " + HASH_LEFT_BRACE + SqlConstants.ENTITY + DOT).append(tableModelFieldInfo.getProperty()).append(RIGHT_BRACE).append(" AS ").append(annoColumn).append(",");
            mergeInsertColum.append("t1.").append(annoColumn).append(",");
            mergeInsertValue.append("t2.").append(annoColumn).append(",");
            if (annoField.isInsertOrUpdateCondition()) {
                mergeCondition.append(" AND ").append(" t1.").append(annoColumn).append("=").append("t2.").append(annoColumn);
            } else {
                mergeUpdate.append(" t1.").append(annoColumn).append("=").append("t2.").append(annoColumn).append(",");
            }
        }
    }

    /**
     * 生成执行sql
     *
     * @param tableName        表名称
     * @param mergeColumn      merge into和insert都可以使用
     * @param mergeCondition   merge into的 on condition使用
     * @param mergeUpdate      merge into的update语句
     * @param mergeInsertColum merge into的insert列名部分
     * @param mergeInsertValue merge into的insert列值部分
     * @param columnSql        纯insert语句使用
     * @return 执行sql
     */
    private static String makeSqlBuilder2Sql(String tableName, StringBuilder mergeColumn, StringBuilder mergeCondition, StringBuilder mergeUpdate, StringBuilder mergeInsertColum, StringBuilder mergeInsertValue, StringBuilder columnSql) {
        String sql;
        if (mergeCondition.length() != 0) {
            // 生成merge into的sql，兼顾insert与update
            sql = "MERGE INTO " + tableName + " t1 USING (SELECT " + mergeColumn.substring(0, mergeColumn.lastIndexOf(",")) + " FROM DUAL) t2  ON ( " + mergeCondition.toString().replaceFirst("AND", "") + " ) " +
                    " WHEN MATCHED THEN UPDATE SET " + mergeUpdate.substring(0, mergeUpdate.lastIndexOf(",")) +
                    " WHEN NOT MATCHED THEN INSERT (" + mergeInsertColum.substring(0, mergeInsertColum.lastIndexOf(",")) + ")VALUES(" + mergeInsertValue.substring(0, mergeInsertValue.lastIndexOf(",")) + ")";
        } else {
            // 生成insert sql
            sql = "INSERT INTO " + tableName +
                    "(" + columnSql.substring(0, columnSql.toString().lastIndexOf(",")) + ")" +
                    " SELECT " + mergeColumn.substring(0, mergeColumn.lastIndexOf(",")) + " FROM DUAL";
        }
        return sql;
    }
}
