package club.emperorws.orm.plus.toolkit;

import club.emperorws.orm.logging.Log;
import club.emperorws.orm.logging.LogFactory;
import club.emperorws.orm.session.SqlSession;
import club.emperorws.orm.starter.session.proxy.SqlSessionFactoryProxyBean;
import club.emperorws.orm.starter.util.Assert;
import club.emperorws.orm.starter.util.SqlSessionUtils;
import club.emperorws.orm.util.MapUtil;
import club.emperorws.orm.plus.dialect.DbType;
import club.emperorws.orm.plus.exception.OrmPlusException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Jdbc工具类
 *
 * @author: 892638
 * @date: 2023/5/19 16:10
 * @description: JdbcUtils: Jdbc工具类
 */
public class JdbcUtils {

    private static final Log logger = LogFactory.getLog(JdbcUtils.class);

    private static final Map<String, DbType> JDBC_DB_TYPE_CACHE = new ConcurrentHashMap<>();

    /**
     * 无法直接通过SqlSession获取，SqlSession是动态代理的，获取的connection是已关闭的。所以需要手动获取connection，然后手动关闭connection
     *
     * @return DbType
     */
    public static DbType getDbType() {
        SqlSession sqlSession = null;
        try {
            sqlSession = SqlSessionFactoryProxyBean.me().getObject().openSession();
            Connection conn = sqlSession.getConnection();
            return MapUtil.computeIfAbsent(JDBC_DB_TYPE_CACHE, conn.getMetaData().getURL(), JdbcUtils::getDbType);
        } catch (SQLException e) {
            throw new OrmPlusException(e);
        } finally {
            if (sqlSession != null) {
                SqlSessionUtils.closeSqlSession(sqlSession, SqlSessionFactoryProxyBean.me().getObject());
            }
        }
    }

    /**
     * 根据连接地址判断数据库类型
     *
     * @param jdbcUrl 连接地址
     * @return ignore
     */
    public static DbType getDbType(String jdbcUrl) {
        Assert.isFalse(StringUtils.isBlank(jdbcUrl), "Error: The jdbcUrl is Null, Cannot read database type");
        String url = jdbcUrl.toLowerCase();
        if (url.contains(":mysql:") || url.contains(":cobar:")) {
            return DbType.MYSQL;
        } else if (url.contains(":mariadb:")) {
            return DbType.MARIADB;
        } else if (url.contains(":oracle:")) {
            return DbType.ORACLE;
        } else if (url.contains(":sqlserver:") || url.contains(":microsoft:")) {
            return DbType.SQL_SERVER2005;
        } else if (url.contains(":sqlserver2012:")) {
            return DbType.SQL_SERVER;
        } else if (url.contains(":postgresql:")) {
            return DbType.POSTGRE_SQL;
        } else if (url.contains(":hsqldb:")) {
            return DbType.HSQL;
        } else if (url.contains(":db2:")) {
            return DbType.DB2;
        } else if (url.contains(":sqlite:")) {
            return DbType.SQLITE;
        } else if (url.contains(":h2:")) {
            return DbType.H2;
        } else if (regexFind(":dm\\d*:", url)) {
            return DbType.DM;
        } else if (url.contains(":xugu:")) {
            return DbType.XU_GU;
        } else if (regexFind(":kingbase\\d*:", url)) {
            return DbType.KINGBASE_ES;
        } else if (url.contains(":phoenix:")) {
            return DbType.PHOENIX;
        } else if (url.contains(":zenith:")) {
            return DbType.GAUSS;
        } else if (url.contains(":gbase:")) {
            return DbType.GBASE;
        } else if (url.contains(":gbasedbt-sqli:") || url.contains(":informix-sqli:")) {
            return DbType.GBASE_8S;
        } else if (url.contains(":ch:") || url.contains(":clickhouse:")) {
            return DbType.CLICK_HOUSE;
        } else if (url.contains(":oscar:")) {
            return DbType.OSCAR;
        } else if (url.contains(":sybase:")) {
            return DbType.SYBASE;
        } else if (url.contains(":oceanbase:")) {
            return DbType.OCEAN_BASE;
        } else if (url.contains(":highgo:")) {
            return DbType.HIGH_GO;
        } else if (url.contains(":cubrid:")) {
            return DbType.CUBRID;
        } else if (url.contains(":goldilocks:")) {
            return DbType.GOLDILOCKS;
        } else if (url.contains(":csiidb:")) {
            return DbType.CSIIDB;
        } else if (url.contains(":sap:")) {
            return DbType.SAP_HANA;
        } else if (url.contains(":impala:")) {
            return DbType.IMPALA;
        } else if (url.contains(":vertica:")) {
            return DbType.VERTICA;
        } else if (url.contains(":xcloud:")) {
            return DbType.XCloud;
        } else if (url.contains(":firebirdsql:")) {
            return DbType.FIREBIRD;
        } else if (url.contains(":redshift:")) {
            return DbType.REDSHIFT;
        } else if (url.contains(":opengauss:")) {
            return DbType.OPENGAUSS;
        } else if (url.contains(":taos:") || url.contains(":taos-rs:")) {
            return DbType.TDENGINE;
        } else if (url.contains(":informix")) {
            return DbType.INFORMIX;
        } else if (url.contains(":uxdb:")) {
            return DbType.UXDB;
        } else {
            logger.warn("The jdbcUrl is " + jdbcUrl + ", Mybatis Plus Cannot Read Database type or The Database's Not Supported!");
            return DbType.OTHER;
        }
    }

    /**
     * 正则匹配
     *
     * @param regex 正则
     * @param input 字符串
     * @return 验证成功返回 true，验证失败返回 false
     */
    public static boolean regexFind(String regex, CharSequence input) {
        if (null == input) {
            return false;
        }
        return Pattern.compile(regex).matcher(input).find();
    }
}
