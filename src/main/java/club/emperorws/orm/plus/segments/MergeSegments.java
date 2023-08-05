package club.emperorws.orm.plus.segments;

import club.emperorws.orm.plus.ISqlSegment;
import club.emperorws.orm.plus.consts.StringPool;

import java.util.Arrays;
import java.util.List;

/**
 * 合并SQL片段为SQL语句
 *
 * @author EmperorWS
 * @date 2022.09.16 22:08
 **/
public class MergeSegments implements ISqlSegment {

    private final NormalSegmentList normal = new NormalSegmentList();
    private final GroupBySegmentList groupBy = new GroupBySegmentList();
    private final OrderBySegmentList orderBy = new OrderBySegmentList();
    private final HavingSegmentList having = new HavingSegmentList();
    private final FromSegmentList from = new FromSegmentList();

    private String sqlSegment = StringPool.EMPTY;

    private boolean cacheSqlSegment = true;

    public NormalSegmentList getNormal() {
        return normal;
    }

    public GroupBySegmentList getGroupBy() {
        return groupBy;
    }

    public OrderBySegmentList getOrderBy() {
        return orderBy;
    }

    public HavingSegmentList getHaving() {
        return having;
    }

    public FromSegmentList getFrom() {
        return from;
    }

    public void add(ISqlSegment... iSqlSegments) {
        List<ISqlSegment> list = Arrays.asList(iSqlSegments);
        ISqlSegment firstSqlSegment = list.get(0);
        if (MatchSegment.ORDER_BY.match(firstSqlSegment)) {
            orderBy.addAll(list);
        } else if (MatchSegment.GROUP_BY.match(firstSqlSegment)) {
            groupBy.addAll(list);
        } else if (MatchSegment.HAVING.match(firstSqlSegment)) {
            having.addAll(list);
        } else if (MatchSegment.JOIN.match(firstSqlSegment)) {
            from.addAll(list);
        } else {
            normal.addAll(list);
        }
        cacheSqlSegment = false;
    }

    @Override
    public String getSqlSegment() {
        if (cacheSqlSegment) {
            return sqlSegment;
        }
        cacheSqlSegment = true;
        if (normal.isEmpty()) {
            if (!groupBy.isEmpty() || !orderBy.isEmpty()) {
                sqlSegment = groupBy.getSqlSegment() + having.getSqlSegment() + orderBy.getSqlSegment();
            }
        } else {
            sqlSegment = normal.getSqlSegment() + groupBy.getSqlSegment() + having.getSqlSegment() + orderBy.getSqlSegment();
        }
        return sqlSegment;
    }

    public void clear() {
        sqlSegment = StringPool.EMPTY;
        cacheSqlSegment = true;
        normal.clear();
        groupBy.clear();
        orderBy.clear();
        having.clear();
        from.clear();
    }
}
