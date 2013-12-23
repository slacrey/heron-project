package com.loadburn.heron.webapp.web;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.loadburn.heron.annotations.*;
import com.loadburn.heron.render.Model;
import com.loadburn.heron.render.Result;
import com.loadburn.heron.transport.Form;
import com.loadburn.heron.transport.Json;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import java.util.List;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-21
 */
@Path("/helloworld")
@Show("/helloworld")
public class HelloWorld {

    private String message = "hello world";
    private List<Person> persons = Lists.newArrayList();
    @Inject
    private HelloService helloService;

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Path("/test/:id")
    @Get
    public void testId(@PathVariable("id") String id, Model<String, Object> model) {
        System.out.print("id:"+id);
        model.put("id", id);
    }

    @Path("/hello")
    @Get
    @Show("/Hello.html")
    public void testHello(Model<String, Object> model) {
        model.put("test", "test");
        System.out.print("000000000000");

    }

    @Path("/test")
    @Post
    public void testPate(@As(Form.class) Person person) {



    }

    @Path("/json")
    @Get
    public Result<?> testResult(SimpleEmail email) {
        helloService.createTable();
        helloService.make();
        List<Person> person = helloService.say();

        try {
            email.setSubject("TestMail");
            email.setMsg("test");
            email.addTo("283815609@qq.com");
            email.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }
        return Result.with(person).as(Json.class);
    }

}
