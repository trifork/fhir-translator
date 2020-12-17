package com.trifork.ehealth.fhir.dstu3;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.util.BundleUtil;
import com.trifork.ehealth.ApplicationProps;
import com.trifork.ehealth.fhir.annotations.OnDSTU3Condition;
import com.trifork.ehealth.google.Translator;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CodeSystem;
import org.hl7.fhir.dstu3.model.ValueSet;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@Conditional(OnDSTU3Condition.class)
public class CodeSystemResourceProvider implements IResourceProvider {

    private final Translator translator;

    public CodeSystemResourceProvider(Translator translator, ApplicationProps applicationProps) {
        this.translator = translator;

        var context = FhirContext.forDstu3();
        var client = context.newRestfulGenericClient("http://tx.fhir.org/r3");
        for (var a : applicationProps.getValuesets()) {
            var result = client.search().byUrl("ValueSet?url=" + a).returnBundle(Bundle.class).execute();
            var valueSet = BundleUtil.toListOfResourcesOfType(context, result, ValueSet.class).get(0);

            System.out.println("Looking into " + valueSet.getUrl());
            var codesystemStrings = valueSet.getCompose().getInclude().stream().map(ValueSet.ConceptSetComponent::getSystem).collect(Collectors.toList());

            System.out.println("Looking up CodeSystems: " + codesystemStrings.stream().collect(Collectors.joining(", ")));

            codesystemStrings.stream().forEach(s ->
            {
                var cs = client.search().byUrl("CodeSystem?url=" + s).returnBundle(Bundle.class).execute();

                var resources = BundleUtil.toListOfResourcesOfType(context, cs, CodeSystem.class);
                if (!resources.isEmpty()) {
                    var css = resources.get(0);
                    System.out.println("Nr of CodeSystem concepts: " + css.getConcept().size());
                }
                else
                    System.out.println("Could not lookup Codesystem");

            });


        }
    }

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return CodeSystem.class;
    }

    @Operation(name = "translate", returnParameters = {
            @OperationParam(name = "return", type = CodeSystem.class),
    })
    public CodeSystem translate(@OperationParam(name = "codeSystem") CodeSystem codeSystem, @OperationParam(name = "language") String language) {

        return translator.addDesignation(codeSystem, language);
    }
}
