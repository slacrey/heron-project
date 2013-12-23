package com.loadburn.heron.complier;

import com.loadburn.heron.render.Renderable;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-27
 */
public interface TemplateCompiler {

    Renderable compile(TemplateLoader.TemplatePage templatePage);

}
