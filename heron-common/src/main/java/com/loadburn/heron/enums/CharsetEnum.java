package com.loadburn.heron.enums;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-7
 */
public enum CharsetEnum {

    UTF8("UTF-8"), GBK("GBK"), GB2312("GB2312");

    private String text;

    CharsetEnum(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public int getIndex() {
        return ordinal();
    }

    // 获得 enum 对象
    public static CharsetEnum valueByIndex(int index) {
        for (CharsetEnum charsetEnum : CharsetEnum.values()) {
            if (charsetEnum.getIndex() == index) {
                return charsetEnum;
            }
        }
        return null;
    }

    // 根据全名获得 enum 对象
    public static CharsetEnum valueByText(String text) {
        for (CharsetEnum charsetEnum : CharsetEnum.values()) {
            if (charsetEnum.getText().equals(text)) {
                return charsetEnum;
            }
        }
        return null;
    }

}
