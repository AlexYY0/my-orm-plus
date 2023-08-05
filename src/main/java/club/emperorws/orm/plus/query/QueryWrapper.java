package club.emperorws.orm.plus.query;

import club.emperorws.orm.metadata.TableModelFieldInfo;
import club.emperorws.orm.plus.AbstractWrapper;
import club.emperorws.orm.plus.consts.StringPool;
import club.emperorws.orm.plus.dto.PreparedSql;
import club.emperorws.orm.plus.dto.SharedString;
import club.emperorws.orm.plus.segments.MergeSegments;
import club.emperorws.orm.plus.toolkit.ArrayUtils;
import club.emperorws.orm.plus.toolkit.StringUtils;
import club.emperorws.orm.plus.toolkit.TableInfoHelper;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static club.emperorws.orm.plus.consts.SqlConstants.SELECT;
import static club.emperorws.orm.plus.consts.StringPool.SPACE;

/**
 * Entity 对象select查询封装操作类
 *
 * @author 892638
 * @date 2022.09.18 03:09
 **/
public class QueryWrapper<T> extends AbstractWrapper<T, String, QueryWrapper<T>>
        implements Query<QueryWrapper<T>, T, String> {

    /**
     * 查询字段
     */
    private final SharedString sqlSelect = new SharedString();

    /**
     * 最终生成的SQL
     */
    private PreparedSql preparedSql = new PreparedSql();

    public QueryWrapper() {
        this((T) null);
    }

    public QueryWrapper(T entity) {
        super.setEntity(entity);
        super.initNeed();
    }

    public QueryWrapper(Class<T> entityClass) {
        super.setEntityClass(entityClass);
        super.initNeed();
    }

    public QueryWrapper(T entity, String... columns) {
        super.setEntity(entity);
        super.initNeed();
        this.select(columns);
    }

    /**
     * 非对外公开的构造方法,只用于生产嵌套 sql
     *
     * @param entityClass 本不应该需要的
     */
    private QueryWrapper(T entity, Class<T> entityClass, AtomicInteger paramNameSeq, AtomicInteger tableSeq,
                         Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments, SharedString paramAlias,
                         SharedString lastSql, SharedString sqlComment, SharedString sqlFirst) {
        super.setEntity(entity);
        super.setEntityClass(entityClass);
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
    public QueryWrapper<T> select(String... columns) {
        if (ArrayUtils.isNotEmpty(columns)) {
            this.sqlSelect.setStringValue(String.join(StringPool.COMMA, columns));
        }
        return typedThis;
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
    @Override
    public QueryWrapper<T> select(Class<T> entityClass, Predicate<TableModelFieldInfo> predicate) {
        super.setEntityClass(entityClass);
        this.sqlSelect.setStringValue(TableInfoHelper.chooseSelect(getEntityClass(), predicate));
        return typedThis;
    }

    @Override
    public String getSqlSelect() {
        if (StringUtils.isEmpty(sqlSelect.getStringValue())) {
            return StringPool.STAR;
        }
        return sqlSelect.getStringValue();
    }

    @Override
    protected String columnSqlInjectFilter(String column) {
        return StringUtils.sqlInjectionReplaceBlank(column);
    }

    /**
     * 用于生成嵌套 sql
     * <p>
     * 故 sqlSelect 不向下传递
     * </p>
     */
    @Override
    protected QueryWrapper<T> instance() {
        return new QueryWrapper<>(getEntity(), getEntityClass(), paramNameSeq, tableSeq, paramNameValuePairs, new MergeSegments(),
                paramAlias, SharedString.emptyString(), SharedString.emptyString(), SharedString.emptyString());
    }

    @Override
    public void clear() {
        super.clear();
        sqlSelect.toNull();
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
            preparedSql.setSrcSelectStr(SELECT + SPACE + getSqlSelect())
                    .setSrcFromStr(getSqlFrom())
                    .setSrcWhereStr(getAfterWhereSqlSegment());
        }
        return preparedSql;
    }
}
