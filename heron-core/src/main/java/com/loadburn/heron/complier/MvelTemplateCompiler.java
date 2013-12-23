package com.loadburn.heron.complier;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.loadburn.heron.bind.Request;
import com.loadburn.heron.bind.Response;
import com.loadburn.heron.render.Renderable;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateRuntime;

import java.util.HashMap;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-29
 */
public class MvelTemplateCompiler implements TemplateCompiler {

    private final Provider<Request<String>> requestProvider;

    @Inject
    public MvelTemplateCompiler(Provider<Request<String>> requestProvider) {
        this.requestProvider = requestProvider;
    }

    /**
     * 返回Renderable实现(非装饰页面)
     *
     * @param template 模板
     * @return Renderable实现实例
     */
    private Renderable getNormalRenderable(Template template) {
        final CompiledTemplate compiledTemplate = org.mvel2.templates.TemplateCompiler.compileTemplate(template.getText());

        return new Renderable() {
            @Override
            public void render(Object bound, Response response) {
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("contextPath", requestProvider.get().context());
                hashMap.put("completePath", requestProvider.get().completePath());
                response.write(TemplateRuntime.execute(compiledTemplate, bound, hashMap).toString());
            }
        };
    }

    @Override
    public Renderable compile(TemplateLoader.TemplatePage templatePage) {

        if (templatePage.isDecorator()) {

            final CompiledTemplate compiledTemplate = org.mvel2.templates.TemplateCompiler.compileTemplate(templatePage.getTemplate().getText());
            final CompiledTemplate decorateCompiledTemplate = org.mvel2.templates.TemplateCompiler.compileTemplate(templatePage.getDecorateTemplate().getText());

            return new Renderable() {
                @Override
                public void render(Object bound, Response response) {
                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
                    hashMap.put("contextPath", requestProvider.get().context());
                    hashMap.put("completePath", requestProvider.get().completePath());

                    String text = TemplateRuntime.execute(compiledTemplate, bound, hashMap).toString();
                    HtmlParser htmlParser = new HtmlParser(text);

                    response.write(TemplateRuntime.execute(decorateCompiledTemplate, bound, hashMap).toString());
                    response.writeToBody(htmlParser.body());
                    response.writeToHead(htmlParser.head());

                }
            };
        } else {
            return getNormalRenderable(templatePage.getTemplate());
        }

    }
}
