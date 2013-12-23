package com.loadburn.heron.transport;

import com.google.inject.ImplementedBy;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-29
 */
@ImplementedBy(JacksonJsonTransport.class)
public abstract class Json implements Transport {
    public String contentType() {
        return "application/json";
    }
}
