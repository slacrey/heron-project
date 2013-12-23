package com.loadburn.heron.transport;

import com.google.inject.ImplementedBy;

@ImplementedBy(UrlEncodedFormTransport.class)
public abstract class Form implements Transport {

    public String contentType() {
        return "application/x-www-form-urlencoded";
    }

}
