package club.emperorws.orm.plus.dto;

import club.emperorws.orm.plus.consts.StringPool;
import club.emperorws.orm.plus.toolkit.StringUtils;

/**
 * 预编译好的Sql对象实体
 *
 * @author: EmperorWS
 * @date: 2023/4/12 9:57
 * @description: PreparedSql: 预编译好的Sql对象实体
 */
public class PreparedSql {

    /**
     * update ${table}
     */
    String srcUpdateStr;

    /**
     * set colum1=xxx1,colum2=xxx2,colum3=xxx3,
     */
    String srcSetStr;

    /**
     * select xxx,xxx,xxx
     */
    String srcSelectStr;

    /**
     * from ${table}
     */
    String srcFromStr;

    /**
     * where xxx
     * group by xxx
     * having xxx
     * order by xxx
     */
    String srcWhereStr;

    /**
     * 最终生成的SQL（带有#{xxx}占位符）
     */
    private SharedString srcSql = new SharedString();

    public PreparedSql setSrcUpdateStr(String srcUpdateStr) {
        this.srcUpdateStr = srcUpdateStr;
        return this;
    }

    public PreparedSql setSrcSetStr(String srcSetStr) {
        this.srcSetStr = srcSetStr;
        return this;
    }

    public PreparedSql setSrcSelectStr(String srcSelectStr) {
        this.srcSelectStr = srcSelectStr;
        return this;
    }

    public PreparedSql setSrcFromStr(String srcFromStr) {
        this.srcFromStr = srcFromStr;
        return this;
    }

    public PreparedSql setSrcWhereStr(String srcWhereStr) {
        this.srcWhereStr = srcWhereStr;
        return this;
    }

    public String getSrcUpdateStr() {
        return srcUpdateStr;
    }

    public String getSrcSetStr() {
        return srcSetStr;
    }

    public String getSrcSelectStr() {
        return srcSelectStr;
    }

    public String getSrcFromStr() {
        return srcFromStr;
    }

    public String getSrcWhereStr() {
        return srcWhereStr;
    }

    /**
     * 获取原始sql字符串，含#{xxx}占位符
     * <p>实际上，真正使用该方法的是嵌套子查询，以及获取sql的conditionList</p>
     * <p>因为只有update、嵌套子查询时，才完全正确，只有where时完全不正确</p>
     *
     * @return 原始sql字符串
     */
    public String getSrcSqlStr() {
        if (StringUtils.isEmpty(srcSql.getStringValue())) {
            if (StringUtils.isBlank(srcSetStr) && StringUtils.isBlank(srcSelectStr)) {
                //对于Query，由于没有select，会自动拼接'*'，导致永远不会进入这里，这对于delete（只有where时）是不友好的
                srcSql.setStringValue(srcWhereStr);
            } else if (StringUtils.isBlank(srcSetStr)) {
                srcSql.setStringValue(srcSelectStr + StringPool.SPACE + srcFromStr + StringPool.SPACE + srcWhereStr);
            } else {
                srcSql.setStringValue(srcUpdateStr + StringPool.SPACE + srcSetStr + StringPool.SPACE + srcWhereStr);
            }
        }
        return srcSql.getStringValue();
    }

    public void clear() {
        this.srcUpdateStr = null;
        this.srcSetStr = null;
        this.srcSelectStr = null;
        this.srcFromStr = null;
        this.srcWhereStr = null;
        this.srcSql.toNull();
    }

    public boolean isNotEmpty() {
        return StringUtils.isNotEmpty(srcUpdateStr) || StringUtils.isNotEmpty(srcSetStr) || StringUtils.isNotEmpty(srcSelectStr) || StringUtils.isNotEmpty(srcFromStr) || StringUtils.isNotEmpty(srcWhereStr);
    }
}
