package com.forest.sqlite;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class BaseDaoFactory {
    private String sqliteDatabasePath;
    private SQLiteDatabase sqLiteDatabase;
    private static final BaseDaoFactory ourInstance = new BaseDaoFactory();

    public static BaseDaoFactory getInstance() {
        return ourInstance;
    }

    private BaseDaoFactory() {
        sqliteDatabasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/forest.db";
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqliteDatabasePath, null);
    }


    public synchronized <T> BaseDao<T> getBaseDao(Class<T> entityClass) {
        BaseDao<T> baseDao = null;
        try {
            baseDao = BaseDao.class.newInstance();
            baseDao.init(entityClass, sqLiteDatabase);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return baseDao;
    }
}
