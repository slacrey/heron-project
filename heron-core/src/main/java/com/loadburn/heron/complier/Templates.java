package com.loadburn.heron.complier;

import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.loadburn.heron.bind.DefaultResponse;
import com.loadburn.heron.complier.Compilers;
import com.loadburn.heron.render.Renderable;
import com.loadburn.heron.utils.HeronUtils;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-28
 */
@Singleton
public class Templates {
    private final Compilers compilers;
    private final ConcurrentMap<Class<?>, Renderable> templates = new MapMaker().makeMap();

    @Inject
    public Templates(Compilers compilers) {
        this.compilers = compilers;
    }

    public void loadAll(Set<TemplateDescription> templates) {
        Preconditions.checkArgument(null != templates, "模板Set集合不能为空: %s", templates);
        for (TemplateDescription template : templates) {
            Renderable compiled = compilers.compile(template.clazz);
            Preconditions.checkArgument(null != compiled, "没有模板关联到: %s",
                    template.clazz);
            this.templates.put(template.clazz, compiled);
        }
    }

    public String render(Class<?> clazz, Object context) {
        Renderable compiled;
        // 判断类的模板是否已经加载
        if (templates.containsKey(clazz)) {
            compiled = templates.get(clazz);
        } else {
            compiled = compilers.compile(clazz);
            templates.put(clazz, compiled);
        }
        Preconditions.checkArgument(null != compiled, "没有模板关联到: %s", clazz);

        return new DefaultResponse(context).toString();
    }

    public static class TemplateDescription {
        private final Class<?> clazz;
        private final String fileName;

        public TemplateDescription(Class<?> clazz, String fileName) {
            this.clazz = clazz;
            this.fileName = fileName;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public String getFileName() {
            return fileName;
        }
    }
}
