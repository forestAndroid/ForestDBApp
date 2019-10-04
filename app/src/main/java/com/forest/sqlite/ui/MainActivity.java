package com.forest.sqlite.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.forest.sqlite.BaseDaoFactory;
import com.forest.sqlite.IBaseDao;
import com.forest.sqlite.R;
import com.forest.sqlite.entity.Person;

public class MainActivity extends AppCompatActivity {
    IBaseDao<Person> baseDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        baseDao = BaseDaoFactory.getInstance().getBaseDao(Person.class);
    }
    public void insert(View view) {
        Person person = new Person();
        person.name = "建国";
        person.password = "4464546556";
        baseDao.insert(person);
    }
}
