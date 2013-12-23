package com.loadburn.heron.bind;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.loadburn.heron.complier.Evaluator;
import com.loadburn.heron.excetions.InvalidBindingException;
import com.loadburn.heron.multipart.commons.CommonsMultipartFile;
import com.loadburn.heron.enums.CharsetEnum;
import com.loadburn.heron.utils.StringUtils;
import net.jcip.annotations.Immutable;
import org.apache.commons.fileupload.FileItem;
import org.mvel2.PropertyAccessException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Immutable
@Singleton
public class MultiPartRequestBinder implements RequestBinder<FileItem> {
    private final Evaluator evaluator;
    private final Provider<FlashCache> cacheProvider;
    private final Logger log = Logger.getLogger(MultiPartRequestBinder.class.getName());

    private static final String VALID_BINDING_REGEX = "[\\w\\.$]*";

    @Inject
    public MultiPartRequestBinder(Evaluator evaluator, Provider<FlashCache> cacheProvider) {
        this.evaluator = evaluator;
        this.cacheProvider = cacheProvider;
    }

    public void bind(Request<FileItem> request, Object o) {

        final Multimap<String, FileItem> map = request.params();

        //循环绑定
        for (Map.Entry<String, Collection<FileItem>> entry : map.asMap().entrySet()) {
            String key = entry.getKey();
            final Collection<FileItem> values = entry.getValue();
            if (!validate(key))
                return;
            Object value;
            if (values.size() > 1) {
                value = Lists.newArrayList(values);
                bindValueToBound(key, o, value);
            } else {

                FileItem fileItem = Iterables.getOnlyElement(values);
                if (!fileItem.isFormField()) {
                    bindValueToBound(key, o, new CommonsMultipartFile(fileItem));
                } else {
                    String rawValue = null;
                    try {
                        rawValue = fileItem.getString(CharsetEnum.UTF8.getText());
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }

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
                    bindValueToBound(key, o, value);
                }
            }
        }
    }

    private void bindValueToBound(String key, Object o, Object value) {
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

    private void addContextAndThrow(Object bound, String key, Object value, Throwable cause) {
        throw new RuntimeException(String.format(
                "给[%s]上的[%s]设置值[%s]出现问题",
                bound, key, value), cause);
    }

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