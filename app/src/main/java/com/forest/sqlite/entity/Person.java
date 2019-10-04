package com.forest.sqlite.entity;


import com.forest.sqlite.annotation.DBField;
import com.forest.sqlite.annotation.DBTable;

@DBTable("person")
public class Person {
    @DBField("tb_name")
    public String name;
    @DBField("tb_password")
    public String password;
}
