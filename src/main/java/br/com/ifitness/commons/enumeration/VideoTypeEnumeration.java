package br.com.ifitness.commons.enumeration;

import lombok.Getter;

@Getter
public enum VideoTypeEnumeration {
    MP4("mp4", "video/mp4");

    private String value;
    private String contentType;

    VideoTypeEnumeration(String value, String contentType) {
        this.value = value;
        this.contentType = contentType;
    }

    public static VideoTypeEnumeration contains(String contentType) {
        for (VideoTypeEnumeration ie: values()) {
            if (ie.getContentType().equals(contentType)) {
                return ie;
            }
        }

        return null;
    }
}
