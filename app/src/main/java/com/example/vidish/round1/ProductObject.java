package com.example.vidish.round1;


/**
 * Created by Vidish on 28-01-2017.
 */
public class ProductObject {
    String name,image,folder;

    public ProductObject(String name,String image, String folder)
    {
        this.name = name;
        this.image = image;
        this.folder = folder;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getFolder(){return folder;}
}
