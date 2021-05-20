package com.abuunity.shareon.Model;

import android.widget.ImageView;

import com.abuunity.shareon.R;

import java.util.ArrayList;
import java.util.List;

public class Steps {
        private int id;
        private String tools;
        private int imageId;

    public Steps() {
    }

    public Steps(int id, String tools, int imageId) {
        this.id = id;
        this.tools = tools;
        this.imageId = imageId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTools() {
        return tools;
    }

    public void setTools(String tools) {
        this.tools = tools;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public static List<Steps> stepsList() {

        List<Steps> dataList = new ArrayList<>();
        int[] intId = getIntId();
        int[] intImage = getIntImage();
        String[] stringTexts = getTexs();

        for (int i = 0; i < intId.length; i++) {
            Steps tools = new Steps();
            tools.setId(intId[i]);
            tools.setTools(stringTexts[i]);
            tools.setImageId(intImage[i]);
            dataList.add(tools);
        }
        return dataList;
    }

    private static int[] getIntId() {

        int[] intId = {
                0, 1
        };
        return intId;
    }

    private static String[] getTexs() {

        String[] texList = {
                "2 buah pisang","1/3 gram susu bubuk"
        };
        return texList;
    }

    private static int[] getIntImage() {

        int[] intImage = {
                R.drawable.ic_photo, R.drawable.ic_photo,
        };
        return intImage;
    }

}
