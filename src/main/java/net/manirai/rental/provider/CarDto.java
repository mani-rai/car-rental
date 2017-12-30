package net.manirai.rental.provider;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.google.common.base.Function;

/**
 * 
 * @author Mani
 *
 */
public class CarDto {
    private Integer carId;

    @NotNull
    private Integer providerId;

    @NotBlank
    private String licensePlate;

    public CarDto() {
    }

    public CarDto(Integer providerId, String licensePlate) {
        this.providerId = providerId;
        this.licensePlate = licensePlate;
    }

    public CarDto(Integer carId, Integer providerId, String licensePlate) {
        this.carId = carId;
        this.providerId = providerId;
        this.licensePlate = licensePlate;
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public static Function<Car, CarDto> fromCar() {
        return new Function<Car, CarDto>() {
            @Override
            public CarDto apply(Car car) {
                return new CarDto(car.getId(), car.getProvider().getId(),
                        car.getLicensePlate());
            }
        };
    }

}
