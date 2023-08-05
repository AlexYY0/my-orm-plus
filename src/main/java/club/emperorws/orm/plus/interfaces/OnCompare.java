package club.emperorws.orm.plus.interfaces;

import java.io.Serializable;

/**
 * table join on condition
 * 暂时只实现equals，如有需要，后续再加
 *
 * @author: 892638
 * @date: 2023/4/10 15:23
 * @description: OnCompare: table join on condition
 */
public interface OnCompare<Children, R> extends Serializable {

    default Children joinEq(R column1, R column2) {
        return joinEq(true, column1, column2);
    }

    /**
     * 等于 =
     *
     * @param condition 查询条件是否生效
     * @param column1   列名称1
     * @param column2   列名称2
     * @return this
     */
    Children joinEq(boolean condition, R column1, R column2);
}
