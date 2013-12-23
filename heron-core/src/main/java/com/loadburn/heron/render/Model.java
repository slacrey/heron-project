package com.loadburn.heron.render;

import java.util.Map;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-28
 */
public interface Model<S, T> {

    /**
     * 增加属性
     * @param key 属性名
     * @param value 属性值
     * @return
     */
    Model<S, T> put(String key, Object value);

    /**
     * 根据属性名得到属性值
     * @param key 属性名
     * @return
     */
    T get(String key);

    /**
     * 批量增加属性
     * @param maps 属性map
     * @return
     */
    Model<S, T> putAll(Map<S, T> maps);

    /**
     *  根据key删除
     * @param key 属性名
     * @return
     */
    Model<S, T> remove(String key);

}
