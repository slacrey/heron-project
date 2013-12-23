package com.loadburn.heron.complier;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import java.util.Map;
import java.util.Set;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-27
 */
@Singleton
public class StandardTemplateSystem implements TemplateSystem {
    private static final String DEFAULT = "html";
    private final Map<String, Class<? extends TemplateCompiler>> templateCompilers;
    private final Injector injector;

    @Inject
    public StandardTemplateSystem(Map<String, Class<? extends TemplateCompiler>> templateCompilers, Injector injector) {
        this.templateCompilers = templateCompilers;
        this.injector = injector;
    }

    @Override
    public String[] getTemplateExtensions() {
        Set<String> keys = templateCompilers.keySet();
        if (keys.isEmpty()) {
            return new String[0];
        }
        String[] extensions = new String[keys.size()];
        int i = 0;
        for (String ext : keys) {
            extensions[i++] = "%s." + ext;
        }
        return extensions;
    }

    @Override
    public TemplateCompiler injectorInstance(String templateName) {
        String extension = templateName.substring(templateName.lastIndexOf(".") + 1);
        Class<? extends TemplateCompiler> type = templateCompilers.get(extension);
        if (type == null)
            type = templateCompilers.get(DEFAULT);
        return injector.getInstance(type);
    }
}
