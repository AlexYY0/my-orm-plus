package club.emperorws.orm.plus.enums;

/**
 * BaseMapper定义好的SQL语句
 *
 * @author: EmperorWS
 * @date: 2023/5/19 10:24
 * @description: SqlMethod: BaseMapper定义好的SQL语句
 */
public enum SqlMethod {

    /**
     * 插入
     */
    INSERT_ONE("insert", "插入一条数据（全字段插入）", "INSERT INTO %s %s VALUES %s"),

    /**
     * 修改
     */
    UPDATE_BY_PRIMARY_KEY("updateByPrimaryKey", "根据ID 选择修改数据", "UPDATE %s SET %s WHERE %s=#{%s}"),
    UPDATE("update", "根据 Wrapper 条件，更新记录", "UPDATE %s SET %s %s"),

    /**
     * 查询
     */
    SELECT_BY_ID("selectById", "根据ID 查询一条数据", "SELECT %s FROM %s WHERE %s=#{%s} %s"),
    SELECT_COUNT("selectCount", "查询满足条件总记录数", "SELECT COUNT(%s) AS total %s %s");

    private final String method;
    private final String desc;
    private final String sql;

    SqlMethod(String method, String desc, String sql) {
        this.method = method;
        this.desc = desc;
        this.sql = sql;
    }

    public String getMethod() {
        return method;
    }

    public String getDesc() {
        return desc;
    }

    public String getSql() {
        return sql;
    }
}
