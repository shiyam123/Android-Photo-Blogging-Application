package com.example.photoblog;

import java.util.Date;
import java.sql.Timestamp;

public class BlogPost extends BlogPostId{
    public String user_id,image,description,thumb;
    public Date timestamp;

    public BlogPost(Date timestamp) {
        this.timestamp = timestamp;
    }

    public BlogPost()
    {

    }


    public BlogPost(String user_id, String image_url, String desc,String thumb,Date timestamp) {
        this.user_id = user_id;
        this.image = image_url;
        this.description = desc;
        this.thumb=thumb;
        this.timestamp=timestamp;

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image;
    }

    public void setImage_url(String image_url) {
        this.image = image_url;
    }



    public String getDesc() {
        return description;
    }

    public void setDesc(String desc) {
        this.description = desc;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
