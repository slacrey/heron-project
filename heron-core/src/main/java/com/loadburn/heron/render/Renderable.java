package com.loadburn.heron.render;

import com.loadburn.heron.bind.Response;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-15
 */
public interface Renderable {

    void render(Object bound, Response response);

}
