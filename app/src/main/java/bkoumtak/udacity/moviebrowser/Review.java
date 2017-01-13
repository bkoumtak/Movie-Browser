package bkoumtak.udacity.moviebrowser;

import java.io.Serializable;

/**
 * Created by kondeelai on 2016-12-18.
 */
public class Review implements Serializable{
    String id;
    String author;
    String content;

    public Review(String id, String author, String content){
        this.id = id;
        this.author = author;
        this.content = content;
    }


}
