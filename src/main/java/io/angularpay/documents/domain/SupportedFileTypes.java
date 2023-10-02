package io.angularpay.documents.domain;

public enum SupportedFileTypes {
    PDF("application/pdf"),
    DOC("application/msword"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    JPEG("image/jpeg"),
    PNG("image/png");

    private final String contentType;

    public String getContentType() {
        return contentType;
    }

    SupportedFileTypes(String contentType) {
        this.contentType = contentType;
    }
}
