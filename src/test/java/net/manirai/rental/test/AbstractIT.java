package net.manirai.rental.test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import net.manirai.rental.Application;

/**
 * 
 * @author Mani
 *
 */
public abstract class AbstractIT {
    protected ApplicationContext app;
    protected Client client;
    protected WebTarget api;

    @BeforeMethod
    public void setUp() {
        app = SpringApplication.run(Application.class);

        ClientConfig cc = new ClientConfig().register(JacksonFeature.class);
        client = ClientBuilder.newClient(cc);
        api = client.target("http://localhost:8080/api");
    }

    @AfterMethod
    public void tearDown() {
        SpringApplication.exit(app);
    }

}
