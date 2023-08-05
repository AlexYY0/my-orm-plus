package club.emperorws.orm.plus.segments;

import club.emperorws.orm.plus.ISqlSegment;
import club.emperorws.orm.plus.consts.StringPool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * SQL片段集合处理抽象类
 *
 * @author 892638
 * @date 2022.09.16 22:13
 **/
public abstract class AbstractISegmentList extends ArrayList<ISqlSegment> implements ISqlSegment, StringPool {

    /**
     * 最后一个值（主要用于做and or等符号判断）
     */
    ISqlSegment lastValue = null;

    /**
     * 刷新lastValue
     */
    boolean flushLastValue = false;

    /**
     * 结果集缓存（加快二次结果生成效率）
     */
    private String sqlSegment = EMPTY;

    /**
     * 是否缓存过结果集
     */
    private boolean cacheSqlSegment = true;

    /**
     * 重写该方法，为了支持个性化配置
     *
     * @param c 拼接SQL的ISqlSegment集合
     * @return 添加是否成功
     */
    @Override
    public boolean addAll(Collection<? extends ISqlSegment> c) {
        List<ISqlSegment> list = new ArrayList<>(c);
        boolean goOn = transformList(list, list.get(0), list.get(list.size() - 1));
        if (goOn) {
            cacheSqlSegment = false;
            if (flushLastValue) {
                this.flushLastValue(list);
            }
            return super.addAll(list);
        }
        return false;
    }

    /**
     * 一些逻辑判断以及list内部元素更改
     *
     * @param list         拼接SQL的ISqlSegment集合
     * @param firstSegment list集合里第一个Segment片段
     * @param lastSegment  list集合里最后第一个Segment片段
     * @return true：继续；false：结束
     */
    protected abstract boolean transformList(List<ISqlSegment> list, ISqlSegment firstSegment, ISqlSegment lastSegment);

    /**
     * 刷新lastValue
     *
     * @param list 拼接SQL的ISqlSegment集合
     */
    private void flushLastValue(List<ISqlSegment> list) {
        lastValue = list.get(list.size() - 1);
    }

    /**
     * 删除sql片段集合最后一个值，并刷新lastValue（最后一个是or and等特殊符号时会生效，删除这个多余的关键字）
     */
    void removeAndFlushLast() {
        remove(size() - 1);
        flushLastValue(this);
    }

    @Override
    public String getSqlSegment() {
        if (cacheSqlSegment) {
            return sqlSegment;
        }
        cacheSqlSegment = true;
        sqlSegment = childrenSqlSegment();
        return sqlSegment;
    }

    /**
     * 执行过addAll方法，才会执行此方法，因为cacheSqlSegment一开始即为true
     *
     * @return sql拼接结果
     */
    protected abstract String childrenSqlSegment();

    @Override
    public void clear() {
        super.clear();
        lastValue = null;
        sqlSegment = EMPTY;
        cacheSqlSegment = true;
    }
}
