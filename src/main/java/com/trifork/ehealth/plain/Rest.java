package com.trifork.ehealth.plain;

import ca.uhn.fhir.context.FhirContext;
import com.trifork.ehealth.fhir.dstu3.CodeSystemResourceProvider;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Rest {

    private final CodeSystemResourceProvider dstu3CodeSystemResourceProvider;

    public Rest(CodeSystemResourceProvider dstu3CodeSystemResourceProvider)
    {
        this.dstu3CodeSystemResourceProvider = dstu3CodeSystemResourceProvider;
    }

    @PostMapping("/translateDSTU3")
    String translateDSTU3(@RequestBody String codeSystem, @RequestParam String language) {

        var context= FhirContext.forDstu3();
        var codesystem = context.newXmlParser().parseResource(org.hl7.fhir.dstu3.model.CodeSystem.class, codeSystem);
        return context.newXmlParser().encodeResourceToString(this.dstu3CodeSystemResourceProvider.translate(codesystem, language));
    }

    @PostMapping("/translateR4")
    String translateR4(@RequestBody String codeSystem) {
        var context= FhirContext.forR4();
        var codesystem = FhirContext.forR4().newXmlParser().parseResource(org.hl7.fhir.r4.model.CodeSystem.class, codeSystem);
        //return context.newXmlParser().encodeResourceToString(this.r4CodeSystemResourceProvider.translate(codesystem, language));
        return null;
    }
}
