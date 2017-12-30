package net.manirai.rental.provider;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import javax.ws.rs.core.Response;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.manirai.rental.exception.ErrorInfo;
import net.manirai.rental.test.AbstractIT;

/**
 * 
 * @author Mani
 *
 */
public class CarResourceIT extends AbstractIT {

    private ProviderApi providerApi;
    private CarApi carApi;

    @Override
    @BeforeMethod
    public void setUp() {
        super.setUp();

        this.providerApi = new ProviderApi(api);
        this.carApi = new CarApi(api);
    }

    @Test
    public void shouldReturnEmptyListWhenNoCars() {
        assertThat(carApi.getCars(), is(empty()));
    }

    @Test
    public void shouldCreateCarForProvider() {
        ProviderDto provider = createProvider("Timmy");
        CarDto car = carApi.createCar(provider, "\tFB8 A3E  ");
        assertThat(car.getCarId(), not(nullValue()));
        assertThat(car.getProviderId(), is(provider.getId()));
        assertThat(car.getLicensePlate(), is("FB8 A3E"));
    }

    @Test
    public void createShouldReturnBadRequestWhenProviderIsNull() {
        assertThat(carApi.postCar(new CarDto(null, "FB8 A3E")).getStatus(),
                is(BAD_REQUEST.getStatusCode()));
    }

    @Test
    public void createShouldReturnBadRequestWhenLicensePlateIsNull() {
        ProviderDto provider = createProvider("Timmy");
        assertThat(carApi.postCar(new CarDto(provider.getId(), null))
                .getStatus(), is(BAD_REQUEST.getStatusCode()));
    }

    @Test
    public void createShouldReturnBadRequestWhenLicensePlateIsBlank() {
        ProviderDto provider = createProvider("Timmy");
        assertThat(carApi.postCar(new CarDto(provider.getId(), "  \t"))
                .getStatus(), is(BAD_REQUEST.getStatusCode()));
    }

    @Test
    public void createShouldReturnNotFoundWhenProviderDoesntExist() {
        Response resp = this.carApi.postCar(new CarDto(117, "FB8 A3E"));
        
        assertThat(resp.getStatus(),
                is(NOT_FOUND.getStatusCode()));
        assertThat(resp.readEntity(ErrorInfo.class).getMessage(), is("No provider found!"));
    }

    @Test
    public void createShouldReturnConflictWhenCarAlreadyExists() {
        ProviderDto provider = createProvider("Timmy");
        assertThat(carApi.postCar(new CarDto(provider.getId(), "FB8 A3E"))
                .getStatus(), is(CREATED.getStatusCode()));
        Response resp = this.carApi.postCar(new CarDto(provider.getId(), "FB8 A3E  "));
        assertThat(resp.getStatus(), is(CONFLICT.getStatusCode()));
        assertThat(resp.readEntity(ErrorInfo.class).getMessage(), is("Car already exist!"));
    }

    public ProviderDto createProvider(String name) {
        return providerApi.createProvider(name);
    }
}
