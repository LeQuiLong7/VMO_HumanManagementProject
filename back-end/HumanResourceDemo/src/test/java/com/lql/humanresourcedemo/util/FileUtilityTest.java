package com.lql.humanresourcedemo.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilityTest {

    @ParameterizedTest(name = "File extension of {0} is {1}")
    @CsvSource({
            "abc.jpg, jpg",
            "abc-adf.xlsx, xlsx",
            "1.abc, abc",
            ".git, git",

    })
    void getFileExtensionTest(String filename, String fileExtension) {
        assertEquals(fileExtension, FileUtil.getFileExtension(filename));
    }

    @ParameterizedTest(name = "{0} extension is not supported for avatar")
    @ValueSource(strings = {"svg", "gif", "docs"})
    void notSupportedAvatarExtensionTest(String fileExtension) {
        assertFalse(FileUtil.supportAvatarExtension(fileExtension));
    }

    @ParameterizedTest(name = "{0} extension is supported for avatar")
    @ValueSource(strings = {"jpg", "png", "jpeg"})
    void supportedAvatarExtensionTest(String fileExtension) {
        assertTrue(FileUtil.supportAvatarExtension(fileExtension));
    }
}