package com.loadburn.heron.transport;

import com.google.inject.ImplementedBy;

@ImplementedBy(MultiPartFormTransport.class)
public abstract class MultiPartForm implements Transport {

    public String contentType() {
        return "multipart/form-data";
    }

}
