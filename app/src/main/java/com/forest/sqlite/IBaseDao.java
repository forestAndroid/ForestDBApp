package com.forest.sqlite;

public interface IBaseDao<T> {
    Long insert(T entity);
}
