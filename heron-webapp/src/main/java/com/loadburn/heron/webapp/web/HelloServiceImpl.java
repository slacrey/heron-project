package com.loadburn.heron.webapp.web;

import com.google.common.collect.Lists;
import com.google.inject.Provider;
import com.loadburn.heron.storage.EntityStorage;
import com.loadburn.heron.storage.annotations.Transactional;
import com.loadburn.heron.storage.convertion.handler.BeanListHandler;
import com.loadburn.heron.storage.sql.Query;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-11-4
 */
public class HelloServiceImpl implements HelloService {

    private final Provider<EntityStorage> es;

    @Inject
    public HelloServiceImpl(Provider<EntityStorage> es) {
        this.es = es;
    }

    @Override
    @Transactional
    public List<Person> say() {
        List<Person> persons = null;
        try {
            persons = es.get().find(Person.class, new BeanListHandler<Person>(Person.class));
//            persons = sql.get().with(Person.class, "select_person").as(new BeanListHandler<Person>(Person.class)).params(null).list();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (persons != null && !persons.isEmpty()) {
            return persons;
        }
        return Lists.newArrayList();
    }

    @Transactional
    public void createTable() {
        try {
            Query query = es.get().createNamedQuery(Person.class, "create_person");
            if (!query.tableExists("person")) {
                query.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void make() {
        Person person = new Person();
        person.setName("lin12");
        try {
            es.get().save(Person.class, person);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
