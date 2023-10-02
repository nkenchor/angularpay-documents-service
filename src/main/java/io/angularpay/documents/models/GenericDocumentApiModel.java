package io.angularpay.documents.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class GenericDocumentApiModel {

    @NotNull
    @JsonIgnore
    private MultipartFile file;

    @NotEmpty
    private String filename;

    @NotEmpty
    @JsonProperty("file_description")
    private String fileDescription;
}
