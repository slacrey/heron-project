package com.loadburn.heron.transport;

import com.google.inject.ImplementedBy;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-29
 */
@ImplementedBy(StandardTextTransport.class)
public abstract class Text implements Transport {
    public String contentType() {
        return "text/plain";
    }
}
