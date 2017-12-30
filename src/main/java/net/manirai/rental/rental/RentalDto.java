package net.manirai.rental.rental;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;

import com.google.common.base.Function;
import net.manirai.rental.constraint.ValidDateDuration;

/**
 * 
 * @author Mani
 *
 */
@ValidDateDuration(from = "from", to = "to")
public class RentalDto {
    public static final String STATUS_BOOKED = "booked";
    public static final String STATUS_CANCELED = "canceled";

    private Integer rentalId;

    private String status;

    @NotNull
    private Integer carId;

    @NotNull
    private Integer clientId;

    @NotNull
    private LocalDate from;

    @NotNull
    private LocalDate to;

    public RentalDto() {
    }
    
    public RentalDto(Integer carId, Integer clientId, LocalDate from,
            LocalDate to) {
        this.carId = carId;
        this.clientId = clientId;
        this.from = from;
        this.to = to;
    }

    public RentalDto(String status, Integer carId, Integer clientId, LocalDate from,
            LocalDate to) {
        this.status = status;
        this.carId = carId;
        this.clientId = clientId;
        this.from = from;
        this.to = to;
    }
    
    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }

    public Integer getRentalId() {
        return rentalId;
    }

    public void setRentalId(Integer rentalId) {
        this.rentalId = rentalId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(rentalId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RentalDto)) {
            return false;
        }
        RentalDto other = (RentalDto) obj;
        return Objects.equals(rentalId, other.rentalId)
                && Objects.equals(status, other.status)
                && Objects.equals(carId, other.carId)
                && Objects.equals(clientId, other.clientId)
                && Objects.equals(from, other.from)
                && Objects.equals(to, other.to);
    }

    public static Function<Rental, RentalDto> fromRental() {
        return new Function<Rental, RentalDto>() {

            @Override
            public RentalDto apply(Rental rental) {
                return new RentalDto(rental.getStatus(), rental.getCar().getId(), rental.getClient().getId(), rental.getFrom(), rental.getTo());
            }
            
        };
    }

}
