package com.lql.humanresourcedemo.util;

import java.util.List;

public class FileUtil {

    public static String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return "";
    }
    public static boolean supportAvatarExtension(String fileExtension) {
        return supportImageExtension.stream().anyMatch(s -> s.equalsIgnoreCase(fileExtension));
    }
    public static List<String> supportImageExtension = List.of("jpg", "jpeg", "png");

}
