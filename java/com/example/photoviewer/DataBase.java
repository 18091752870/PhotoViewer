package com.example.photoviewer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class DataBase {

    private static SQLiteDatabase DB;
    public String filePath = "/data/data/com.example.photoviewer/files";//图片存储路径

    DataBase(){
        File fileDir=new File(filePath);
        if(!fileDir.exists())//如果文件夹不存在
            fileDir.mkdir();//创建文件夹
        DB = SQLiteDatabase.openOrCreateDatabase(filePath + "/info.db", null);
        init();
    }

    public static void init(){//初始化，如果没有user表，创建之
        try{
            String testSql="select * from user";
            DB.rawQuery(testSql, null);
        }catch(Exception e){
            create();
        }
    }
    /**
     * 建表
     */
    public static void create() {
        String createSql = "create table user(_id integer primary key autoincrement,title varchar(100),author varchar(100))";
        DB.execSQL(createSql);
    }

    /**
     * 插入
     */
    public static Cursor insert(String title, String author) {//插入一项记录，返回索引id
        String insertSql = "insert into user(title,author) values(?,?)";
        DB.execSQL(insertSql, new String[] {title, author});

        String getIdSql= "select _id from user where title=?";
        Cursor cursor=DB.rawQuery(getIdSql,new String[]{title});
        return cursor;
    }

    /**
     * 查询
     */
    public static Cursor selectAll() {
        String selectSql = "select _id,title,author from user";
        Cursor cursor = DB.rawQuery(selectSql, null);
        return cursor;
    }
    /**
     * 删除
     */
    public static void delete(String id) {
        String deleteSql = "delete from user where _id=?";
        DB.execSQL(deleteSql, new String[] { id });
    }

    public static void close(){
        DB.close();
    }
    /**
     * 更新
     */

    public void update(String title, String author, String id) {
        String updataSql = "update user set title=?,author=? where _id=?";
        DB.execSQL(updataSql, new String[] { title, author, id });
    }

    public Cursor selectById(String id) {
        String selectSql = "select title,author from user where _id=?";
        Cursor cursor = DB.rawQuery(selectSql,new String[]{id});
        return cursor;
    }
}
