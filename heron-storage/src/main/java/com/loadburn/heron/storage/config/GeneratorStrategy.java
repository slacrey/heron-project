package com.loadburn.heron.storage.config;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-14
 */
public enum GeneratorStrategy {

    StorageId(StorageId.class);

    private Class<?> clazz;

    GeneratorStrategy(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public int getIndex() {
        return ordinal();
    }

    // 获得 enum 对象
    public static GeneratorStrategy valueByIndex(int index) {
        for (GeneratorStrategy strategy : GeneratorStrategy.values()) {
            if (strategy.getIndex() == index) {
                return strategy;
            }
        }
        return null;
    }

    // 根据全名获得 enum 对象
    public static GeneratorStrategy valueByText(Class<?> clazz) {
        for (GeneratorStrategy strategy : GeneratorStrategy.values()) {
            if (strategy.getClazz().equals(clazz)) {
                return strategy;
            }
        }
        return null;
    }

}
