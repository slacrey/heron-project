package com.loadburn.heron.webapp.web;

import com.google.inject.Inject;
import com.loadburn.heron.annotations.*;
import com.loadburn.heron.multipart.MultipartFile;
import com.loadburn.heron.render.Result;
import com.loadburn.heron.storage.annotations.Transactional;
import com.loadburn.heron.transport.Json;
import com.loadburn.heron.transport.MultiPartForm;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Decoration
@Show("DecoratedPage.html")
@Path("/decorated")
public class DecoratedPage extends DecoratorPage {

    @Inject
    private HelloService helloService;
    private String description;
    private List<Person> persons;

    @Override
    public String getWorld() {
        return "This comes from the subclass";
    }

    public String getDescription() {
        return description;
    }

    @Path("/index")
    @Get
    public  void decorated() {
//        helloService.createTable();
//        helloService.make();
        persons = helloService.say();
    }

    @Path("/make")
    @Get
    public  void decoratedMake() {
//        helloService.createTable();
        helloService.make();
//        persons = helloService.say();
    }

    @Path("/upload")
    @Post
    public void uploadFile(@As(MultiPartForm.class) Person person) {
        MultipartFile file = person.getUpload();
        String destDir = "D:\\test\\";
        File dir = new File(destDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File dist = new File(destDir+file.getName());
        try {
            file.transferTo(dist);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }
}
