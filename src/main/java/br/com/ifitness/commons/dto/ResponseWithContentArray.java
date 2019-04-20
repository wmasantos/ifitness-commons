package br.com.ifitness.commons.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseWithContentArray<T> implements Serializable {
    private static final long serialVersionUID = -6530646999065856843L;
    private int code;
    private int httpCode;
    private String message;
    private List<T> content;
    private List<String> error;
}
