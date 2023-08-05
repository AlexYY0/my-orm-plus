package club.emperorws.orm.plus.update;

import club.emperorws.orm.plus.AbstractWrapper;
import club.emperorws.orm.plus.consts.SqlConstants;
import club.emperorws.orm.plus.consts.StringPool;
import club.emperorws.orm.plus.dto.PreparedSql;
import club.emperorws.orm.plus.dto.SharedString;
import club.emperorws.orm.plus.segments.MergeSegments;
import club.emperorws.orm.plus.toolkit.CollectionUtils;
import club.emperorws.orm.plus.toolkit.StringUtils;
import club.emperorws.orm.plus.toolkit.TableInfoHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Update 条件封装
 *
 * @author EmperorWS
 * @date 2022.09.18 15:53
 **/
public class UpdateWrapper<T> extends AbstractWrapper<T, String, UpdateWrapper<T>>
        implements Update<UpdateWrapper<T>, String> {

    /**
     * SQL 更新字段内容，例如：name='1', age=2
     */
    private final List<String> sqlSet;

    /**
     * 最终生成的SQL
     */
    private PreparedSql preparedSql = new PreparedSql();

    public UpdateWrapper() {
        // 如果无参构造函数，请注意实体 NULL 情况 SET 必须有否则 SQL 异常
        this((T) null);
    }

    public UpdateWrapper(T entity) {
        super.setEntity(entity);
        super.initNeed();
        this.sqlSet = new ArrayList<>();
    }

    public UpdateWrapper(Class<T> entityClass) {
        super.setEntityClass(entityClass);
        super.initNeed();
        this.sqlSet = new ArrayList<>();
    }

    private UpdateWrapper(T entity, List<String> sqlSet, AtomicInteger paramNameSeq, AtomicInteger tableSeq,
                          Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments, SharedString paramAlias,
                          SharedString lastSql, SharedString sqlComment, SharedString sqlFirst) {
        super.setEntity(entity);
        this.sqlSet = sqlSet;
        this.paramNameSeq = paramNameSeq;
        this.tableSeq = tableSeq;
        this.paramNameValuePairs = paramNameValuePairs;
        this.expression = mergeSegments;
        this.paramAlias = paramAlias;
        this.lastSql = lastSql;
        this.sqlComment = sqlComment;
        this.sqlFirst = sqlFirst;
    }

    @Override
    public String getSqlSet() {
        if (CollectionUtils.isEmpty(sqlSet)) {
            return SqlConstants.EMPTY;
        }
        return String.join(SqlConstants.COMMA, sqlSet);
    }

    @Override
    public UpdateWrapper<T> set(boolean condition, String column, Object val, String mapping) {
        return maybeDo(condition, () -> {
            String sql = formatParam(mapping, val);
            sqlSet.add(column + SqlConstants.EQUALS + sql);
        });
    }

    @Override
    public UpdateWrapper<T> setSql(boolean condition, String sql) {
        if (condition && StringUtils.isNotBlank(sql)) {
            sqlSet.add(sql);
        }
        return typedThis;
    }

    @Override
    protected String columnSqlInjectFilter(String column) {
        return StringUtils.sqlInjectionReplaceBlank(column);
    }

    @Override
    protected UpdateWrapper<T> instance() {
        return new UpdateWrapper<>(getEntity(), null, paramNameSeq, tableSeq, paramNameValuePairs, new MergeSegments(),
                paramAlias, SharedString.emptyString(), SharedString.emptyString(), SharedString.emptyString());
    }

    @Override
    public void clear() {
        super.clear();
        sqlSet.clear();
        preparedSql.clear();
    }

    /**
     * 获取预编译sql信息
     *
     * @return 预编译sql信息
     */
    @Override
    public PreparedSql getPreparedSql() {
        if (!preparedSql.isNotEmpty()) {
            preparedSql.setSrcUpdateStr(SqlConstants.UPDATE + StringPool.SPACE + TableInfoHelper.getTableName(getEntityClass()))
                    .setSrcSetStr(SqlConstants.SET + StringPool.SPACE + getSqlSet())
                    .setSrcWhereStr(getAfterWhereSqlSegment());
        }
        return preparedSql;
    }
}
