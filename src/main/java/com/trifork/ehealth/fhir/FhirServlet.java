package com.trifork.ehealth.fhir;

import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class FhirServlet extends RestfulServer {

    public FhirServlet(Collection<IResourceProvider> resourceProviders) {
        setResourceProviders(resourceProviders);
    }

}
