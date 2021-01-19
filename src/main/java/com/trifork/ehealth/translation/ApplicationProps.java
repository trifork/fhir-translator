package com.trifork.ehealth.translation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "application")
public class ApplicationProps {

    private List<String> valuesets;

    public List<String> getValuesets() {
        return valuesets;
    }

    public void setValuesets(List<String> valuesets) {
        this.valuesets = valuesets;
    }


}
