package club.emperorws.orm.plus.segments;

import club.emperorws.orm.plus.ISqlSegment;
import club.emperorws.orm.plus.enums.SqlKeyword;

import java.util.List;
import java.util.stream.Collectors;

/**
 * GROUP BY语句SQL片段
 *
 * @author EmperorWS
 * @date 2022.09.17 00:40
 **/
public class GroupBySegmentList extends AbstractISegmentList {

    @Override
    protected boolean transformList(List<ISqlSegment> list, ISqlSegment firstSegment, ISqlSegment lastSegment) {
        //删除第一个标识符
        list.remove(0);
        return true;
    }

    @Override
    protected String childrenSqlSegment() {
        if (isEmpty()) {
            return EMPTY;
        }
        return this.stream().map(ISqlSegment::getSqlSegment).collect(Collectors.joining(COMMA, SPACE + SqlKeyword.GROUP_BY.getSqlSegment() + SPACE, EMPTY));
    }
}
