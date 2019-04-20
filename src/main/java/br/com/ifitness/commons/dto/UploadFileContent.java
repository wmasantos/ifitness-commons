package br.com.ifitness.commons.dto;

import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UploadFileContent<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private byte [] bytes;
    private T enumType;
}
