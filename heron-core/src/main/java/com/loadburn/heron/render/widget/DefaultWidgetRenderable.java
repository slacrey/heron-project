package com.loadburn.heron.render.widget;

import com.google.common.collect.Lists;
import com.loadburn.heron.bind.Response;
import com.loadburn.heron.render.Renderable;
import net.jcip.annotations.ThreadSafe;

import java.util.ArrayList;
import java.util.List;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-5
 */
@ThreadSafe
public class DefaultWidgetRenderable implements WidgetRenderable {

    private final List<Renderable> widgets = Lists.newArrayList();

    @Override
    public synchronized WidgetRenderable addWidget(Renderable renderable) {
        widgets.add(renderable);
        return this;
    }

    @Override
    public void render(Object bound, Response response) {
        for (Renderable widget : widgets) {
            widget.render(bound, response);
        }
    }

}
