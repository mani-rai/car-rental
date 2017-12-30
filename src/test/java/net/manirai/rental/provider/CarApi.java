package net.manirai.rental.provider;

import static javax.ws.rs.client.Entity.json;

import java.util.List;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

/**
 * 
 * @author Mani
 *
 */
public class CarApi {
    private WebTarget api;

    public CarApi(WebTarget apiRoot) {
        this.api = apiRoot.path("cars");
    }

    public Response postCar(ProviderDto provider, String licensePlate) {
        return postCar(new CarDto(provider.getId(), licensePlate));
    }

    public Response postCar(CarDto car) {
        return api.request().post(json(car));
    }

    public CarDto createCar(ProviderDto provider, String licensePlate) {
        return postCar(provider, licensePlate).readEntity(CarDto.class);
    }

    public List<CarDto> getCars() {
        return api.request().get(new GenericType<List<CarDto>>() {
        });
    }
}
