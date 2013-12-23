package com.loadburn.heron.webapp.web;

import com.loadburn.heron.multipart.MultipartFile;
import com.loadburn.heron.storage.annotations.*;
import com.loadburn.heron.storage.config.GeneratorStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "person")
@Modifies({
        @Modify(name = "create_person", modify = "create table person (id bigint(20) not null, name VARCHAR(30) null)"),
        @Modify(name = "insert_person", modify = "insert into person (id, name) values (1, @name)")
})
@Queries({
        @Query(name = "select_person", query = "select * from person")
})
@Cache(region="")
public class Person implements Serializable {

    @Id(strategy = GeneratorStrategy.StorageId)
    private Long id;
    @NotNull(message = "constraintViolationNullFirstName")
    @Size(min = 1, message = "constraintViolationLengthFirstName")
    private String name;
    @Transient
    private MultipartFile upload;

    public Person() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MultipartFile getUpload() {
        return upload;
    }

    public void setUpload(MultipartFile upload) {
        this.upload = upload;
    }
}
