package club.emperorws.orm.plus.segments;

import club.emperorws.orm.plus.ISqlSegment;
import club.emperorws.orm.plus.enums.SqlKeyword;

import java.util.List;
import java.util.stream.Collectors;

/**
 * HAVING语句SQL片段
 *
 * @author EmperorWS
 * @date 2022.09.17 00:40
 **/
public class HavingSegmentList extends AbstractISegmentList {

    @Override
    protected boolean transformList(List<ISqlSegment> list, ISqlSegment firstSegment, ISqlSegment lastSegment) {
        if (!isEmpty()) {
            this.add(SqlKeyword.AND);
        }
        //删除第一个标识符
        list.remove(0);
        return true;
    }

    @Override
    protected String childrenSqlSegment() {
        if (isEmpty()) {
            return EMPTY;
        }
        return this.stream().map(ISqlSegment::getSqlSegment).collect(Collectors.joining(SPACE, SPACE + SqlKeyword.HAVING.getSqlSegment() + SPACE, EMPTY));
    }
}
