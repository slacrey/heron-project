package com.loadburn.heron.storage.config;

public enum DataBaseType {

    MySQL("MYSQL"), MsSQL("MSSQL"), MsSQL2000("MSSQL2000"), Oracle("ORACLE"), PostSQL("POSTSQL");

    private String text;

    DataBaseType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public int getIndex() {
        return ordinal();
    }

    // 获得 enum 对象
    public static DataBaseType valueByIndex(int index) {
        for (DataBaseType charsetEnum : DataBaseType.values()) {
            if (charsetEnum.getIndex() == index) {
                return charsetEnum;
            }
        }
        return null;
    }

    // 根据全名获得 enum 对象
    public static DataBaseType valueByText(String text) {
        for (DataBaseType charsetEnum : DataBaseType.values()) {
            if (charsetEnum.getText().equals(text)) {
                return charsetEnum;
            }
        }
        return null;
    }
}