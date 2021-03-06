package by.bsuir.service.email.impl;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class AbstractEmailContext {

    private String from;
    private String to;
    private String subject;
    private String templateLocation;
    private Map<String, Object> context;
}
