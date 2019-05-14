package br.com.ifitness.commons.enumeration;

import lombok.Getter;

@Getter
public enum S3BucketFolderEnumeration {
    BOT("bots/"),
    EXERCISE("exercise/"),
    EXERCISE_IMAGE("exercise/image/"),
    EXERCISE_VIDEO("exercise/video/"),
    USER("user/"),
    USER_PICTURE("user/picture/"),
    USER_IMAGES("user/images/");

    private String value;

    S3BucketFolderEnumeration(String value) {
        this.value = value;
    }
}
