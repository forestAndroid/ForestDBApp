package com.forest.sqlite;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.forest.sqlite.annotation.DBField;
import com.forest.sqlite.annotation.DBTable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BaseDao<T> implements IBaseDao<T> {
    private SQLiteDatabase database;

    private Class<T> entityClass;
    private String tableName;
    private boolean isInit = false;
    private HashMap<String, Field> cacheMap;

    protected synchronized boolean init(Class<T> entity, SQLiteDatabase sqLiteDatabase) {
        if (!isInit) {
            entityClass = entity;
            database = sqLiteDatabase;
            tableName = entity.getAnnotation(DBTable.class).value();
            if (!sqLiteDatabase.isOpen()) {
                return false;
            }
            if (!autoCreateTable()) {
                return false;
            }
            isInit = true;
        }
        initCacheMap();
        return isInit;
    }

    private void initCacheMap() {
        cacheMap = new HashMap<>();
        String sql = "select * from " + tableName + " limit 1,0";
        Cursor cursor = database.rawQuery(sql, null);
        String[] colmunNames = cursor.getColumnNames();
        Field[] colmunFields = entityClass.getDeclaredFields();
        for (String colmunName : colmunNames) {
            Field resultField = null;
            for (Field field : colmunFields) {
                if (colmunName.equals(field.getAnnotation(DBField.class).value())) {
                    resultField = field;
                    break;
                }
            }
            if (resultField != null) {
                cacheMap.put(colmunName, resultField);
            }
        }
        cursor.close();

    }

    private boolean autoCreateTable() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("CREATE TABLE IF NOT EXISTS ");
        stringBuffer.append(tableName + "(");
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            Class type = field.getType();
            if (type == String.class) {
                stringBuffer.append(field.getAnnotation(DBField.class).value() + " TEXT,");
            } else if (type == Double.class) {
                stringBuffer.append(field.getAnnotation(DBField.class).value() + " DOUBLE,");
            } else if (type == Integer.class) {
                stringBuffer.append(field.getAnnotation(DBField.class).value() + " INTEGER,");
            } else if (type == Long.class) {
                stringBuffer.append(field.getAnnotation(DBField.class).value() + " BIGINT,");
            } else if (type == byte[].class) {
                stringBuffer.append(field.getAnnotation(DBField.class).value() + " BLOB,");
            } else {
                continue;
            }
        }


        if (stringBuffer.charAt(stringBuffer.length() - 1) == ',') {
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        }
        stringBuffer.append(")");
        try {
            this.database.execSQL(stringBuffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        System.out.println("create table sql:" + stringBuffer.toString());
        return true;
    }

    @Override
    public Long insert(T entity) {

        ContentValues contentValues = getValues(entity);
        database.insert(tableName, null, contentValues);

        return null;
    }

    private ContentValues getValues(T entity) {
        ContentValues contentValues = new ContentValues();
        Iterator<Map.Entry<String, Field>> iterable = cacheMap.entrySet().iterator();
        while (iterable.hasNext()) {
            Map.Entry<String, Field> fieldEntry = iterable.next();

            Field field = fieldEntry.getValue();

            String key = fieldEntry.getKey();

            field.setAccessible(true);

            try {
                Object object = field.get(entity);

                Class type = field.getType();

                if (type == String.class) {
                    String value = (String) object;
                    contentValues.put(key, value);
                } else if (type == Double.class) {
                    Double value = (Double) object;
                    contentValues.put(key, value);
                } else if (type == Integer.class) {
                    Integer value = (Integer) object;
                    contentValues.put(key, value);
                } else if (type == Long.class) {
                    Long value = (Long) object;
                    contentValues.put(key, value);
                } else if (type == byte[].class) {
                    byte[] value = (byte[]) object;
                    contentValues.put(key, value);
                } else {
                    continue;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return contentValues;
    }
}
