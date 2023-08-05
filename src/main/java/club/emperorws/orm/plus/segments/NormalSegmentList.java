package club.emperorws.orm.plus.segments;

import club.emperorws.orm.plus.ISqlSegment;
import club.emperorws.orm.plus.enums.SqlKeyword;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 普通SQL片段
 *
 * @author EmperorWS
 * @date 2022.09.17 00:01
 **/
public class NormalSegmentList extends AbstractISegmentList {

    /**
     * 是否处理了上一个not（因为not必须在 AND OR的后面，所以只能先 AND OR，再not）
     */
    private boolean executeNot = true;

    public NormalSegmentList() {
        this.flushLastValue = true;
    }

    /**
     * 一些逻辑判断以及list内部元素更改
     *
     * @param list         拼接SQL的ISqlSegment集合
     * @param firstSegment list集合里第一个Segment片段
     * @param lastSegment  list集合里最后第一个Segment片段
     * @return true：继续下一步；false：结束执行
     */
    @Override
    protected boolean transformList(List<ISqlSegment> list, ISqlSegment firstSegment, ISqlSegment lastSegment) {
        if (list.size() == 1) {
            //特殊连接符会进入，一般是and()、or()、not()方法
            if (!MatchSegment.NOT.match(firstSegment)) {
                //不是not()
                if (isEmpty()) {
                    return false;
                }
                boolean matchLastAnd = MatchSegment.AND.match(lastValue);
                boolean matchLastOr = MatchSegment.OR.match(lastValue);
                if (matchLastAnd || matchLastOr) {
                    //判断上一个值，防止重复添加
                    if (matchLastAnd && MatchSegment.AND.match(firstSegment)) {
                        return false;
                    } else if (matchLastOr && MatchSegment.OR.match(firstSegment)) {
                        return false;
                    } else {
                        //删除上一个值，替换成最新的
                        removeAndFlushLast();
                    }
                }
            } else {
                executeNot = false;
                return false;
            }
        } else {
            if (MatchSegment.APPLY.match(firstSegment)) {
                //嵌套添加，删除第一个标识符
                list.remove(0);
            }
            if (!MatchSegment.AND_OR.match(lastValue) && !isEmpty()) {
                //默认为AND拼接
                add(SqlKeyword.AND);
            }
            if (!executeNot) {
                list.add(0, SqlKeyword.NOT);
                executeNot = true;
            }
        }
        return true;
    }

    @Override
    protected String childrenSqlSegment() {
        if (MatchSegment.AND_OR.match(lastValue)) {
            //删除最后一个多出的关键字段
            removeAndFlushLast();
        }
        final String str = this.stream().map(ISqlSegment::getSqlSegment).collect(Collectors.joining(SPACE));
        return LEFT_BRACKET + str + RIGHT_BRACKET;
    }

    @Override
    public void clear() {
        super.clear();
        flushLastValue = true;
        executeNot = true;
    }
}
