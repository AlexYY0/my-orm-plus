package club.emperorws.orm.plus;

import club.emperorws.orm.plus.consts.SqlConstants;
import club.emperorws.orm.plus.consts.StringPool;
import club.emperorws.orm.plus.dto.PreparedSql;
import club.emperorws.orm.plus.segments.MergeSegments;
import club.emperorws.orm.plus.segments.NormalSegmentList;
import club.emperorws.orm.plus.toolkit.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 最顶层SQL条件封装类
 *
 * @param <T> 需要封装查询的实体类
 * @author 892638
 * @date 2022.09.16 18:13
 **/
public abstract class Wrapper<T> implements ISqlSegment {

    public abstract T getEntity();

    public String getSqlSelect() {
        return null;
    }

    public String getSqlSet() {
        return null;
    }

    public String getSqlComment() {
        return null;
    }

    public String getSqlFirst() {
        return null;
    }

    public String getSqlFrom() {
        return null;
    }

    public abstract MergeSegments getExpression();

    /**
     * 获取WHERE 之后的所有SQL语句
     *
     * @return sql语句
     */
    public String getAfterWhereSqlSegment() {
        MergeSegments expression = getExpression();
        if (Objects.nonNull(expression)) {
            NormalSegmentList normal = expression.getNormal();
            String afterWhereSql = getSqlSegment();
            if (StringUtils.isNotBlank(afterWhereSql)) {
                if (normal.isEmpty()) {
                    return afterWhereSql;
                } else {
                    return SqlConstants.WHERE + StringPool.SPACE + afterWhereSql;
                }
            }
        }
        return StringPool.EMPTY;
    }

    /**
     * 解析、获取预编译SQL语句的条件（默认没有mapping，尚未实现该功能   例: javaType=int,jdbcType=NUMERIC这种）
     * 由于冯导写的ORM conditionList必须是String，所以只能强转
     *
     * @return 预编译SQL语句的条件
     */
    public abstract List<String> processAndGetCondition();

    /**
     * 获取预编译sql信息（#{xxx}占位符替换为?）
     *
     * @return 预编译sql信息（#{xxx}占位符替换为?）
     */
    public abstract PreparedSql getPreparedSql();

    abstract public void clear();
}
