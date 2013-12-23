package com.loadburn.heron.render;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-28
 */
@Singleton
public class ModelProvider implements Provider<Model<String, Object>> {

    public ModelProvider() {
    }

    @Override
    public Model<String, Object> get() {
        return new Model<String, Object>() {

            private HashMap<String, Object> hashMap = Maps.newHashMap();

            @Override
            public Model<String, Object> put(String key, Object value) {
                hashMap.put(key, value);
                return this;
            }

            @Override
            public Object get(String key) {
                return hashMap.get(key);
            }

            @Override
            public Model<String, Object> putAll(Map<String, Object> attributes) {
                hashMap.putAll(attributes);
                return this;
            }

            @Override
            public Model<String, Object> remove(String key) {
                hashMap.remove(key);
                return this;
            }

        };
    }
}
