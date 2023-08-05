package club.emperorws.orm.plus.toolkit;

import club.emperorws.orm.metadata.TableModelFieldInfo;
import club.emperorws.orm.metadata.TableModelInfo;
import club.emperorws.orm.metadata.TableModelInfoHelper;
import club.emperorws.orm.plus.consts.StringPool;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 获取ORM中数据库与实体的映射关系
 *
 * @author 892638
 * @date 2022.09.19 11:06
 **/
public class TableInfoHelper {

    /**
     * 获取表名称
     *
     * @param clazz 表对应的实体类型
     * @return 数据库表名称
     */
    public static String getTableName(Class<?> clazz) {
        return TableModelInfoHelper.getTableInfo(clazz).getTableName();
    }


    /**
     * 按需 获取需要进行查询的 select sql 片段
     * 否则只查询主键
     *
     * @param clazz     反射实体类
     * @param predicate 过滤条件
     * @return sql 片段
     */
    public static String chooseSelect(Class<?> clazz, Predicate<TableModelFieldInfo> predicate) {
        //1. 获取实体信息
        TableModelInfo tableInfo = TableModelInfoHelper.getTableInfo(clazz);
        List<TableModelFieldInfo> fieldInfoList = tableInfo.getFieldInfoList();
        String fieldsSqlSelect = fieldInfoList.stream().filter(predicate).map(TableModelFieldInfo::getColumn).collect(Collectors.joining(StringPool.COMMA));
        if (StringUtils.isNotBlank(fieldsSqlSelect)) {
            return fieldsSqlSelect;
        }
        return tableInfo.getPkName();
    }
}
