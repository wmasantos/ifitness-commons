package br.com.ifitness.commons.enumeration;

import lombok.Getter;

@Getter
public enum ImageTypeEnumeration {
    JPG("jpg", "image/jpg"),
    JPEG("jpeg", "image/jpeg"),
    PNG("png", "image/png");

    private String value;
    private String contentType;

    ImageTypeEnumeration(String value, String contentType) {
        this.value = value;
        this.contentType = contentType;
    }

    public static ImageTypeEnumeration contains(String contentType) {
        for (ImageTypeEnumeration ie: values()) {
            if (ie.getContentType().equals(contentType)) {
                return ie;
            }
        }

        return null;
    }
}
