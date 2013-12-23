package com.loadburn.heron.render.widget;

import com.loadburn.heron.render.Renderable;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-5
 */
public interface WidgetRenderable extends Renderable {

    WidgetRenderable addWidget(Renderable renderable);

}
