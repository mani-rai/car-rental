package net.manirai.rental.rental;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.manirai.rental.repository.RentalRepository;

/**
 * 
 * @author Mani
 *
 */
@Repository
@Transactional
public class RentalDao {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private RentalRepository rentalRepo;

    public void save(Rental rental) {
        this.em.persist(rental);
    }

    public List<Rental> getOverlappingRents(int carId, int clientId,
            LocalDate from, LocalDate to, String status) {
        return this.rentalRepo.getOverlappingRents(carId, clientId, from, to,
                status);
    }

    public Rental get(int rentalId) {
        return this.em.find(Rental.class, rentalId);
    }

    public void update(Rental rental) {
        this.em.merge(rental);
    }

    public List<Rental> getClientsOverlappingRents(int clientId,
            LocalDate from, LocalDate to, String status) {
        return this.rentalRepo.getClientsOverlappingRents(clientId, from, to, status);
    }

    public List<Rental> getOverlappingRentsAndCarsByDateDuration(
            LocalDate from, LocalDate to, String status) {
        return this.rentalRepo.getOverlappingRentsAndCarsByDateDuration(from, to, status);
    }
}