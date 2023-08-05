package club.emperorws.orm.plus.segments;

import club.emperorws.orm.plus.ISqlSegment;
import club.emperorws.orm.plus.enums.SqlKeyword;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ORDER BY语句SQL片段
 *
 * @author 892638
 * @date 2022.09.17 00:49
 **/
public class OrderBySegmentList extends AbstractISegmentList {

    @Override
    protected boolean transformList(List<ISqlSegment> list, ISqlSegment firstSegment, ISqlSegment lastSegment) {
        //删除第一个标识符
        list.remove(0);
        final List<ISqlSegment> sqlSegmentList = new ArrayList<>(list);
        list.clear();
        list.add(() -> sqlSegmentList.stream().map(ISqlSegment::getSqlSegment).collect(Collectors.joining(SPACE)));
        return true;
    }

    @Override
    protected String childrenSqlSegment() {
        if (isEmpty()) {
            return EMPTY;
        }
        return this.stream().map(ISqlSegment::getSqlSegment).collect(Collectors.joining(COMMA, SPACE + SqlKeyword.ORDER_BY.getSqlSegment() + SPACE, EMPTY));
    }
}
