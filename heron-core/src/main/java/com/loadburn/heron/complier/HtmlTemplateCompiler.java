package com.loadburn.heron.complier;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.loadburn.heron.bind.Request;
import com.loadburn.heron.bind.Response;
import com.loadburn.heron.render.Renderable;
import com.loadburn.heron.route.HeronPage;
import com.loadburn.heron.utils.HeronUtils;
import org.jsoup.nodes.Element;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateRuntime;

import java.util.HashMap;
import java.util.Stack;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-29
 */
public class HtmlTemplateCompiler implements TemplateCompiler {

    private final HeronPage wildfire;
    private final Provider<Request<String>> requestProvider;
    private final Templates templates;

    @Inject
    public HtmlTemplateCompiler(HeronPage wildfire, Provider<Request<String>> requestProvider, Templates templates) {
        this.wildfire = wildfire;
        this.requestProvider = requestProvider;
        this.templates = templates;
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
