package com.loadburn.heron.bind;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.loadburn.heron.complier.Evaluator;
import com.loadburn.heron.excetions.InvalidBindingException;
import com.loadburn.heron.utils.StringUtils;
import net.jcip.annotations.Immutable;
import org.mvel2.PropertyAccessException;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Immutable
@Singleton
public class MvelRequestBinder implements RequestBinder<String> {
    private final Evaluator evaluator;
    private final Provider<FlashCache> cacheProvider;
    private final Logger log = Logger.getLogger(MvelRequestBinder.class.getName());

    private static final String VALID_BINDING_REGEX = "[\\w\\.$]*";

    @Inject
    public MvelRequestBinder(Evaluator evaluator, Provider<FlashCache> cacheProvider) {
        this.evaluator = evaluator;
        this.cacheProvider = cacheProvider;
    }

    public void bind(Request<String> request, Object o) {
        final Multimap<String, String> map = request.params();

        //循环绑定
        for (Map.Entry<String, Collection<String>> entry : map.asMap().entrySet()) {
            String key = entry.getKey();

            final Collection<String> values = entry.getValue();

            if (!validate(key))
                return;

            Object value;

            if (values.size() > 1) {
                value = Lists.newArrayList(values);
            } else {

                String rawValue = Iterables.getOnlyElement(values);
                if (rawValue.startsWith(COLLECTION_BIND_PREFIX)) {
                    final String[] binding = rawValue.substring(COLLECTION_BIND_PREFIX.length()).split("/");
                    if (binding.length != 2)
                        throw new InvalidBindingException(
                                "Collection sources must be bound in the form '[C/collection/hashcode'. "
                                        + "Was the request corrupt? Or did you try to bind something manually"
                                        + " with a key starting '[C/'? Was: " + rawValue);

                    final Collection<?> collection = cacheProvider.get().get(binding[0]);

                    value = search(collection, binding[1]);
                } else {
                    value = rawValue;
                }
            }

            try {
                evaluator.write(key, o, value);
            } catch (PropertyAccessException e) {

                if (e.getCause() instanceof InvocationTargetException) {
                    addContextAndThrow(o, key, value, e.getCause());
                }
                if (log.isLoggable(Level.FINER)) {
                    log.finer("属性[" + key + "]不能被绑定,"
                            + " 但是不是错误,可能是因为没有该属性.");
                }
            } catch (Exception e) {
                addContextAndThrow(o, key, value, e);
            }
        }
    }

    private void addContextAndThrow(Object bound, String key, Object value, Throwable cause) {
        throw new RuntimeException(String.format(
                "给[%s]上的[%s]设置值[%s]出现问题",
                bound, key, value), cause);
    }

    /**
     * 搜索集合中对象是否存在指定hashcode的实例
     * @param collection 集合
     * @param hashKey hashcode
     * @return
     */
    private Object search(Collection<?> collection, String hashKey) {
        int hash = Integer.valueOf(hashKey);
        for (Object o : collection) {
            if (o.hashCode() == hash)
                return o;
        }
        return null;
    }

    private boolean validate(String binding) {
        if (StringUtils.empty(binding) || !binding.matches(VALID_BINDING_REGEX)) {
            log.warning(
                    "绑定表达式包含无效的字符: " + binding
                            + " (ignoring)");
            return false;
        }
        return true;
    }

}