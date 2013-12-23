package com.loadburn.heron.webapp.web;

import com.loadburn.heron.annotations.Show;

@Show("/Decorator.html")
public abstract class DecoratorPage {
    public String getHello() {
        return "Hello (from the superclass)";
    }

    public abstract String getWorld();
}