package com.trifork.ehealth.translation.fhir.dstu3;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.util.BundleUtil;
import com.trifork.ehealth.translation.ApplicationProps;
import com.trifork.ehealth.translation.google.TranslatorStu3;
import com.trifork.ehealth.translation.fhir.annotations.OnDSTU3Condition;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CodeSystem;
import org.hl7.fhir.dstu3.model.ValueSet;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@Conditional(OnDSTU3Condition.class)
public class CodeSystemResourceProvider implements IResourceProvider {
    private static final Logger logger = LoggerFactory.getLogger(CodeSystemResourceProvider.class);
    private final TranslatorStu3 translatorStu3;

    public CodeSystemResourceProvider(TranslatorStu3 translatorStu3, ApplicationProps applicationProps) {
        this.translatorStu3 = translatorStu3;

        var context = FhirContext.forDstu3();
        var client = context.newRestfulGenericClient("http://tx.fhir.org/r3");
        for (var a : applicationProps.getValuesets()) {
            var result = client.search().byUrl("ValueSet?url=" + a).returnBundle(Bundle.class).execute();
            var valueSet = BundleUtil.toListOfResourcesOfType(context, result, ValueSet.class).get(0);

            logger.info("Looking into {}", valueSet.getUrl());
            var uniqueCodesystemStrings = valueSet.getCompose().getInclude().stream().map(ValueSet.ConceptSetComponent::getSystem).collect(Collectors.toSet());

            logger.info("Looking up CodeSystems: {}", String.join(", ", uniqueCodesystemStrings));

            for (String s : uniqueCodesystemStrings) {
                var cs = client.search().byUrl("CodeSystem?url=" + s).returnBundle(Bundle.class).execute();
                var resources = BundleUtil.toListOfResourcesOfType(context, cs, CodeSystem.class);
                if (!resources.isEmpty()) {
                    var css = resources.get(0);
                    logger.info("Nr of CodeSystem concepts: {}", css.getConcept().size());
                } else
                    logger.info("Could not lookup Codesystem");
            }
        }
    }

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return CodeSystem.class;
    }

    @Operation(name = "translate", returnParameters = @OperationParam(name = "return", type = CodeSystem.class))
    public CodeSystem translate(@OperationParam(name = "codeSystem") CodeSystem codeSystem, @OperationParam(name = "language") String language) {
        return translatorStu3.addDesignation(codeSystem, language);
    }
}
