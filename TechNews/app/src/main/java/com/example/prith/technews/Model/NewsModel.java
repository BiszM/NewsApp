package com.example.prith.technews.Model;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.List;

/**
 * Created by prith on 2/21/2018.
 */

@Table(name = "news")
public class NewsModel extends Model {

    @Column(name = "title")
    public String title;

    @Column(name = "image")
    public String image;

    @Column(name = "description")
    public String description;

    @Column(name = "author")
    public String author;

    @Column(name = "type")
    public String type;

    @Column(name = "bookmarked")
    public boolean bookmarked;

    @Column(name = "save")
    public boolean save;

    @Column(name = "website")
    public String website;

    public NewsModel(){
        super();
    }

    public NewsModel(String author, String title, String description, String image,
                     String website, boolean save, boolean bookmarked) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.image = image;
        this.website = website;
        this.save = save;
        this.bookmarked = bookmarked;
    }

    public static void deleteNews(String type){
        new Delete().from(NewsModel.class).where("type = ?", type).execute();
    }

    public static List<NewsModel> getaData(String type){
        return new Select(new String[]{"title"}).from(NewsModel.class).where("type = ?", type).execute();
    }

    public static List<NewsModel> getNewsDetails(String type){
        return new Select().from(NewsModel.class).where("type = ?", type).execute();
    }

    public static List<NewsModel> getSavedNews(){
        Log.i("Model", "in model");
        return new Select().from(NewsModel.class).where("save = ?", true).execute();
    }

    public static List<NewsModel> getBookmarkedNews(){
        return new Select().from(NewsModel.class).where("bookmarked = ?", true).execute();
    }

    public static void setBookmark(String bookmark, int id){
        new Update(NewsModel.class).set("bookmark = ?", bookmark).where("id = ?", id).execute();
    }

    public static void setSave(String save, int id){
        new Update(NewsModel.class).set("save = ?", save).where("id = ?", id).execute();
    }

    @Override
    public String toString() {
        return "NewsModel{" +
                "title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", description='" + description + '\'' +
                ", author='" + author + '\'' +
                ", type='" + type + '\'' +
                ", bookmarked='" + bookmarked + '\'' +
                ", save='" + save + '\'' +
                ", website='" + website + '\'' +
                '}';
    }
}
