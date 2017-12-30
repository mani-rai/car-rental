package net.manirai.rental.provider;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import net.manirai.rental.exception.ErrorInfo;

/**
 * 
 * @author Mani
 *
 */
@Component
@Transactional
@Path("/cars")
@Produces(APPLICATION_JSON)
public class CarResource {
    @Inject
    private ProviderDao providerDao;

    @Inject
    private CarDao carDao;

    @GET
    public List<CarDto> getCars() {
        return Lists.transform(carDao.listAll(), CarDto.fromCar());
    }

    @POST
    public Response createCar(@Valid CarDto dto, @Context UriInfo uriInfo) {
        Provider provider = providerDao.get(dto.getProviderId());
        if(provider == null) {
            return Response.status(Status.NOT_FOUND).entity(new ErrorInfo("No provider found!")).build();
        } else {
            
            Car existingCar = this.carDao.getCarByLicensePlate(dto.getLicensePlate().trim());
            
            if(existingCar != null) {
                return Response.status(Status.CONFLICT).entity(new ErrorInfo("Car already exist!")).build();
            } else {
                Car car = new Car(provider, dto.getLicensePlate().trim());
                this.carDao.save(car);
                URI uri = UriBuilder.fromResource(CarResource.class).path("{id}")
                        .build(provider.getId());
                return Response.created(uri).entity(CarDto.fromCar().apply(car))
                        .build();
            }
        }
    }
}
