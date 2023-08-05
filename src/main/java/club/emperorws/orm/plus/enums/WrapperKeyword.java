package club.emperorws.orm.plus.enums;

import club.emperorws.orm.plus.ISqlSegment;

/**
 * todo wrapper内部使用枚举
 *
 * @author EmperorWS
 * @date 2022.09.16 23:16
 **/
public enum WrapperKeyword implements ISqlSegment {

    /**
     * 无实际意义，仅标记作用，用于嵌套的叠加标识
     */
    APPLY(null);

    private final String keyword;

    WrapperKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String getSqlSegment() {
        return keyword;
    }
}
