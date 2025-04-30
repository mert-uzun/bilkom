package com.bilkom.enums;

/**
 * Enum for the relative paths of the avatars.
 * 
 * @author Mert Uzun
 * @version 1.0
 */
public enum AvatarRelativePaths {
    AVATAR_1("Media/avatars/avatar_1.png"),
    AVATAR_2("Media/avatars/avatar_2.png"),
    AVATAR_3("Media/avatars/avatar_3.png"),
    AVATAR_4("Media/avatars/avatar_4.png"),
    AVATAR_5("Media/avatars/avatar_5.png"),
    AVATAR_6("Media/avatars/avatar_6.png"),
    AVATAR_7("Media/avatars/avatar_7.png"),
    AVATAR_8("Media/avatars/avatar_8.png"),
    AVATAR_9("Media/avatars/avatar_9.png"),
    AVATAR_10("Media/avatars/avatar_10.png"),
    AVATAR_11("Media/avatars/avatar_11.png"),
    AVATAR_12("Media/avatars/avatar_12.png"),
    AVATAR_13("Media/avatars/avatar_13.png"),
    AVATAR_14("Media/avatars/avatar_14.png"),
    AVATAR_15("Media/avatars/avatar_15.png"),
    AVATAR_16("Media/avatars/avatar_16.png");


    private final String path;

    AvatarRelativePaths(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
