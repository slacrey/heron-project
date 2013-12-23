package com.loadburn.heron.complier;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.loadburn.heron.annotations.Decoration;
import com.loadburn.heron.annotations.Show;
import com.loadburn.heron.enums.CharsetEnum;
import com.loadburn.heron.excetions.NoSuchTemplateException;
import com.loadburn.heron.excetions.TemplateLoaderException;
import com.loadburn.heron.render.Renderable;
import com.loadburn.heron.route.HeronPage;
import net.jcip.annotations.Immutable;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.Arrays;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-27
 */
@Immutable
public final class TemplateLoader {

    private final Provider<ServletContext> context;
    private final TemplateSystem templateSystem;

    @Inject
    public TemplateLoader(Provider<ServletContext> context, TemplateSystem templateSystem) {
        this.context = context;
        this.templateSystem = templateSystem;
    }

    /**
     * 编译页面模板
     * @param page 页面对象
     * @return  页面渲染对象 {@link com.loadburn.heron.render.Renderable}
     */
    public Renderable compile(HeronPage.Page page) {

        Show methodShow = page.getShow();

        Template template = loadTemplate(page.pageClass(), methodShow);
        TemplateCompiler templateCompiler = templateSystem.injectorInstance(template.getName());

        if (templateCompiler == null) {
            templateCompiler = templateSystem.injectorInstance("html");
        }
        TemplatePage templatePage = new TemplatePage(page.pageClass(), template);

        return templateCompiler.compile(templatePage);
    }

    /**
     * 编译模板
     * @param templateClass 模板页面类
     * @return 页面渲染对象 {@link com.loadburn.heron.render.Renderable}
     */
    public Renderable compile(Class<?> templateClass) {
        Template template = loadTemplate(templateClass, null);
        TemplateCompiler templateCompiler = templateSystem.injectorInstance(template.getName());

        if (templateCompiler == null) {
            templateCompiler = templateSystem.injectorInstance("html");
        }
        TemplatePage templatePage = new TemplatePage(templateClass, template);

        return templateCompiler.compile(templatePage);
    }

    /**
     * 加载模板
     * @param pageClass 页面类
     * @param methodShow 方法注解对象
     * @return 页面模板 {@link Template}
     */
    protected Template loadTemplate(Class<?> pageClass, Show methodShow) {

        String template = null;
        String extension = null;

        Show show = pageClass.getAnnotation(Show.class);
        if (null != show) {
            template = show.value();
        }

        //判断在方法上是否存在@Show注解
        if (methodShow != null) {
            if (template != null) {
                template = template + methodShow.value();
            } else {
                template = methodShow.value();
            }
        }

        if (template == null || template.length() == 0) {
            template = pageClass.getSimpleName();
        } else {
            // 判断模板类型是否已在系统中预定义
            for (String ext : templateSystem.getTemplateExtensions()) {
                String type = ext.replace("%s.", ".");
                if (template.endsWith(type)) {
                    extension = type;
                    break;
                }
            }
        }
        if (null == template) {
            String message = String.format("无法找到对应模板 %s", Show.class);
            NoSuchTemplateException exception = new NoSuchTemplateException(message);
            throw exception;
        }

        TemplateSource templateSource = null;
        String text;

        boolean appendExtension = false;
        try {
            final ServletContext servletContext = context.get();
            InputStream stream = null;

            // 模板中存在后缀名
            if (template.contains(".") || null != extension) {
                // 在类资源文件路径下查找模板
                stream = pageClass.getResourceAsStream(template);

                // 在web上下文中查找模板
                if (null == stream) {
                    stream = open(template, servletContext);
                }

                // 在web-inf下面查找
                if (null == stream) {
                    stream = openWebInf(template, servletContext);
                }

                // 在servlet context下面查找
                if (null == stream) {
                    stream = servletContext.getResourceAsStream(template);
                }
            }


            if (null == stream) {
                appendExtension = true;
                for (String ext : templateSystem.getTemplateExtensions()) {
                    String name = String.format(ext, template);
                    stream = pageClass.getResourceAsStream(name);
                    if (null != stream) {
                        extension = ext;
                        break;
                    }
                }
            }

            if (null == stream) {
                for (String ext : templateSystem.getTemplateExtensions()) {
                    String name = String.format(ext, pageClass.getSimpleName());
                    stream = open(name, servletContext);
                    if (null != stream) {
                        extension = ext;
                        break;
                    }
                }
            }

            if (null == stream) {
                for (String ext : templateSystem.getTemplateExtensions()) {
                    String name = String.format(ext, pageClass.getSimpleName());
                    //WEB-INF
                    stream = openWebInf(name, servletContext);
                    if (null != stream) {
                        extension = ext;
                        break;
                    }
                }
            }
            if (null == stream) {
                for (String ext : templateSystem.getTemplateExtensions()) {
                    String name = String.format(ext, template);
                    //ServletContext 中打开
                    stream = servletContext.getResourceAsStream(name);
                    if (null != stream) {
                        extension = ext;
                        break;
                    }
                }
            }

            if (null == stream) {
                String message = String.format(
                        "在包根目录或者WEB-INF/中均不存在[%s]的后缀为" + Arrays.toString(templateSystem.getTemplateExtensions()).replace("%s.", ".")
                                + "的模板,请查阅[%s]类中方法或是类上是否有@Show? 或是相应目录是否存在模板文件.", pageClass.getSimpleName(), pageClass.getName(), pageClass.getPackage().getName());
                NoSuchTemplateException exception = new NoSuchTemplateException(message);
                throw exception;
            }

            text = read(stream);

        } catch (IOException e) {
            String message = "不能加载模板 (i/o error): " + pageClass;
            TemplateLoaderException exception = new TemplateLoaderException(message, e);
            throw exception;
        }

        if (appendExtension) {
            template += "." + extension;
        }

        return new Template(template, text, templateSource);

    }

    /**
     * 打开文件
     * @param templateName 文件名
     * @param context web上下文对象
     * @return 输入流
     */
    private static InputStream open(String templateName, ServletContext context) {
        try {
            String path = context.getRealPath(templateName);
            return path == null ? null : new FileInputStream(path);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    /**
     * 在web-inf下面打开
     * @param templateName 模板名称
     * @param context web上下文对象
     * @return 输入流{@link InputStream}
     */
    private static InputStream openWebInf(String templateName, ServletContext context) {
        return open("/WEB-INF/" + templateName, context);
    }

    /**
     * 从输入流中读取模板文件内容
     * @param stream 输入流 {@link java.io.InputStream}
     * @return 模板内容
     * @throws java.io.IOException
     */
    private static String read(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, CharsetEnum.UTF8.getText()));
        StringBuilder builder = new StringBuilder();
        try {
            while (reader.ready()) {
                builder.append(reader.readLine());
                builder.append("\n");
            }
        } finally {
            stream.close();
        }
        return builder.toString();
    }

    public class TemplatePage {
        private final Class<?> templateClazz;
        private final Template template;
        private Class<?> decorateTemplateClazz;
        private Template decorateTemplate;
        private boolean decorator = false;

        private Class<?> decorateClass(final Class<?> extendClassArgument) {
            Class<?> extendClass = extendClassArgument;
            while (extendClass != Object.class) {
                extendClass = extendClass.getSuperclass();
                if (extendClass.isAnnotationPresent(Decoration.class)) {
                    decorateClass(extendClass);
                } else if (extendClass.isAnnotationPresent(Show.class)) {
                    return extendClass;
                }
            }
            throw new IllegalStateException("Could not find super class annotated with @Show on parent of class: " + extendClassArgument);
        }

        private void initDecorateHandle(Class<?> templateClass) {
            if (templateClass.getAnnotation(Decoration.class) != null) {
                this.decorateTemplateClazz = decorateClass(templateClass);
                this.decorateTemplate = loadTemplate(this.decorateTemplateClazz, null);
                this.decorator = true;
            }
        }

        public TemplatePage(Class<?> templateClazz, Template template) {
            this.templateClazz = templateClazz;
            this.template = template;
            this.initDecorateHandle(templateClazz);
        }

        public Class<?> getTemplateClazz() {
            return templateClazz;
        }

        public Template getTemplate() {
            return template;
        }

        public Class<?> getDecorateTemplateClazz() {
            return decorateTemplateClazz;
        }

        public Template getDecorateTemplate() {
            return decorateTemplate;
        }

        public boolean isDecorator() {
            return decorator;
        }
    }

}
