package com.loadburn.heron.complier;

import com.google.inject.ImplementedBy;
import com.loadburn.heron.render.Renderable;
import com.loadburn.heron.route.HeronPage;

import java.util.concurrent.ConcurrentMap;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-28
 */
@ImplementedBy(StandardCompilers.class)
public interface Compilers {

    void compilePage(HeronPage.Page page);

    Renderable compile(Class<?> templateClass);

}
