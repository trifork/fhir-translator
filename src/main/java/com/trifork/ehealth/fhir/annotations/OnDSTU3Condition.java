package com.trifork.ehealth.fhir.annotations;

import ca.uhn.fhir.context.FhirVersionEnum;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class OnDSTU3Condition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata metadata) {
        FhirVersionEnum version = FhirVersionEnum.forVersionString(conditionContext.
                getEnvironment()
                .getRequiredProperty("fhir_version")
                .toUpperCase());

        return version == FhirVersionEnum.DSTU3;

    }
}
