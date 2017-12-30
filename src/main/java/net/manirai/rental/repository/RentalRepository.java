package net.manirai.rental.repository;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.manirai.rental.rental.Rental;

/**
 * 
 * @author Mani
 *
 */
@Repository
public interface RentalRepository extends CrudRepository<Rental, Integer> {

    @Query("SELECT rental FROM Rental rental WHERE (rental.car.id = :carId OR rental.client.id = :clientId)"
                                                    + " AND (:from BETWEEN rental.from AND rental.to OR :to BETWEEN rental.from AND rental.to OR rental.from BETWEEN :from AND :to OR rental.to BETWEEN :from AND :to)"
                                                    + " AND :from != rental.to"
                                                    + " AND :to != rental.from"
                                                    + " AND rental.status = :status")
    public List<Rental> getOverlappingRents(@Param("carId") int carId,
                                            @Param("clientId") int clientId,
                                            @Param("from") LocalDate from,
                                            @Param("to") LocalDate to,
                                            @Param("status") String status);

    @Query("SELECT rental FROM Rental rental WHERE rental.client.id = :clientId"
                                                    + " AND (:from BETWEEN rental.from AND rental.to OR :to BETWEEN rental.from AND rental.to OR rental.from BETWEEN :from AND :to OR rental.to BETWEEN :from AND :to)"
                                                    + " AND :from != rental.to"
                                                    + " AND :to != rental.from"
                                                    + " AND rental.status = :status")
    public List<Rental> getClientsOverlappingRents(@Param("clientId") int clientId,
                                                    @Param("from") LocalDate from,
                                                    @Param("to") LocalDate to,
                                                    @Param("status") String status);
    
    @Query("SELECT rental FROM Rental rental LEFT JOIN FETCH rental.car car WHERE (:from BETWEEN rental.from AND rental.to OR :to BETWEEN rental.from AND rental.to OR rental.from BETWEEN :from AND :to OR rental.to BETWEEN :from AND :to)"
                                                    + " AND :from != rental.to"
                                                    + " AND :to != rental.from"
                                                    + " AND rental.status = :status")
    public List<Rental> getOverlappingRentsAndCarsByDateDuration(
            @Param("from") LocalDate from, @Param("to") LocalDate to, @Param("status") String status);
}