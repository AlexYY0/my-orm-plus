package club.emperorws.orm.plus;

import club.emperorws.orm.plus.consts.SqlConstants;
import club.emperorws.orm.plus.consts.StringPool;
import club.emperorws.orm.plus.dto.SharedString;
import club.emperorws.orm.plus.enums.SqlJoin;
import club.emperorws.orm.plus.enums.SqlKeyword;
import club.emperorws.orm.plus.enums.SqlLike;
import club.emperorws.orm.plus.enums.WrapperKeyword;
import club.emperorws.orm.plus.interfaces.*;
import club.emperorws.orm.plus.segments.MergeSegments;
import club.emperorws.orm.plus.toolkit.*;
import club.emperorws.orm.starter.util.Assert;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.regex.Matcher;

import static java.util.stream.Collectors.joining;

/**
 * 顶层的SQL语句封装类
 *
 * @param <T>        需要封装查询的实体类
 * @param <R>        DB Table列的名称属性（一般为String）
 * @param <Children> 子查询封装类
 * @author 892638
 * @date 2022.09.16 18:10
 **/
public abstract class AbstractWrapper<T, R, Children extends AbstractWrapper<T, R, Children>> extends Wrapper<T>
        implements Compare<Children, R>, Nested<Children, Children>, Join<Children>, Func<Children, R>,
        From<Children, Children>, OnCompare<Children, R> {

    /**
     * 链式调用支持
     */
    protected final Children typedThis = (Children) this;

    /**
     * SQL语句?变量占位命名
     */
    protected AtomicInteger paramNameSeq;

    /**
     * sql联表查询时的table重命名
     */
    protected AtomicInteger tableSeq;

    /**
     * SQL语句?变量-值存储器
     */
    protected Map<String, Object> paramNameValuePairs;

    /**
     * SQL语句?变量前置别名
     */
    protected SharedString paramAlias;

    protected SharedString lastSql;

    protected SharedString sqlFirst;

    protected SharedString sqlComment;

    /**
     * 数据库映射实体类
     */
    private T entity;

    /**
     * 数据库映射实体类类型
     */
    private Class<T> entityClass;

    /**
     * SQL片段存储器
     */
    protected MergeSegments expression;

    @Override
    public T getEntity() {
        return entity;
    }

    public Children setEntity(T entity) {
        this.entity = entity;
        return typedThis;
    }

    public Class<T> getEntityClass() {
        if (Objects.isNull(entityClass) && Objects.nonNull(entity)) {
            entityClass = (Class<T>) entity.getClass();
        }
        return entityClass;
    }

    public Children setEntityClass(Class<T> entityClass) {
        if (Objects.nonNull(entityClass)) {
            this.entityClass = entityClass;
        }
        return typedThis;
    }

    @Override
    public <V> Children allEq(boolean condition, Map<R, V> params, boolean null2IsNull) {
        if (condition && CollectionUtils.isNotEmpty(params)) {
            params.forEach((k, v) -> {
                if (StringUtils.checkValNotNull(v)) {
                    eq(k, v);
                } else {
                    if (null2IsNull) {
                        isNull(k);
                    }
                }
            });
        }
        return typedThis;
    }

    @Override
    public <V> Children allEq(boolean condition, BiPredicate<R, V> filter, Map<R, V> params, boolean null2IsNull) {
        if (condition && CollectionUtils.isNotEmpty(params)) {
            params.forEach((k, v) -> {
                if (filter.test(k, v)) {
                    if (StringUtils.checkValNotNull(v)) {
                        eq(k, v);
                    } else {
                        if (null2IsNull) {
                            isNull(k);
                        }
                    }
                }
            });
        }
        return typedThis;
    }

    @Override
    public Children eq(boolean condition, R column, Object val) {
        return addCondition(condition, column, SqlKeyword.EQ, val);
    }

    @Override
    public Children ne(boolean condition, R column, Object val) {
        return addCondition(condition, column, SqlKeyword.NE, val);
    }

    @Override
    public Children gt(boolean condition, R column, Object val) {
        return addCondition(condition, column, SqlKeyword.GT, val);
    }

    @Override
    public Children ge(boolean condition, R column, Object val) {
        return addCondition(condition, column, SqlKeyword.GE, val);
    }

    @Override
    public Children lt(boolean condition, R column, Object val) {
        return addCondition(condition, column, SqlKeyword.LT, val);
    }

    @Override
    public Children le(boolean condition, R column, Object val) {
        return addCondition(condition, column, SqlKeyword.LE, val);
    }

    @Override
    public Children like(boolean condition, R column, Object val) {
        return likeValue(condition, SqlKeyword.LIKE, column, val, SqlLike.DEFAULT);
    }

    @Override
    public Children notLike(boolean condition, R column, Object val) {
        return likeValue(condition, SqlKeyword.NOT_LIKE, column, val, SqlLike.DEFAULT);
    }

    @Override
    public Children likeLeft(boolean condition, R column, Object val) {
        return likeValue(condition, SqlKeyword.LIKE, column, val, SqlLike.LEFT);
    }

    @Override
    public Children likeRight(boolean condition, R column, Object val) {
        return likeValue(condition, SqlKeyword.LIKE, column, val, SqlLike.RIGHT);
    }

    @Override
    public Children between(boolean condition, R column, Object val1, Object val2) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), SqlKeyword.BETWEEN,
                () -> formatParam(null, val1), SqlKeyword.AND, () -> formatParam(null, val2)));
    }

    @Override
    public Children notBetween(boolean condition, R column, Object val1, Object val2) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), SqlKeyword.NOT_BETWEEN,
                () -> formatParam(null, val1), SqlKeyword.AND, () -> formatParam(null, val2)));
    }

    @Override
    public Children and(boolean condition, Consumer<Children> consumer) {
        return and(condition).addNestedCondition(condition, consumer);
    }

    @Override
    public Children or(boolean condition, Consumer<Children> consumer) {
        return or(condition).addNestedCondition(condition, consumer);
    }

    @Override
    public Children nested(boolean condition, Consumer<Children> consumer) {
        return addNestedCondition(condition, consumer);
    }

    @Override
    public Children not(boolean condition, Consumer<Children> consumer) {
        return not(condition).addNestedCondition(condition, consumer);
    }

    @Override
    public Children or(boolean condition) {
        return maybeDo(condition, () -> appendSqlSegments(SqlKeyword.OR));
    }

    @Override
    public Children apply(boolean condition, String applySql, Object... values) {
        return maybeDo(condition, () -> appendSqlSegments(WrapperKeyword.APPLY,
                () -> formatSqlMaybeWithParam(applySql, null, values)));
    }

    @Override
    public Children last(boolean condition, String lastSql) {
        if (condition) {
            this.lastSql.setStringValue(StringPool.SPACE + lastSql);
        }
        return typedThis;
    }

    @Override
    public Children comment(boolean condition, String comment) {
        if (condition) {
            this.sqlComment.setStringValue(comment);
        }
        return typedThis;
    }

    @Override
    public Children first(boolean condition, String firstSql) {
        if (condition) {
            this.sqlFirst.setStringValue(firstSql);
        }
        return typedThis;
    }

    @Override
    public Children exists(boolean condition, String existsSql, Object... values) {
        return maybeDo(condition, () -> appendSqlSegments(SqlKeyword.EXISTS,
                () -> String.format("(%s)", formatSqlMaybeWithParam(existsSql, null, values))));
    }

    @Override
    public Children notExists(boolean condition, String existsSql, Object... values) {
        return not(condition).exists(condition, existsSql, values);
    }

    @Override
    public Children isNull(boolean condition, R column) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), SqlKeyword.IS_NULL));
    }

    @Override
    public Children isNotNull(boolean condition, R column) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), SqlKeyword.IS_NOT_NULL));
    }

    @Override
    public Children in(boolean condition, R column, Collection<?> coll) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), SqlKeyword.IN, inExpression(coll)));
    }

    @Override
    public Children in(boolean condition, R column, Object... values) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), SqlKeyword.IN, inExpression(values)));
    }

    @Override
    public Children notIn(boolean condition, R column, Collection<?> coll) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), SqlKeyword.NOT_IN, inExpression(coll)));
    }

    @Override
    public Children notIn(boolean condition, R column, Object... values) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), SqlKeyword.NOT_IN, inExpression(values)));
    }

    @Override
    public Children inSql(boolean condition, R column, String inValue) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), SqlKeyword.IN,
                () -> String.format("(%s)", inValue)));
    }

    @Override
    public Children gtSql(boolean condition, R column, String inValue) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), SqlKeyword.GT,
                () -> String.format("(%s)", inValue)));
    }

    @Override
    public Children geSql(boolean condition, R column, String inValue) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), SqlKeyword.GE,
                () -> String.format("(%s)", inValue)));
    }

    @Override
    public Children ltSql(boolean condition, R column, String inValue) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), SqlKeyword.LT,
                () -> String.format("(%s)", inValue)));
    }

    @Override
    public Children leSql(boolean condition, R column, String inValue) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), SqlKeyword.LE,
                () -> String.format("(%s)", inValue)));
    }

    @Override
    public Children notInSql(boolean condition, R column, String inValue) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), SqlKeyword.NOT_IN,
                () -> String.format("(%s)", inValue)));
    }

    @Override
    public Children groupBy(boolean condition, R column, R... columns) {
        return maybeDo(condition, () -> {
            String one = columnToString(column);
            if (ArrayUtils.isNotEmpty(columns)) {
                one += (StringPool.COMMA + columnsToString(columns));
            }
            final String finalOne = one;
            appendSqlSegments(SqlKeyword.GROUP_BY, () -> finalOne);
        });
    }

    @Override
    @SafeVarargs
    public final Children orderBy(boolean condition, boolean isAsc, R column, R... columns) {
        return maybeDo(condition, () -> {
            final SqlKeyword mode = isAsc ? SqlKeyword.ASC : SqlKeyword.DESC;
            appendSqlSegments(SqlKeyword.ORDER_BY, columnToSqlSegment(columnSqlInjectFilter(column)), mode);
            if (ArrayUtils.isNotEmpty(columns)) {
                Arrays.stream(columns).forEach(c -> appendSqlSegments(SqlKeyword.ORDER_BY,
                        columnToSqlSegment(columnSqlInjectFilter(c)), mode));
            }
        });
    }

    @Override
    public Children groupBy(boolean condition, R column) {
        return maybeDo(condition, () -> appendSqlSegments(SqlKeyword.GROUP_BY, () -> columnToString(column)));
    }

    @Override
    public Children groupBy(boolean condition, List<R> columns) {
        return maybeDo(condition, () -> appendSqlSegments(SqlKeyword.GROUP_BY, () -> columnsToString(columns)));
    }

    @Override
    public Children orderBy(boolean condition, boolean isAsc, R column) {
        return maybeDo(condition, () -> appendSqlSegments(SqlKeyword.ORDER_BY, columnToSqlSegment(columnSqlInjectFilter(column)),
                isAsc ? SqlKeyword.ASC : SqlKeyword.DESC));
    }

    @Override
    public Children orderBy(boolean condition, boolean isAsc, List<R> columns) {
        return maybeDo(condition, () -> columns.forEach(c -> appendSqlSegments(SqlKeyword.ORDER_BY,
                columnToSqlSegment(columnSqlInjectFilter(c)), isAsc ? SqlKeyword.ASC : SqlKeyword.DESC)));
    }

    @Override
    public Children having(boolean condition, String sqlHaving, Object... params) {
        return maybeDo(condition, () -> appendSqlSegments(SqlKeyword.HAVING,
                () -> formatSqlMaybeWithParam(sqlHaving, null, params)));
    }

    @Override
    public Children func(boolean condition, Consumer<Children> consumer) {
        return maybeDo(condition, () -> consumer.accept(typedThis));
    }

    @Override
    public Children joinEq(boolean condition, R column1, R column2) {
        return addOriginCondition(condition, column1, SqlKeyword.EQ, column2);
    }

    /**
     * from 嵌套子查询
     * <p>适用于单表，SqlJoin.LEFT_JOIN是标志位，无实际意义</p>
     * <p>注意：只允许select查询使用</p>
     *
     * @param condition 满足条件才执行：一般为true
     * @param consumer  子查询
     * @return this
     */
    @Override
    public Children from(boolean condition, Consumer<Children> consumer) {
        if (condition) {
            //生成from sql子查询
            final Children instance = instance();
            consumer.accept(instance);
            appendSqlSegments(SqlJoin.FROM, () -> StringPool.LEFT_BRACKET, () -> instance.getPreparedSql().getSrcSqlStr(), () -> StringPool.RIGHT_BRACKET, () -> SqlConstants.TABLE_ALIAS + tableSeq.incrementAndGet());
        }
        return typedThis;
    }

    @Override
    public <S> Children join(boolean condition, SqlJoin sqlJoin, Class<S> joinEntity, Consumer<Children> consumer) {
        if (condition) {
            //初始化拼接第一个table
            if (CollectionUtils.isEmpty(expression.getFrom())) {
                appendSqlSegments(sqlJoin, () -> TableInfoHelper.getTableName(getEntityClass()), () -> SqlConstants.TABLE_ALIAS + tableSeq.incrementAndGet());
            }
            //生成from sql并拼接ON查询条件
            final Children instance = instance();
            consumer.accept(instance);
            appendSqlSegments(sqlJoin, () -> TableInfoHelper.getTableName(joinEntity), () -> SqlConstants.TABLE_ALIAS + tableSeq.incrementAndGet(), SqlKeyword.ON, instance);
        }
        return typedThis;
    }

    @Override
    public Children join(boolean condition, SqlJoin sqlJoin, Consumer<Children> nestedQueryConsumer, Consumer<Children> onConditionConsumer) {
        if (condition) {
            //初始化拼接第一个table
            if (CollectionUtils.isEmpty(expression.getFrom())) {
                appendSqlSegments(sqlJoin, () -> TableInfoHelper.getTableName(getEntityClass()), () -> SqlConstants.TABLE_ALIAS + tableSeq.incrementAndGet());
            }
            //from嵌套子查询拼接
            final Children nestedQueryInstance = instance();
            nestedQueryConsumer.accept(nestedQueryInstance);
            //生成from sql并拼接ON查询条件
            final Children onConditionInstance = instance();
            onConditionConsumer.accept(onConditionInstance);
            appendSqlSegments(sqlJoin, () -> StringPool.LEFT_BRACKET, () -> nestedQueryInstance.getPreparedSql().getSrcSqlStr(), () -> StringPool.RIGHT_BRACKET, () -> SqlConstants.TABLE_ALIAS + tableSeq.incrementAndGet(), SqlKeyword.ON, onConditionInstance);
        }
        return typedThis;
    }

    @Override
    public Children join(boolean condition, SqlJoin sqlJoin, String joinStr) {
        if (condition) {
            //初始化拼接第一个table
            if (CollectionUtils.isEmpty(expression.getFrom())) {
                appendSqlSegments(sqlJoin, () -> TableInfoHelper.getTableName(getEntityClass()), () -> SqlConstants.TABLE_ALIAS + tableSeq.incrementAndGet());
            }
            //生成from sql并拼接ON查询条件
            appendSqlSegments(sqlJoin, () -> joinStr);
        }
        return typedThis;
    }

    /**
     * 内部自用
     * <p>NOT 关键词</p>
     */
    protected Children not(boolean condition) {
        return maybeDo(condition, () -> appendSqlSegments(SqlKeyword.NOT));
    }

    /**
     * 内部自用
     * <p>拼接 AND</p>
     */
    protected Children and(boolean condition) {
        return maybeDo(condition, () -> appendSqlSegments(SqlKeyword.AND));
    }

    /**
     * 内部自用
     * <p>拼接 LIKE 以及 值</p>
     */
    protected Children likeValue(boolean condition, SqlKeyword keyword, R column, Object val, SqlLike sqlLike) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), keyword,
                () -> formatParam(null, SqlUtils.concatLike(val, sqlLike))));
    }

    /**
     * 原汁原味查询条件拼接
     *
     * @param condition  是否执行
     * @param column     属性
     * @param sqlKeyword SQL 关键词
     * @param val        条件值
     */
    protected Children addOriginCondition(boolean condition, R column, SqlKeyword sqlKeyword, Object val) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), sqlKeyword, val::toString));
    }

    /**
     * 普通查询条件拼接
     *
     * @param condition  是否执行
     * @param column     属性
     * @param sqlKeyword SQL 关键词
     * @param val        条件值
     */
    protected Children addCondition(boolean condition, R column, SqlKeyword sqlKeyword, Object val) {
        return maybeDo(condition, () -> appendSqlSegments(columnToSqlSegment(column), sqlKeyword,
                () -> formatParam(null, val)));
    }

    /**
     * 多重嵌套查询条件
     *
     * @param condition 查询条件值
     */
    protected Children addNestedCondition(boolean condition, Consumer<Children> consumer) {
        return maybeDo(condition, () -> {
            final Children instance = instance();
            consumer.accept(instance);
            appendSqlSegments(WrapperKeyword.APPLY, instance);
        });
    }

    /**
     * 函数化的做事
     *
     * @param condition 做不做
     * @param something 做什么
     * @return Children
     */
    protected final Children maybeDo(boolean condition, DoSomething something) {
        if (condition) {
            something.doIt();
        }
        return typedThis;
    }


    /**
     * 获取in表达式 包含括号
     *
     * @param value 集合
     */
    protected ISqlSegment inExpression(Collection<?> value) {
        if (CollectionUtils.isEmpty(value)) {
            return () -> "()";
        }
        return () -> value.stream().map(i -> formatParam(null, i))
                .collect(joining(StringPool.COMMA, StringPool.LEFT_BRACKET, StringPool.RIGHT_BRACKET));
    }

    /**
     * 获取in表达式 包含括号
     *
     * @param values 数组
     */
    protected ISqlSegment inExpression(Object[] values) {
        if (ArrayUtils.isEmpty(values)) {
            return () -> "()";
        }
        return () -> Arrays.stream(values).map(i -> formatParam(null, i))
                .collect(joining(StringPool.COMMA, StringPool.LEFT_BRACKET, StringPool.RIGHT_BRACKET));
    }

    /**
     * 添加 where 片段
     *
     * @param sqlSegments ISqlSegment 数组
     */
    protected void appendSqlSegments(ISqlSegment... sqlSegments) {
        expression.add(sqlSegments);
    }


    /**
     * 格式化 sql
     * <p>
     * 支持 "{0}" 这种,或者 "sql {0} sql" 这种
     *
     * @param sqlStr  可能是sql片段
     * @param mapping 例如: "javaType=int,jdbcType=NUMERIC" 这种
     * @param params  参数
     * @return sql片段
     */
    protected final String formatSqlMaybeWithParam(String sqlStr, String mapping, Object... params) {
        if (StringUtils.isBlank(sqlStr)) {
            return null;
        }
        if (ArrayUtils.isNotEmpty(params)) {
            for (int i = 0; i < params.length; ++i) {
                final String target = SqlConstants.LEFT_BRACE + i + SqlConstants.RIGHT_BRACE;
                sqlStr = sqlStr.replace(target, formatParam(mapping, params[i]));
            }
        }
        return sqlStr;
    }

    /**
     * 处理入参
     *
     * @param mapping 例如: "javaType=int,jdbcType=NUMERIC" 这种
     * @param param   参数
     * @return value
     */
    protected final String formatParam(String mapping, Object param) {
        final String genParamName = SqlConstants.WRAPPER_PARAM + paramNameSeq.incrementAndGet();
        final String paramStr = getParamAlias() + SqlConstants.WRAPPER_PARAM_MIDDLE + genParamName;
        paramNameValuePairs.put(genParamName, param);
        return SqlScriptUtils.safeParam(paramStr, mapping);
    }

    /**
     * 获取 columnName
     */
    protected final ISqlSegment columnToSqlSegment(R column) {
        return () -> columnToString(column);
    }

    /**
     * 获取 columnName
     */
    protected String columnToString(R column) {
        return (String) column;
    }

    /**
     * 获取 columnNames
     */
    protected String columnsToString(R... columns) {
        return Arrays.stream(columns).map(this::columnToString).collect(joining(StringPool.COMMA));
    }

    /**
     * 多字段转换为逗号 "," 分割字符串
     *
     * @param columns 多字段
     */
    protected String columnsToString(List<R> columns) {
        return columns.stream().map(this::columnToString).collect(joining(StringPool.COMMA));
    }

    /**
     * 必要的初始化
     */
    protected void initNeed() {
        paramNameSeq = new AtomicInteger(0);
        tableSeq = new AtomicInteger(0);
        paramNameValuePairs = new HashMap<>(16);
        expression = new MergeSegments();
        lastSql = SharedString.emptyString();
        sqlComment = SharedString.emptyString();
        sqlFirst = SharedString.emptyString();
    }

    @Override
    public void clear() {
        entity = null;
        paramNameSeq.set(0);
        tableSeq.set(0);
        paramNameValuePairs.clear();
        expression.clear();
        lastSql.toEmpty();
        sqlComment.toEmpty();
        sqlFirst.toEmpty();
    }

    @Override
    public String getSqlSegment() {
        return expression.getSqlSegment() + lastSql.getStringValue();
    }

    @Override
    public String getSqlComment() {
        if (StringUtils.isNotBlank(sqlComment.getStringValue())) {
            return "/*" + StringEscape.escapeRawString(sqlComment.getStringValue()) + "*/";
        }
        return null;
    }

    @Override
    public String getSqlFirst() {
        if (StringUtils.isNotBlank(sqlFirst.getStringValue())) {
            return StringEscape.escapeRawString(sqlFirst.getStringValue());
        }
        return null;
    }

    /**
     * 获取frmo sql语句
     *
     * @return from sql语句
     */
    @Override
    public String getSqlFrom() {
        if (CollectionUtils.isEmpty(expression.getFrom())) {
            return SqlConstants.FROM + StringPool.SPACE + TableInfoHelper.getTableName(getEntityClass());
        }
        return expression.getFrom().getSqlSegment();
    }

    @Override
    public MergeSegments getExpression() {
        return expression;
    }

    public Map<String, Object> getParamNameValuePairs() {
        return paramNameValuePairs;
    }

    public String getParamAlias() {
        return paramAlias == null ? SqlConstants.WRAPPER : paramAlias.getStringValue();
    }

    /**
     * 参数别名设置，初始化时优先设置该值、重复设置异常
     *
     * @param paramAlias 参数别名
     * @return Children
     */
    @SuppressWarnings("unused")
    public Children setParamAlias(String paramAlias) {
        Assert.notEmpty(paramAlias, "paramAlias can not be empty!");
        Assert.isEmpty(paramNameValuePairs, "Please call this method before working!");
        Assert.isNull(this.paramAlias, "Please do not call the method repeatedly!");
        this.paramAlias = new SharedString(paramAlias);
        return typedThis;
    }

    /**
     * 解析、获取预编译SQL语句的条件（默认没有mapping，尚未实现该功能   例: javaType=int,jdbcType=NUMERIC这种）
     * 由于冯导写的ORM conditionList必须是String，所以只能强转
     *
     * @return 预编译SQL语句的条件e'q
     */
    @Override
    public List<String> processAndGetCondition() {
        List<String> conditionList = new ArrayList<>();
        String sql = getPreparedSql().getSrcSqlStr();
        Matcher matcher = SqlConstants.SQL_REPLACE_PATTERN.matcher(sql);
        while (matcher.find()) {
            String oneParamName = matcher.group()
                    .replace(StringPool.HASH_LEFT_BRACE, StringPool.EMPTY)
                    .replace(StringPool.RIGHT_BRACE, StringPool.EMPTY)
                    .replace(getParamAlias() + SqlConstants.WRAPPER_PARAM_MIDDLE, StringPool.EMPTY);
            conditionList.add(paramNameValuePairs.get(oneParamName).toString());
        }
        return conditionList;
    }

    /**
     * 子类返回一个自己的新对象
     */
    protected abstract Children instance();


    /**
     * 字段 SQL 注入过滤处理，子类重写实现过滤逻辑
     *
     * @param column 字段内容
     * @return
     */
    protected R columnSqlInjectFilter(R column) {
        return column;
    }

    /**
     * 做事函数
     */
    @FunctionalInterface
    public interface DoSomething {
        void doIt();
    }
}
