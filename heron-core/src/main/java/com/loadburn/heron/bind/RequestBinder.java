package com.loadburn.heron.bind;

public interface RequestBinder<P> {
    String COLLECTION_BIND_PREFIX = "[C/";

    void bind(Request<P> request, Object o);
}
