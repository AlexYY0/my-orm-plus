package club.emperorws.orm.plus.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 分页相关数据
 *
 * @author 892638
 * @date 2022.09.13 15:49
 **/
public class PageInfo<T> implements Serializable {

    /**
     * 查询结果
     */
    private List<T> list;

    /**
     * 分页信息：页码
     */
    private long pageNumber;

    /**
     * 分页信息：页大小
     */
    private long pageSize;

    /**
     * 查询偏移量
     */
    private long startNum;

    /**
     * 查询截至量
     */
    private long endNum;

    /**
     * 总行数
     */
    private long totalRow;

    /**
     * 总页数
     */
    private long totalPage;

    /**
     * 当前查询到的行数
     */
    private long rows;

    public PageInfo() {
    }

    public PageInfo(long pageNumber, long pageSize) {
        //初始化纠正分页数据
        this.pageNumber = Math.max(pageNumber, 1);
        this.pageSize = Math.max(pageSize, 0);
        this.startNum = (this.pageNumber - 1) * this.pageSize + 1;
        this.endNum = this.pageNumber * this.pageSize;
    }

    public PageInfo(List<T> list, long pageNumber, long pageSize, long totalRow, long totalPage) {
        this.list = list;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalRow = totalRow;
        this.totalPage = totalPage;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public long getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(long pageNumber) {
        this.pageNumber = pageNumber;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public long getStartNum() {
        return startNum;
    }

    public void setStartNum(long startNum) {
        this.startNum = startNum;
    }

    public long getEndNum() {
        return endNum;
    }

    public void setEndNum(long endNum) {
        this.endNum = endNum;
    }

    public long getTotalRow() {
        return totalRow;
    }

    public void setTotalRow(long totalRow) {
        this.totalRow = totalRow;
    }

    public long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(long totalPage) {
        this.totalPage = totalPage;
    }

    public long getRows() {
        return rows;
    }

    public void setRows(long rows) {
        this.rows = rows;
    }
}
