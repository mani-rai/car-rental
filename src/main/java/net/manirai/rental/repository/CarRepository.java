package net.manirai.rental.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.manirai.rental.provider.Car;

/**
 * 
 * @author Mani
 *
 */
@Repository
public interface CarRepository extends CrudRepository<Car, Integer> {

    Car getCarByLicensePlate(String licensePlate);

    @Query("SELECT car FROM Car car WHERE car.id NOT IN :idList")
    List<Car> getUnlistedCarById(@Param("idList") Set<Integer> idList);
}
