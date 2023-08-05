package club.emperorws.orm.plus.toolkit;

import club.emperorws.orm.mapping.SqlSource;

/**
 * SqlSource辅助工具类
 *
 * @author: 892638
 * @date: 2023/6/30 17:39
 * @description: SqlSourceHelper: SqlSource辅助工具类
 */
public class SqlSourceHelper {

    /**
     * 创建SqlSource的工具类
     *
     * @param dynamicSql orm需要的动态sql
     * @return 执行Mapper需要的SqlSource
     */
    public static SqlSource createSqlSource(String dynamicSql) {
        return new SqlSource.Builder(dynamicSql).build();
    }
}
