package com.storeapp.shared.config;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

/**
 * Configurazione CORS per permettere richieste dal frontend Angular
 */
@Provider
public class CorsConfiguration implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext,
                      ContainerResponseContext responseContext) throws IOException {

        // Permetti richieste da localhost:4200 (frontend Angular)
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "http://localhost:4200");

        // Permetti credenziali (cookies, authorization headers)
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");

        // Permetti questi metodi HTTP
        responseContext.getHeaders().add("Access-Control-Allow-Methods",
            "GET, POST, PUT, DELETE, OPTIONS, HEAD");

        // Permetti questi headers
        responseContext.getHeaders().add("Access-Control-Allow-Headers",
            "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, " +
            "Access-Control-Request-Headers, Authorization");

        // Cache preflight per 1 ora
        responseContext.getHeaders().add("Access-Control-Max-Age", "3600");
    }
}
