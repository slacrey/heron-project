package com.loadburn.heron.complier;

import com.google.inject.ImplementedBy;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-27
 */
@ImplementedBy(StandardTemplateSystem.class)
public interface TemplateSystem {
    /**
     * 从模板编译对象map中获取key
     * 获取定义的模板编译类的key（相当于模板的后缀）
     * @return key的数组 其中格式为["%s.html","%s.jsp"]
     */
    String[] getTemplateExtensions();

    /**
     * 获取模板的编译类
     * @param templateName 模板名称
     * @return 编译类实例 {@link TemplateCompiler}
     */
    TemplateCompiler injectorInstance(String templateName);
}
