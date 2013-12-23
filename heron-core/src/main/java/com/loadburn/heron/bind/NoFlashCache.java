package com.loadburn.heron.bind;

import com.google.inject.Singleton;

@Singleton
public class NoFlashCache implements FlashCache {
    @Override
    public <T> T get(String key) {
        return null;
    }

    @Override
    public <T> T remove(String key) {
        return null;
    }

    @Override
    public <T> void put(String key, T t) {
    }
}
