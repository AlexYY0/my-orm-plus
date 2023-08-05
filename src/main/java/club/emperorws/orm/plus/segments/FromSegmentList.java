package club.emperorws.orm.plus.segments;

import club.emperorws.orm.plus.ISqlSegment;

import java.util.List;
import java.util.stream.Collectors;

import static club.emperorws.orm.plus.consts.SqlConstants.FROM;


/**
 * 存储from的sql片段集合
 *
 * @author: EmperorWS
 * @date: 2023/4/11 10:12
 * @description: FromSegmentList: 存储from的sql片段集合
 */
public class FromSegmentList extends AbstractISegmentList {

    /**
     * 一些逻辑判断以及list内部元素更改
     *
     * @param list         拼接SQL的ISqlSegment集合
     * @param firstSegment list集合里第一个Segment片段
     * @param lastSegment  list集合里最后第一个Segment片段
     * @return true：继续；false：结束
     */
    @Override
    protected boolean transformList(List<ISqlSegment> list, ISqlSegment firstSegment, ISqlSegment lastSegment) {
        if (isEmpty()) {
            //删除第一个标识符
            list.remove(0);
        }
        return true;
    }

    /**
     * 执行过addAll方法，才会执行此方法，因为cacheSqlSegment一开始即为true
     *
     * @return sql拼接结果
     */
    @Override
    protected String childrenSqlSegment() {
        if (isEmpty()) {
            return EMPTY;
        }
        return this.stream().map(ISqlSegment::getSqlSegment).collect(Collectors.joining(SPACE, SPACE + FROM + SPACE, EMPTY));
    }
}
