package com.trifork.ehealth.translation;

import com.trifork.ehealth.translation.fhir.FhirServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CodesystemTranslatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodesystemTranslatorApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean<FhirServlet> hapiServletRegistration(FhirServlet fhirServlet) {
        ServletRegistrationBean<FhirServlet> servletRegistrationBean = new ServletRegistrationBean<>();
        servletRegistrationBean.setServlet(fhirServlet);
        servletRegistrationBean.addUrlMappings("/fhir/*");
        servletRegistrationBean.setLoadOnStartup(1);

        return servletRegistrationBean;
    }


}
