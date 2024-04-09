package com.lql.humanresourcedemo.utility;

import java.util.List;

public class FileUtility {

    public static String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return "";
    }
    public static List<String> supportImageExtension = List.of("jpg", "jpeg", "png");

}
