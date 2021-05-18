package com.abuunity.shareon.Model;

import com.abuunity.shareon.R;

import java.util.ArrayList;
import java.util.List;

public class Tools {
        private int id;
        private String tools;

    public Tools() {
    }

    public Tools(int id, String tools) {
        this.id = id;
        this.tools = tools;
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

    public static List<Tools> getObjectList() {

        List<Tools> dataList = new ArrayList<>();
        int[] intId = getIntId();
        String[] stringTexts = getTexs();

        for (int i = 0; i < intId.length; i++) {
            Tools tools = new Tools();
            tools.setId(intId[i]);
            tools.setTools(stringTexts[i]);
            dataList.add(tools);
        }
        return dataList;
    }

    private static int[] getIntId() {

        int[] intId = {
                1, 2
        };
        return intId;
    }

    private static String[] getTexs() {

        String[] texList = {
                "2 buah pisang","1/3 gram susu bubuk"
        };
        return texList;
    }
}
