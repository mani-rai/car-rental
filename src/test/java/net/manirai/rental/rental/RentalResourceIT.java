package net.manirai.rental.rental;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;

import javax.ws.rs.core.Response;
import net.manirai.rental.client.ClientApi;

import org.joda.time.LocalDate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.manirai.rental.client.ClientDto;
import net.manirai.rental.provider.CarApi;
import net.manirai.rental.provider.CarDto;
import net.manirai.rental.provider.ProviderApi;
import net.manirai.rental.provider.ProviderDto;
import static net.manirai.rental.rental.RentalDto.STATUS_BOOKED;
import static net.manirai.rental.rental.RentalDto.STATUS_CANCELED;
import net.manirai.rental.test.AbstractIT;

/**
 * 
 * @author Mani
 *
 */
public class RentalResourceIT extends AbstractIT {
    private static final String LICENSE_PLATE_1 = "FB8 A3E";
    private static final String LICENSE_PLATE_2 = "AC3 BBE";

    private static final LocalDate TOMORROW = new LocalDate();
    private static final LocalDate AFTER_TOMORROW = TOMORROW.plusDays(1);

    private ProviderApi providerApi;
    private CarApi carApi;
    private ClientApi clientApi;
    private RentalApi rentalApi;

    private ProviderDto provider;
    private CarDto car1;
    private CarDto car2;
    private ClientDto client1;
    private ClientDto client2;

    @Override
    @BeforeMethod
    public void setUp() {
        super.setUp();

        this.providerApi = new ProviderApi(api);
        this.clientApi = new ClientApi(api);
        this.carApi = new CarApi(api);
        this.rentalApi = new RentalApi(api);

        provider = providerApi.createProvider("Timmy");
        car1 = carApi.createCar(provider, LICENSE_PLATE_1);
        car2 = carApi.createCar(provider, LICENSE_PLATE_2);
        client1 = clientApi.createClient("Anne");
        client2 = clientApi.createClient("Betty");
    }

    @Test
    public void available_ShouldReturnAvailableCarsInDateRange() {
        List<AvailableCarRental> available = getAvailable(client1, TOMORROW,
                TOMORROW.plusDays(2));

        // Left end inclusive, right end exclusive
        assertThat(
                available,
                containsInAnyOrder(new AvailableCarRental(car1, TOMORROW),
                        new AvailableCarRental(car1, AFTER_TOMORROW),
                        new AvailableCarRental(car2, TOMORROW),
                        new AvailableCarRental(car2, AFTER_TOMORROW)));
    }

    @Test
    public void makeRental_shouldCreateRentalForUserAndCar() {
        RentalDto rental = createRental(car1, client1, TOMORROW,
                AFTER_TOMORROW);

        assertThat(rental.getRentalId(), is(notNullValue()));
    }

    @Test
    public void makeRental_shouldRequireCar() {
        Response resp = postRental(null, client1.getId(), TOMORROW,
                AFTER_TOMORROW);

        assertThat(resp.getStatus(), is(BAD_REQUEST.getStatusCode()));
    }

    @Test
    public void makeRental_shouldReturnNotFoundWhenCarDoesntExist() {
        Response resp = postRental(113, client1.getId(), TOMORROW,
                AFTER_TOMORROW);

        assertThat(resp.getStatus(), is(NOT_FOUND.getStatusCode()));
    }

    @Test
    public void makeRental_shouldRequireClient() {
        Response resp = postRental(car1.getCarId(), null, TOMORROW, AFTER_TOMORROW);
        
        assertThat(resp.getStatus(), is(BAD_REQUEST.getStatusCode()));
    }

    @Test
    public void makeRental_shouldReturnNotFoundWhenClientDoesntExist() {
        Response resp = postRental(car1.getCarId(), 113, TOMORROW,
              AFTER_TOMORROW);
        
        assertThat(resp.getStatus(), is(NOT_FOUND.getStatusCode()));
    }

    @Test
    public void makeRental_shouldRequireFromDate() {
        Response resp = postRental(car1.getCarId(), client1.getId(), null,
              AFTER_TOMORROW);
        
        assertThat(resp.getStatus(), is(BAD_REQUEST.getStatusCode()));
    }

    @Test
    public void makeRental_shouldRequireToDate() {
        Response resp = postRental(car1.getCarId(), client1.getId(), TOMORROW,
              null);
        
        assertThat(resp.getStatus(), is(BAD_REQUEST.getStatusCode()));
    }

    @Test
    public void makeRental_shouldRequireFromDateBeforeToDate() {
        Response resp = postRental(car1.getCarId(), client1.getId(), TOMORROW.plusDays(2),
                TOMORROW);
          
          assertThat(resp.getStatus(), is(BAD_REQUEST.getStatusCode()));
    }

    @Test
    public void available_ShouldExcludeBookedCars() {
        // Given an existing rental
        createRental(car1, client1, AFTER_TOMORROW, TOMORROW.plusDays(3));

        // When querying available cars for another client
        List<AvailableCarRental> available = getAvailable(client2, TOMORROW,
                TOMORROW.plusDays(4));

        // Then expect it to include all days for non-booked car, and only the
        // non-booked days for car2
        assertThat(
                available,
                containsInAnyOrder(
                        //
                        new AvailableCarRental(car2, TOMORROW),
                        new AvailableCarRental(car2, AFTER_TOMORROW),
                        new AvailableCarRental(car2, TOMORROW.plusDays(2)),
                        new AvailableCarRental(car2, TOMORROW.plusDays(3)),

                        new AvailableCarRental(car1, TOMORROW),
                        new AvailableCarRental(car1, TOMORROW.plusDays(3))));
    }

    @Test
    public void available_ShouldExcludeSlotsOverlappingRentalsForTheSameClient() {
        // Given an existing rental
        createRental(car1, client1, AFTER_TOMORROW, TOMORROW.plusDays(3));

        // When querying available cars for the same client
        List<AvailableCarRental> available = getAvailable(client1, TOMORROW,
                TOMORROW.plusDays(4));

        // Then expect it to exclude the days he already has booked
        assertThat(
                available,
                containsInAnyOrder(
                        //
                        new AvailableCarRental(car2, TOMORROW),
                        new AvailableCarRental(car2, TOMORROW.plusDays(3)),

                        new AvailableCarRental(car1, TOMORROW),
                        new AvailableCarRental(car1, TOMORROW.plusDays(3))));
    }

    @Test
    public void makeRental_shouldPreventRentalOfAlreadyBookedCar() {
        postRental(car1, client1, AFTER_TOMORROW, TOMORROW.plusDays(2));
        Response resp = postRental(car1, client2, TOMORROW,
                TOMORROW.plusDays(2));

        assertThat(resp.getStatus(), is(CONFLICT.getStatusCode()));
    }

    @Test
    public void makeRental_shouldPreventRentalOverlappingWithAnotherRentalByTheSameUser() {
        postRental(car1, client1, AFTER_TOMORROW, TOMORROW.plusDays(2));
        Response resp = postRental(car2, client1, AFTER_TOMORROW,
                TOMORROW.plusDays(3));

        assertThat(resp.getStatus(), is(CONFLICT.getStatusCode()));
    }

    @Test
    public void makeRental_shouldAllowAdjacentRentals() {
        postRental(car1, client1, AFTER_TOMORROW, TOMORROW.plusDays(2));

        Response resp = postRental(car1, client1, TOMORROW, AFTER_TOMORROW);
        assertThat(resp.getStatus(), is(CREATED.getStatusCode()));

        resp = postRental(car1, client1, TOMORROW.plusDays(2),
                TOMORROW.plusDays(5));
        assertThat(resp.getStatus(), is(CREATED.getStatusCode()));
    }

    @Test
    public void cancellation_shouldUpdateRentalStatus() {
        RentalDto rental = createRental(car1, client1, TOMORROW,
                AFTER_TOMORROW);

        assertThat(rental.getStatus(), is(STATUS_BOOKED));

        cancelRental(rental);

        RentalDto loaded = getRental(rental.getRentalId());
        assertThat(loaded.getStatus(), is(STATUS_CANCELED));
    }

    @Test
    public void makeRental_shouldAllowOverlapWithCanceled() {
        RentalDto old = createRental(car1, client1, AFTER_TOMORROW,
                TOMORROW.plusDays(3));
        cancelRental(old);

        Response newResp = postRental(car1, client1, TOMORROW,
                TOMORROW.plusDays(2));
        assertThat(newResp.getStatus(), is(CREATED.getStatusCode()));
    }
    
    @Test
    public void rentedSameCarTwiceBySameClient_GetAvailableForSameClient() {
        createRental(car1, client1, AFTER_TOMORROW, TOMORROW.plusDays(3));
        createRental(car1, client1, TOMORROW.plusDays(3), TOMORROW.plusDays(6));
        
        List<AvailableCarRental> available = getAvailable(client1, TOMORROW, TOMORROW.plusDays(8));
        
        assertThat(available, containsInAnyOrder(
                                                new AvailableCarRental(car1, TOMORROW),
                                                new AvailableCarRental(car1, TOMORROW.plusDays(6)),
                                                new AvailableCarRental(car1, TOMORROW.plusDays(7)),
                                                
                                                new AvailableCarRental(car2, TOMORROW),
                                                new AvailableCarRental(car2, TOMORROW.plusDays(6)),
                                                new AvailableCarRental(car2, TOMORROW.plusDays(7))));
    }
    
    @Test
    public void rentedTwoDiffCarBySameClient_GetAvailableForSameClient() {
        createRental(car1, client1, TOMORROW, TOMORROW.plusDays(3));
        createRental(car2, client1, TOMORROW.plusDays(5), TOMORROW.plusDays(7));
        
        List<AvailableCarRental> available = getAvailable(client1, TOMORROW.plusDays(3), TOMORROW.plusDays(9));
        
        assertThat(available, containsInAnyOrder(
                                                new AvailableCarRental(car1, TOMORROW.plusDays(3)),
                                                new AvailableCarRental(car1, TOMORROW.plusDays(4)),
                                                new AvailableCarRental(car1, TOMORROW.plusDays(7)),
                                                new AvailableCarRental(car1, TOMORROW.plusDays(8)),
                                                
                                                new AvailableCarRental(car2, TOMORROW.plusDays(3)),
                                                new AvailableCarRental(car2, TOMORROW.plusDays(4)),
                                                new AvailableCarRental(car2, TOMORROW.plusDays(7)),
                                                new AvailableCarRental(car2, TOMORROW.plusDays(8))));
    }
    
    @Test
    public void rentedTwoDiffCarBySameClient_GetAvailableForDiffClient() {
        createRental(car1, client1, TOMORROW.plusDays(3), TOMORROW.plusDays(6));
        createRental(car2, client1, TOMORROW.plusDays(8), TOMORROW.plusDays(10));
        
        List<AvailableCarRental> available = getAvailable(client2, TOMORROW, TOMORROW.plusDays(9));
        
        assertThat(available, containsInAnyOrder(
                                                new AvailableCarRental(car1, TOMORROW),
                                                new AvailableCarRental(car1, AFTER_TOMORROW),
                                                new AvailableCarRental(car1, TOMORROW.plusDays(2)),
                                                new AvailableCarRental(car1, TOMORROW.plusDays(6)),
                                                new AvailableCarRental(car1, TOMORROW.plusDays(7)),
                                                new AvailableCarRental(car1, TOMORROW.plusDays(8)),
                                                
                                                new AvailableCarRental(car2, TOMORROW),
                                                new AvailableCarRental(car2, AFTER_TOMORROW),
                                                new AvailableCarRental(car2, TOMORROW.plusDays(2)),
                                                new AvailableCarRental(car2, TOMORROW.plusDays(3)),
                                                new AvailableCarRental(car2, TOMORROW.plusDays(4)),
                                                new AvailableCarRental(car2, TOMORROW.plusDays(5)),
                                                new AvailableCarRental(car2, TOMORROW.plusDays(6)),
                                                new AvailableCarRental(car2, TOMORROW.plusDays(7))));
    }
    
    @Test
    public void rentedSameCarByTwoDiffClient_GetAvailable() {
        createRental(car1, client1, AFTER_TOMORROW, TOMORROW.plusDays(3));
        createRental(car1, client2, TOMORROW.plusDays(3), TOMORROW.plusDays(6));
        
        List<AvailableCarRental> available = getAvailable(client2, TOMORROW, TOMORROW.plusDays(4));
        
        assertThat(available, containsInAnyOrder(
                                                new AvailableCarRental(car1, TOMORROW),
                                                
                                                new AvailableCarRental(car2, TOMORROW),
                                                new AvailableCarRental(car2, AFTER_TOMORROW),
                                                new AvailableCarRental(car2, TOMORROW.plusDays(2))));
    }
        
    public List<AvailableCarRental> getAvailable(ClientDto client,
            LocalDate from, LocalDate to) {
        return rentalApi.getAvailable(client, from, to);
    }

    public RentalDto getRental(int id) {
        return rentalApi.getRental(id);
    }

    public Response postRental(Integer carId, Integer clientId,
            LocalDate from, LocalDate to) {
        return rentalApi.postRental(carId, clientId, from, to);
    }

    public Response postRental(CarDto car, ClientDto client, LocalDate from,
            LocalDate to) {
        return rentalApi.postRental(car, client, from, to);
    }

    public RentalDto createRental(CarDto car, ClientDto client,
            LocalDate from, LocalDate to) {
        return rentalApi.createRental(car, client, from, to);
    }

    public void cancelRental(RentalDto rental) {
        rentalApi.cancelRental(rental);
    }

}
