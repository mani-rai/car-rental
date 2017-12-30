package net.manirai.rental;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

/**
 * 
 * @author Mani
 *
 */
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(RequestContextFilter.class);
        packages("net.manirai.rental");
        register(LoggingFilter.class);
        register(JacksonFeature.class);
        register(ValidationConfigurationContextResolver.class);
    }
}
