package club.emperorws.orm.plus.dto;

import club.emperorws.orm.plus.consts.StringPool;

import java.io.Serializable;

/**
 * SQL查询字段String封装
 *
 * @author 892638
 * @date 2022.09.17 23:47
 **/
public class SharedString implements Serializable {

    /**
     * String内容
     */
    private String stringValue;

    public SharedString() {
    }

    public SharedString(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public static SharedString emptyString() {
        return new SharedString(StringPool.EMPTY);
    }

    public void toEmpty() {
        stringValue = StringPool.EMPTY;
    }

    public void toNull() {
        stringValue = null;
    }
}
