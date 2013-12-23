package com.loadburn.heron.webapp.web;

import com.google.inject.ImplementedBy;

import java.util.List;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-11-4
 */
@ImplementedBy(HelloServiceImpl.class)
public interface HelloService {
    List<Person> say();

    void make();

    void createTable();
}
