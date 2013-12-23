package com.loadburn.heron.webapp.web;

import com.loadburn.heron.annotations.Decoration;
import com.loadburn.heron.annotations.Path;
import com.loadburn.heron.annotations.Show;

import java.util.Arrays;
import java.util.List;

/**
 * author: Martins Barinskis (martins.barinskis@gmail.com)
 */
@Decoration
@Show("DecoratedRepeat.html")
@Path("/decorated/repeat")
public class DecoratedRepeat extends DecoratorPage {

    private static final List<String> ITEMS = Arrays.asList("one", "two", "three");

    private final String messagePrefix = "Hello, ";

    public String getMessagePrefix() {
        return messagePrefix;
    }

    public List<String> getItems() {
        return ITEMS;
    }

    @Override
    public String getWorld() {
        return "This is a decorated page with a repeat block";
    }

}