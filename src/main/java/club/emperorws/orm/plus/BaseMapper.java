package club.emperorws.orm.plus;

import club.emperorws.orm.annotations.*;
import club.emperorws.orm.mapping.SqlSource;
import club.emperorws.orm.metadata.TableModelFieldInfo;
import club.emperorws.orm.metadata.TableModelInfo;
import club.emperorws.orm.metadata.TableModelInfoHelper;
import club.emperorws.orm.plus.consts.SqlConstants;
import club.emperorws.orm.plus.consts.StringPool;
import club.emperorws.orm.plus.dialect.DbType;
import club.emperorws.orm.plus.dialect.DialectFactory;
import club.emperorws.orm.plus.dialect.dialects.IDialect;
import club.emperorws.orm.plus.dto.PageInfo;
import club.emperorws.orm.plus.dto.PreparedSql;
import club.emperorws.orm.plus.enums.SqlMethod;
import club.emperorws.orm.plus.query.QueryWrapper;
import club.emperorws.orm.plus.toolkit.*;
import club.emperorws.orm.plus.update.UpdateWrapper;
import club.emperorws.orm.reflection.MetaObject;
import club.emperorws.orm.result.BatchResult;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * 基础DAO层
 *
 * @author 892638
 * @date 2023.05.18 18:03
 **/
public interface BaseMapper<T> {

    /**
     * 新增插入
     *
     * @param entity 实体
     * @return 新增影响的行数
     */
    default Integer insert(T entity) {
        // 1. 获取实体信息
        TableModelInfo tableInfo = TableModelInfoHelper.getTableInfo(entity.getClass());
        // 2. 生成insert sql
        String dynamicSql = String.format(SqlMethod.INSERT_ONE.getSql(), tableInfo.getTableName(),
                StringPool.LEFT_BRACKET + String.join(",", tableInfo.getColumnList()) + StringPool.RIGHT_BRACKET,
                StringPool.LEFT_BRACKET + tableInfo.getPropertyList().stream().map(property -> StringPool.HASH_LEFT_BRACE + SqlConstants.ENTITY + StringPool.DOT + property + StringPool.RIGHT_BRACE).collect(Collectors.joining(",")) + StringPool.RIGHT_BRACKET);
        // 3. 主键自动生成
        DbUtils.createEntityId(tableInfo, entity);
        // 4. 执行Mapper方法
        return insert(new SqlSource.Builder(dynamicSql).build(), entity);
    }

    /**
     * 新增插入（null和空字符串不会插入）
     *
     * @param entity 实体
     * @return 新增影响的行数
     */
    default Integer insertSelective(T entity) {
        // 1. 获取实体信息
        TableModelInfo tableInfo = TableModelInfoHelper.getTableInfo(entity.getClass());
        // 2. 获取对象的元对象（方便取值）
        MetaObject metaObject = tableInfo.getConfiguration().newMetaObject(entity);
        // 3. 封装insert语句
        StringJoiner columSql = new StringJoiner(",");
        StringJoiner valueSql = new StringJoiner(",");
        for (TableModelFieldInfo tableModelFieldInfo : tableInfo.getFieldInfoList()) {
            Object fieldValue = metaObject.getValue(tableModelFieldInfo.getProperty());
            if (fieldValue == null || StringUtils.isBlank(String.valueOf(fieldValue))) {
                continue;
            }
            columSql.add(tableModelFieldInfo.getColumn());
            valueSql.add(StringPool.HASH_LEFT_BRACE + SqlConstants.ENTITY + StringPool.DOT + tableModelFieldInfo.getProperty() + StringPool.RIGHT_BRACE);
        }
        // 4. 生成insert sql
        String dynamicSql = String.format(SqlMethod.INSERT_ONE.getSql(), tableInfo.getTableName(),
                StringPool.LEFT_BRACKET + columSql + StringPool.RIGHT_BRACKET,
                StringPool.LEFT_BRACKET + valueSql + StringPool.RIGHT_BRACKET);
        // 5. 主键自动生成
        DbUtils.createEntityId(tableInfo, entity);
        // 6. 执行Mapper方法
        return insert(new SqlSource.Builder(dynamicSql).build(), entity);
    }

    /**
     * 新增
     *
     * @param entity 实体
     * @return 新增影响的行数
     */
    @Insert(resultType = "")
    Integer insert(SqlSource sqlSource, @Param(SqlConstants.ENTITY) T entity);

    /**
     * 通过主键相等来更新数据
     *
     * @param entity 实体
     * @return 更新影响的行数
     */
    default Integer updateByPrimaryKey(T entity) {
        // 1. 获取实体信息
        TableModelInfo tableInfo = TableModelInfoHelper.getTableInfo(entity.getClass());
        // 2. 生成update sql
        String dynamicSql = String.format(SqlMethod.UPDATE_BY_PRIMARY_KEY.getSql(), tableInfo.getTableName(),
                tableInfo.getFieldInfoList().stream()
                        .filter(fieldInfo -> !fieldInfo.getColumn().equals(tableInfo.getPkName()))
                        .map(fieldInfo -> fieldInfo.getColumn() + StringPool.EQUALS + StringPool.HASH_LEFT_BRACE + SqlConstants.ENTITY + StringPool.DOT + fieldInfo.getProperty() + StringPool.RIGHT_BRACE)
                        .collect(Collectors.joining(",")),
                tableInfo.getPkName(),
                SqlConstants.ENTITY + StringPool.DOT + tableInfo.getTableModelFieldInfo(tableInfo.getPkName()).getProperty());
        // 3. 执行Mapper方法
        return updateByPrimaryKey(new SqlSource.Builder(dynamicSql).build(), entity);
    }

    /**
     * 通过主键相等来更新数据（null和空字符串不会更新）
     *
     * @param entity 实体
     * @return 更新影响的行数
     */
    default Integer updateByPrimaryKeySelective(T entity) {
        // 1. 获取实体信息
        TableModelInfo tableInfo = TableModelInfoHelper.getTableInfo(entity.getClass());
        // 2. 获取对象的元对象（方便取值）
        MetaObject metaObject = tableInfo.getConfiguration().newMetaObject(entity);
        // 3. 封装update语句
        UpdateWrapper<T> updateWrapper = WrapperHelper.update(entity);
        for (TableModelFieldInfo tableModelFieldInfo : tableInfo.getFieldInfoList()) {
            Object fieldValue = metaObject.getValue(tableModelFieldInfo.getProperty());
            if (fieldValue == null || StringUtils.isBlank(String.valueOf(fieldValue))) {
                continue;
            }
            updateWrapper.set(tableModelFieldInfo.getColumn(), fieldValue);
        }
        updateWrapper.eq(tableInfo.getPkName(), metaObject.getValue(tableInfo.getPkName()));
        // 4. 执行Mapper方法
        return update(updateWrapper);
    }

    /**
     * 根据主键修改
     *
     * @param entity 实体
     * @return 修改影响的行数
     */
    @Update(resultType = "")
    Integer updateByPrimaryKey(SqlSource sqlSource, @Param(SqlConstants.ENTITY) T entity);

    /**
     * 通用更新方法
     *
     * @param wrapper 存储着sql中的条件语句
     * @return 更新成功的个数
     */
    default Integer update(Wrapper<T> wrapper) {
        PreparedSql preparedSql = wrapper.getPreparedSql();
        String sql = preparedSql.getSrcSqlStr();
        // 执行Mapper方法
        return update(new SqlSource.Builder(sql).build(), wrapper);
    }

    @Update(resultType = "")
    Integer update(SqlSource sqlSource, @Param(SqlConstants.WRAPPER) Wrapper<T> wrapper);

    /**
     * 获取数据存在的个数
     * <p>支持join联表查询</p>
     *
     * @param wrapper 存储着sql中的条件语句
     * @return 统计个数
     */
    default Long selectCount(Wrapper<T> wrapper) {
        PreparedSql preparedSql = wrapper.getPreparedSql();
        String dynamicSql = String.format(SqlMethod.SELECT_COUNT.getSql(), wrapper.getSqlSelect(), preparedSql.getSrcFromStr(), preparedSql.getSrcWhereStr());
        // 执行Mapper方法
        return selectCount(new SqlSource.Builder(dynamicSql).build(), wrapper);
    }

    @Select(resultType = "")
    Long selectCount(SqlSource sqlSource, @Param(SqlConstants.WRAPPER) Wrapper<T> wrapper);

    /**
     * 通过主键相等来查询数据
     *
     * @param entity 实体
     * @return 查询结果
     */
    default T selectByPrimaryKey(T entity) {
        // 1. 获取实体信息
        TableModelInfo tableInfo = TableModelInfoHelper.getTableInfo(entity.getClass());
        // 2. 获取对象的元对象（方便取值）
        MetaObject metaObject = tableInfo.getConfiguration().newMetaObject(entity);
        // 3. 组装查询信息
        QueryWrapper<T> queryWrapper = WrapperHelper.query(entity)
                .select(i -> true)
                .eq(tableInfo.getPkName(), metaObject.getValue(tableInfo.getPkName()));
        // 3. 执行Mapper方法
        return selectOne(queryWrapper);
    }

    /**
     * 通过主键相等来查询数据
     *
     * @param id 主键
     * @return 查询结果
     */
    default T selectByPrimaryKey(Serializable id) {
        // 1. 获取泛型实体class类型（确切的、已知的公式）
        Type mapperActualTypeArgument = ((ParameterizedType) ((Class<?>) getClass().getGenericInterfaces()[0]).getGenericInterfaces()[0]).getActualTypeArguments()[0];
        @SuppressWarnings("unchecked")
        Class<T> entityClass = (Class<T>) mapperActualTypeArgument;
        // 2. 获取实体信息
        TableModelInfo tableInfo = TableModelInfoHelper.getTableInfo(entityClass);
        // 3. 组装查询信息
        QueryWrapper<T> queryWrapper = WrapperHelper.query(entityClass)
                .select(i -> true)
                .eq(tableInfo.getPkName(), id);
        // 3. 执行Mapper方法
        return selectOne(queryWrapper);
    }

    /**
     * 通用查询方法（结果为一个）
     *
     * @param wrapper 存储着sql中的条件语句
     * @return 查询成功的结果
     */
    default T selectOne(Wrapper<T> wrapper) {
        PreparedSql preparedSql = wrapper.getPreparedSql();
        String sql = preparedSql.getSrcSelectStr() + StringPool.SPACE + preparedSql.getSrcFromStr() + StringPool.SPACE + preparedSql.getSrcWhereStr();
        // 执行Mapper方法
        return selectOne(new SqlSource.Builder(sql).build(), wrapper);
    }

    /**
     * 根据 entity 条件，查询一条记录
     * <p>查询一条记录，例如 qw.last("limit 1") 限制取一条记录, 注意：多条数据会报异常（ORM抛出的异常）</p>
     */
    @Select(resultType = "")
    T selectOne(SqlSource sqlSource, @Param(SqlConstants.WRAPPER) Wrapper<T> wrapper);

    /**
     * 通用查询方法（结果为集合）
     * String类型为 colum like '%value%'
     * Date类型为 colum >= 'value'（待定，可讨论）
     * 其他类型为 colum = 'value'
     *
     * @param entity 对象中的条件语句
     * @return 查询成功的数据集合
     */
    default List<T> select(T entity) {
        // 1. 获取实体信息
        TableModelInfo tableInfo = TableModelInfoHelper.getTableInfo(entity.getClass());
        // 2. 获取对象的元对象（方便取值）
        MetaObject metaObject = tableInfo.getConfiguration().newMetaObject(entity);
        // 3. 封装查询条件
        QueryWrapper<T> queryWrapper = WrapperHelper.query(entity);
        for (TableModelFieldInfo tableModelFieldInfo : tableInfo.getFieldInfoList()) {
            Object fieldValue = metaObject.getValue(tableModelFieldInfo.getProperty());
            if (fieldValue == null) {
                continue;
            }
            String typeName = tableModelFieldInfo.getField().getType().getName();
            switch (typeName) {
                case "java.lang.String":
                    queryWrapper.like(tableModelFieldInfo.getColumn(), fieldValue);
                    break;
                default:
                    queryWrapper.eq(tableModelFieldInfo.getColumn(), fieldValue);
            }
        }
        return select(queryWrapper);
    }

    /**
     * 通用查询方法（结果为集合）
     * <p>支持join联表查询</p>
     *
     * @param wrapper 存储着sql中的条件语句
     * @return 查询成功的数据集合
     */
    default List<T> select(Wrapper<T> wrapper) {
        PreparedSql preparedSql = wrapper.getPreparedSql();
        String sql = preparedSql.getSrcSelectStr() + StringPool.SPACE + preparedSql.getSrcFromStr() + StringPool.SPACE + preparedSql.getSrcWhereStr();
        // 执行Mapper方法
        return select(new SqlSource.Builder(sql).build(), wrapper);
    }

    @Select(resultType = "")
    List<T> select(SqlSource sqlSource, @Param(SqlConstants.WRAPPER) Wrapper<T> wrapper);

    /**
     * 限制数量的查询
     *
     * @param wrapper 存储着sql中的条件语句
     * @param limit   限制查询的数量
     * @return 询成功的数据集合
     */
    default List<T> selectLimit(Wrapper<T> wrapper, int limit) {
        PageInfo<T> pageData = selectPage(wrapper, PageHelper.<T>startPage(1, limit));
        return pageData.getList();
    }

    /**
     * 通用查询方法（结果为集合，且支持分页）
     * <p>支持join联表查询</p>
     *
     * @param wrapper  存储着sql中的条件语句
     * @param pageInfo 分页信息
     * @return 查询成功的数据集合
     */
    default PageInfo<T> selectPage(Wrapper<T> wrapper, PageInfo<T> pageInfo) {
        // 1. 获取原SQL
        PreparedSql preparedSql = wrapper.getPreparedSql();
        String sql = preparedSql.getSrcSelectStr() + StringPool.SPACE + preparedSql.getSrcFromStr() + StringPool.SPACE + preparedSql.getSrcWhereStr();
        // 2. 获取总数信息
        String countSql = String.format("with subTable as (%s) select count(*) from subTable", sql);
        Long total = selectCount(new SqlSource.Builder(countSql).build(), wrapper);
        // 3. 获取数据库方言版分页SQL
        DbType dbType = JdbcUtils.getDbType();
        IDialect dialect = DialectFactory.getDialect(dbType);
        String dialectSql = dialect.buildPaginationSql(sql, pageInfo.getStartNum(), pageInfo.getEndNum());
        // 3. 执行Mapper方法
        List<T> pageList = selectPage(new SqlSource.Builder(dialectSql).build(), wrapper, pageInfo);
        // 4. 组装Page数据
        pageInfo.setList(pageList);
        pageInfo.setRows(pageList.size());
        pageInfo.setTotalRow(total);
        pageInfo.setTotalPage((long) Math.ceil(((double) total) / ((double) pageInfo.getPageSize())));
        return pageInfo;
    }

    @Select(resultType = "")
    List<T> selectPage(SqlSource sqlSource, @Param(SqlConstants.WRAPPER) Wrapper<T> wrapper, @Param(SqlConstants.PAGE) PageInfo<T> pageInfo);

    /**
     * 通过主键相等来删除数据
     *
     * @param entity 实体
     * @return 删除影响的行数
     */
    default Integer deleteByPrimaryKey(T entity) {
        // 1. 获取实体信息
        TableModelInfo tableInfo = TableModelInfoHelper.getTableInfo(entity.getClass());
        // 2. 获取对象的元对象（方便取值）
        MetaObject metaObject = tableInfo.getConfiguration().newMetaObject(entity);
        // 3. 封装查询条件
        QueryWrapper<T> queryWrapper = WrapperHelper.query(entity)
                .eq(tableInfo.getPkName(), metaObject.getValue(tableInfo.getPkName()));
        // 4. 执行Mapper方法
        return delete(queryWrapper);
    }

    /**
     * 通过主键相等来删除数据
     *
     * @param id 主键
     * @return 删除影响的行数
     */
    default Integer deleteByPrimaryKey(Serializable id) {
        // 1. 获取泛型实体class类型（确切的、已知的公式）
        Type mapperActualTypeArgument = ((ParameterizedType) ((Class<?>) getClass().getGenericInterfaces()[0]).getGenericInterfaces()[0]).getActualTypeArguments()[0];
        @SuppressWarnings("unchecked")
        Class<T> entityClass = (Class<T>) mapperActualTypeArgument;
        // 2. 获取实体信息
        TableModelInfo tableInfo = TableModelInfoHelper.getTableInfo(entityClass);
        // 3. 组装查询信息
        QueryWrapper<T> queryWrapper = WrapperHelper.query(entityClass)
                .eq(tableInfo.getPkName(), id);
        // 3. 执行Mapper方法
        return delete(queryWrapper);
    }

    /**
     * 通用删除方法，对象里的所有非空属性与数据库的对应属性做equals判断
     * ps：如果是null对象，sql即为delete from table，删除所有数据，请谨慎使用
     *
     * @param entity 实体
     * @return 删除成功的数据量
     */
    default Integer delete(T entity) {
        // 1. 获取实体信息
        TableModelInfo tableInfo = TableModelInfoHelper.getTableInfo(entity.getClass());
        // 2. 获取对象的元对象（方便取值）
        MetaObject metaObject = tableInfo.getConfiguration().newMetaObject(entity);
        // 3. 封装查询条件
        QueryWrapper<T> queryWrapper = WrapperHelper.query(entity);
        for (TableModelFieldInfo tableModelFieldInfo : tableInfo.getFieldInfoList()) {
            Object fieldValue = metaObject.getValue(tableModelFieldInfo.getProperty());
            if (fieldValue == null) {
                continue;
            }
            String typeName = tableModelFieldInfo.getField().getType().getName();
            switch (typeName) {
                case "java.lang.String":
                    queryWrapper.like(tableModelFieldInfo.getColumn(), fieldValue);
                    break;
                default:
                    queryWrapper.eq(tableModelFieldInfo.getColumn(), fieldValue);
            }
        }
        return delete(queryWrapper);
    }

    /**
     * 通用删除方法
     *
     * @param wrapper 存储着sql中的条件语句
     * @return 删除成功的个数
     */
    default Integer delete(Wrapper<T> wrapper) {
        PreparedSql preparedSql = wrapper.getPreparedSql();
        String sql = SqlConstants.DELETE + StringPool.SPACE + preparedSql.getSrcFromStr() + StringPool.SPACE + preparedSql.getSrcWhereStr();
        // 执行Mapper方法
        return delete(new SqlSource.Builder(sql).build(), wrapper);
    }

    @Delete(resultType = "")
    Integer delete(SqlSource sqlSource, @Param(SqlConstants.WRAPPER) Wrapper<T> wrapper);

    /**
     * 新增或修改（最好是主键或唯一键）【todo 如果有可能，建议变更逻辑：先select，再update】
     * <p>
     * 使用了注解AnnFileModel.AnnField里的isInsertOrUpdateCondition属性
     * </p>
     * <p>目前只支持Oracle和MySQL：Oracle的实现是：merge into；MySQL的实现是： ON DUPLICATE KEY UPDATE</p>
     *
     * @param entity 实体数据
     * @return 新增或修改受影响的行数
     */
    default Integer insertOrUpdate(T entity) {
        // 1. 获取数据库方言
        DbType dbType = JdbcUtils.getDbType();
        IDialect dialect = DialectFactory.getDialect(dbType);
        // 2. 生成SQL语句
        String sql = dialect.buildInsertOrUpdateSql(entity.getClass());
        return insertOrUpdate(SqlSourceHelper.createSqlSource(sql), entity);
    }

    @Insert(resultType = "")
    Integer insertOrUpdate(SqlSource sqlSource, @Param(SqlConstants.ENTITY) T entity);

    /**
     * Flush批处理Batcg模式下的SQL执行
     *
     * @return 结果
     */
    @Flush
    List<BatchResult> flush();
}
