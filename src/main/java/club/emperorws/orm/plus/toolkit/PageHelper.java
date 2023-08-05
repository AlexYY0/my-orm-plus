package club.emperorws.orm.plus.toolkit;

import club.emperorws.orm.plus.dto.PageInfo;

/**
 * 创建分页信息工具类
 *
 * @author 892638
 * @date 2022.09.18 22:34
 **/
public class PageHelper {

    /**
     * 创建分页信息
     *
     * @param pageNum  页数
     * @param pageSize 每页数量
     * @param <T>      实体信息
     * @return 分页信息
     */
    public static  <T> PageInfo<T> startPage(long pageNum, long pageSize) {
        return new PageInfo<T>(pageNum, pageSize);
    }
}
