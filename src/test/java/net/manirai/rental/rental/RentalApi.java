package net.manirai.rental.rental;

import static javax.ws.rs.client.Entity.json;

import java.util.List;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.joda.time.LocalDate;

import net.manirai.rental.client.ClientDto;
import net.manirai.rental.provider.CarDto;

/**
 * 
 * @author Mani
 *
 */
public class RentalApi {
	private WebTarget api;

	public RentalApi(WebTarget apiRoot) {
		this.api = apiRoot.path("rentals");
	}

	public List<AvailableCarRental> getAvailable(ClientDto client,
			LocalDate from, LocalDate to) {
		return api.path("available").queryParam("client", client.getId())
				.queryParam("from", from).queryParam("to", to).request()
				.get(new GenericType<List<AvailableCarRental>>() {
				});
	}

	public RentalDto getRental(int id) {
		return api.path(String.valueOf(id)).request().get(RentalDto.class);
	}

	public Response postRental(Integer carId, Integer clientId, LocalDate from,
			LocalDate to) {
		return api.request().post(
				json(new RentalDto(carId, clientId, from, to)));
	}

	public Response postRental(CarDto car, ClientDto client, LocalDate from,
			LocalDate to) {
		return postRental(car.getCarId(), client.getId(), from, to);
	}

	public RentalDto createRental(CarDto car, ClientDto client, LocalDate from,
			LocalDate to) {
		return postRental(car, client, from, to).readEntity(RentalDto.class);
	}

	public void cancelRental(RentalDto rental) {
		api.path(String.valueOf(rental.getRentalId())).request().delete();
	}

}
