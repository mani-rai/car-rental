package net.manirai.rental.rental;

import java.util.Objects;

import org.joda.time.LocalDate;

import net.manirai.rental.provider.CarDto;

/**
 * 
 * @author Mani
 *
 */
public class AvailableCarRental {

    private CarDto car;
    private LocalDate date;

    public AvailableCarRental() {
    }

    public AvailableCarRental(CarDto car, LocalDate date) {
        this.car = car;
        this.date = date;
    }

    public CarDto getCar() {
        return car;
    }

    public void setCar(CarDto car) {
        this.car = car;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(date);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AvailableCarRental)) {
            return false;
        }
        AvailableCarRental other = (AvailableCarRental) obj;
        return Objects.equals(car.getCarId(), other.car.getCarId())
                && Objects.equals(date, other.date);
    }
}
