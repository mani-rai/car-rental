package net.manirai.rental.rental;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import net.manirai.rental.client.Client;
import net.manirai.rental.client.ClientDao;
import net.manirai.rental.exception.ErrorInfo;
import net.manirai.rental.provider.Car;
import net.manirai.rental.provider.CarDao;
import net.manirai.rental.provider.CarDto;

/**
 * 
 * @author Mani
 *
 */
@Component
@Transactional
@Path("/rentals")
@Produces(APPLICATION_JSON)
public class RentalResource {

    private static final Log log = LogFactory.getLog(RentalResource.class);

    @Inject
    private CarDao carDao;

    @Inject
    private ClientDao clientDao;

    @Inject
    private RentalDao rentalDao;

    @POST
    public Response createRental(@Valid RentalDto dto, @Context UriInfo uriInfo) {

        Car car = this.carDao.get(dto.getCarId());

        if (car == null) {
            return Response.status(Status.NOT_FOUND)
                    .entity(new ErrorInfo("No car found!")).build();
        } else {

            Client client = this.clientDao.get(dto.getClientId());

            if (client == null) {
                return Response.status(Status.NOT_FOUND)
                        .entity(new ErrorInfo("No cliend found!")).build();
            } else {

                List<Rental> overlappingRents = this.rentalDao
                        .getOverlappingRents(dto.getCarId(), dto.getClientId(),
                                dto.getFrom(), dto.getTo(),
                                RentalDto.STATUS_BOOKED);

                if (overlappingRents != null && overlappingRents.size() > 0) {
                    return Response
                            .status(Status.CONFLICT)
                            .entity(new ErrorInfo(
                                    "Rentals already exists in specified date duration."))
                            .build();
                } else {

                    Rental rental = new Rental(car, client, dto.getFrom(),
                            dto.getTo());
                    rental.setStatus(RentalDto.STATUS_BOOKED);

                    this.rentalDao.save(rental);

                    RentalDto rentalDto = RentalDto.fromRental().apply(rental);
                    rentalDto.setRentalId(rental.getId());

                    URI uri = UriBuilder.fromResource(RentalResource.class)
                            .path("{id}").build(rental.getId());
                    return Response.created(uri).entity(rentalDto).build();
                }
            }
        }
    }

    @GET
    @Path("/available")
    public List<AvailableCarRental> getAvailable(
            @QueryParam("client") int clientId,
            @QueryParam("from") String from, @QueryParam("to") String to) {

        LocalDate fromDate = null;
        LocalDate toDate = null;

        // Date parsing could stop getting available car service. So, for
        // debugging simplicity date parsing
        // has been logged if any exception occurs.
        try {
            fromDate = LocalDate.parse(from);
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error("Exception while parsing 'FROM' parameter", e);
            throw new BadRequestException();
        }

        try {
            toDate = LocalDate.parse(to);
        } catch (NullPointerException | IllegalArgumentException e) {
            log.error("Exception while parsing 'TO' parameter", e);
            throw new BadRequestException();
        }

        List<AvailableCarRental> acrList = new ArrayList<AvailableCarRental>();

        if (fromDate != null && toDate != null) {
            Set<AvailableCarRental> acrSet = null;

            List<Rental> overLappingBookedRentsByDateDuration = this.rentalDao
                    .getOverlappingRentsAndCarsByDateDuration(fromDate, toDate,
                            RentalDto.STATUS_BOOKED);

            // This block of code checks overlapped rents and gets available
            // cars available in each day
            if (overLappingBookedRentsByDateDuration != null
                    && overLappingBookedRentsByDateDuration.size() > 0) {

                acrSet = new HashSet<AvailableCarRental>();

                // Removals are as identifiers for those AvailableCarRental
                // objects that must not be contained in the returned list.
                Set<String> removals = new HashSet<String>();

                for (Rental rental : overLappingBookedRentsByDateDuration) {
                    LocalDate tempDate = fromDate;

                    // Looping through each day to check if rented cars are
                    // available for a specific day or not.
                    while (toDate.isAfter(tempDate)) {

                        // Condition that will check if a specific date is
                        // overlapped in rented duration.
                        if (!(tempDate.isEqual(rental.getFrom()) || (tempDate
                                .isAfter(rental.getFrom()) && tempDate
                                .isBefore(rental.getTo())))) {
                            AvailableCarRental acr = new AvailableCarRental(
                                    CarDto.fromCar().apply(rental.getCar()),
                                    tempDate);
                            acrSet.add(acr);
                        } else {
                            // Adds an identifier that is not eligible for
                            // return list.
                            // This identifier logic is used for a complex
                            // scenario. For Example:
                            // A client having same car rented twice with
                            // different date duration
                            // will have two different rents which will get
                            // looped twice with different date duration.
                            // In each loop an ineligible car or date gets
                            // inserted to the list with respect to another
                            // rent.
                            // So this identifier gets added in each loop which
                            // is used further to remove the ineligible cars and
                            // dates.
                            removals.add(rental.getCar().getLicensePlate()
                                    + tempDate);
                        }
                        tempDate = tempDate.plusDays(1);
                    }

                    // Checks for contaminant of ineligible cars and dates in
                    // generated list.
                    // If found removes from generated list.
                    for (String identifier : removals) {
                        Iterator<AvailableCarRental> iter = acrSet.iterator();
                        while (iter.hasNext()) {
                            AvailableCarRental root = iter.next();
                            if ((root.getCar().getLicensePlate() + root
                                    .getDate()).equalsIgnoreCase(identifier)) {
                                iter.remove();
                            }
                        }
                    }
                }
            }
            // End of getting available cars in specific date logic from
            // overlapped rents.

            Set<Integer> availableCarIdList = null;

            // Getting car id list generated from overlapped rents.
            // Meanwhile, converting set to list as required by API's.
            if (acrSet != null && acrSet.size() > 0) {
                availableCarIdList = new HashSet<Integer>();
                for (AvailableCarRental acr : acrSet) {
                    acrList.add(acr);
                    availableCarIdList.add(acr.getCar().getCarId());
                }
            }

            List<Car> availableCarList = null;

            // This logic gets the remaining cars that are not being overlapped
            // or that are being cancelled.
            // If available car list are generated from overlapped rents,
            // unlisted remaining cars are retrieved.
            // If no available car list are generated this block of code gets
            // all the cars.
            if (availableCarIdList != null && availableCarIdList.size() > 0) {
                availableCarList = this.carDao
                        .getUnlistedCarById(availableCarIdList);
            } else {
                availableCarList = this.carDao.listAll();
            }

            // Getting client specific overlapping rents to check for client
            // available date and add unoverlapped cars relatively.
            // Similar logic is used to add available cars in a specific date.
            List<Rental> clientsOverlappingRents = this.rentalDao
                    .getClientsOverlappingRents(clientId, fromDate, toDate,
                            RentalDto.STATUS_BOOKED);

            // An identifier that must not be included in the return list.
            Set<LocalDate> overlappedDates = null;

            // Getting identifiers
            if (clientsOverlappingRents != null
                    && clientsOverlappingRents.size() > 0) {
                overlappedDates = new HashSet<LocalDate>();
                for (Rental rental : clientsOverlappingRents) {
                    LocalDate tempDate = fromDate;
                    while (toDate.isAfter(tempDate)) {
                        if (tempDate.isEqual(rental.getFrom())
                                || tempDate.isAfter(rental.getFrom())
                                && tempDate.isBefore(rental.getTo())) {
                            overlappedDates.add(tempDate);
                        }
                        tempDate = tempDate.plusDays(1);
                    }
                }
            }

            // Adding cars and dates in a list
            // If overlapped client dates are available, the remaining cars are
            // added with specific available date.
            // If overlapped client dates are unavailable, the remaining cars
            // are added in all the request days.
            if (overlappedDates != null && overlappedDates.size() > 0) {
                if (availableCarList != null && availableCarList.size() > 0) {
                    for (Car car : availableCarList) {
                        LocalDate tempDate = fromDate;
                        while (toDate.isAfter(tempDate)) {
                            if (!overlappedDates.contains(tempDate)) {
                                AvailableCarRental acr = new AvailableCarRental(
                                        CarDto.fromCar().apply(car), tempDate);
                                acrList.add(acr);
                            }
                            tempDate = tempDate.plusDays(1);
                        }
                    }
                }
            } else {
                if (availableCarList != null && availableCarList.size() > 0) {
                    for (Car car : availableCarList) {
                        LocalDate tempDate = fromDate;
                        while (toDate.isAfter(tempDate)) {
                            AvailableCarRental acr = new AvailableCarRental(
                                    CarDto.fromCar().apply(car), tempDate);
                            acrList.add(acr);
                            tempDate = tempDate.plusDays(1);
                        }
                    }
                }
            }
        }

        return acrList;
    }

    @DELETE
    @Path("/{id}")
    public void cancelRental(@PathParam("id") int id) {

        Rental rental = this.rentalDao.get(id);
        rental.setStatus(RentalDto.STATUS_CANCELED);

        this.rentalDao.update(rental);
    }

    @GET
    @Path("/{id}")
    public RentalDto getRental(@PathParam("id") int id) {
        Rental rental = this.rentalDao.get(id);
        return RentalDto.fromRental().apply(rental);
    }
}
