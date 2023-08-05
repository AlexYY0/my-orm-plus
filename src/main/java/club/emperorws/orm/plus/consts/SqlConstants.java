package club.emperorws.orm.plus.consts;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * ORM PLUS常用常量
 *
 * @author EmperorWS
 * @date 2022.09.17 01:36
 **/
public interface SqlConstants extends StringPool, Serializable {

    String SELECT = "SELECT";

    String UPDATE = "UPDATE";

    String SET = "SET";

    String DELETE = "DELETE";

    String INSERT = "INSERT";

    String INTO = "INTO";

    String VALUES = "VALUES";

    String FROM = "FROM";

    String WHERE = "WHERE";

    String LIMIT = "LIMIT";

    String PAGE = "page";

    String OFFSET_PARAM = PAGE + DOT + "startNum";

    String LIMIT_PARAM = PAGE + DOT + "endNum";

    /**
     * wrapper 内部参数相关
     */
    String WRAPPER_PARAM = "MPGENVAL";

    /**
     * wrapper 内部存储参数
     */
    String WRAPPER_PARAM_MIDDLE = ".paramNameValuePairs" + DOT;

    /**
     * wrapper 类的@Param名称
     */
    String WRAPPER = "ew";

    /**
     * 对象的@Param名称
     */
    String ENTITY = "entity";

    String TABLE_ALIAS = "t";

    /**
     * SQL格式化出预编译占位符的正则表达式
     */
    Pattern SQL_REPLACE_PATTERN = Pattern.compile("#\\{.+?}");
}
