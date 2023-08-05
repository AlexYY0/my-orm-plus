package club.emperorws.orm.plus.dialect;

import club.emperorws.orm.plus.dialect.dialects.*;
import club.emperorws.orm.plus.exception.OrmPlusException;

import java.util.EnumMap;
import java.util.Map;

/**
 * 数据库方言工厂
 *
 * @author: EmperorWS
 * @date: 2023/5/19 14:42
 * @description: DialectFactory: 数据库方言工厂
 */
public class DialectFactory {
    private static final Map<DbType, IDialect> DIALECT_ENUM_MAP = new EnumMap<>(DbType.class);

    public static IDialect getDialect(DbType dbType) {
        IDialect dialect = DIALECT_ENUM_MAP.get(dbType);
        if (null == dialect) {
            if (dbType == DbType.OTHER) {
                throw new OrmPlusException(String.format("%s database not supported.", dbType.getDb()));
            }
            // mysql same type
            else if (dbType == DbType.MYSQL
                    || dbType == DbType.MARIADB
                    || dbType == DbType.GBASE
                    || dbType == DbType.OSCAR
                    || dbType == DbType.XU_GU
                    || dbType == DbType.CLICK_HOUSE
                    || dbType == DbType.OCEAN_BASE
                    || dbType == DbType.CUBRID
                    || dbType == DbType.GOLDILOCKS
                    || dbType == DbType.CSIIDB) {
                dialect = new MySqlDialect();
            }
            // oracle same type
            else if (dbType == DbType.ORACLE
                    || dbType == DbType.DM
                    || dbType == DbType.GAUSS) {
                dialect = new OracleDialect();
            }
            // postgresql same type
            else if (dbType == DbType.POSTGRE_SQL
                    || dbType == DbType.H2
                    || dbType == DbType.SQLITE
                    || dbType == DbType.HSQL
                    || dbType == DbType.KINGBASE_ES
                    || dbType == DbType.PHOENIX
                    || dbType == DbType.SAP_HANA
                    || dbType == DbType.IMPALA
                    || dbType == DbType.HIGH_GO
                    || dbType == DbType.VERTICA
                    || dbType == DbType.REDSHIFT
                    || dbType == DbType.OPENGAUSS
                    || dbType == DbType.TDENGINE
                    || dbType == DbType.UXDB) {
                dialect = new PostgreDialect();
            }
            // other types
            else if (dbType == DbType.ORACLE_12C
                    || dbType == DbType.FIREBIRD
                    || dbType == DbType.SQL_SERVER) {
                dialect = new Oracle12cDialect();
            } else if (dbType == DbType.DB2) {
                dialect = new DB2Dialect();
            } else if (dbType == DbType.SQL_SERVER2005) {
                dialect = new SQLServer2005Dialect();
            } else if (dbType == DbType.SYBASE) {
                dialect = new SybaseDialect();
            } else if (dbType == DbType.GBASEDBT) {
                dialect = new GBasedbtDialect();
            } else if (dbType == DbType.GBASE_INFORMIX) {
                dialect = new GBaseInfromixDialect();
            } else if (dbType == DbType.XCloud) {
                dialect = new XCloudDialect();
            } else if (dbType == DbType.FIREBIRD) {
                dialect = new FirebirdDialect();
            } else if (dbType == DbType.GBASE_8S
                    || dbType == DbType.GBASEDBT
                    || dbType == DbType.GBASE_INFORMIX) {
                dialect = new GBase8sDialect();
            } else if (dbType == DbType.INFORMIX) {
                dialect = new InformixDialect();
            }
            DIALECT_ENUM_MAP.put(dbType, dialect);
        }
        return dialect;
    }
}
