package com.shencangblue.jin.sbms.Utils;

import android.content.ContentValues;
import android.util.Log;

import com.shencangblue.jin.sbms.Model.Book;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.util.List;

public class SqlDeal {
    private static final String TAG = "SqlDeal";

    public SqlDeal(){
        Connector.getDatabase();
    }
    public boolean addData(String author,String name,int pages,double price){

        Book book = new Book();
        book.setAuthor(author);
        book.setName(name);
        book.setPages(pages);
        book.setPrice(price);
        return book.save();

    }
    public Book findBookName(String name){
        Log.e(TAG,name);
        Connector.getDatabase();
        List<Book> books = LitePal.where("name=?", name).find(Book.class);
        if (books.size()!=1){
           return new Book();
        }else {
            return books.get(0);
        }

    }
    public boolean boofindBookName(String name){
        Log.e(TAG,name);
        Connector.getDatabase();
        List<Book> books = LitePal.where("name=?", name).find(Book.class);
        if (books.size()!=1){
            return false;
        }else {
            return true;
        }

    }


    public List<Book> findAll(){
        return LitePal.findAll(Book.class);
    }

    public boolean upDate(Long id,String author,String name,int pages,double price){
        //Book book = new Book();
        ContentValues contentValues = new ContentValues();
        contentValues.put("author",author);
        contentValues.put("name",name);
        contentValues.put("pages",pages);
        contentValues.put("price", price);
        LitePal.update(Book.class,contentValues,id);
        return true;
    }
    public Book findBookId(int id){
        Book book = LitePal.find(Book.class,id);
        return book;
    }
    public void delete(int id){
        LitePal.delete(Book.class,id);

    }
}
