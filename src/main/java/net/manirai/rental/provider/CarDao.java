package net.manirai.rental.provider;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.manirai.rental.repository.CarRepository;

/**
 * 
 * @author Mani
 *
 */
@Repository
@Transactional
public class CarDao {

    @PersistenceContext
    private EntityManager em;
    
    @Autowired
    private CarRepository carRepo;

    @SuppressWarnings("unchecked")
    public List<Car> listAll() {
        return em.createQuery("from Car").getResultList();
    }

    public void save(Car car) {
        em.persist(car);
    }
    
    public Car get(int id) {
        return this.em.find(Car.class, id);
    }

    public Car getCarByLicensePlate(String licensePlate) {
        return this.carRepo.getCarByLicensePlate(licensePlate);
    }

    public List<Car> getUnlistedCarById(Set<Integer> idList) {
        return this.carRepo.getUnlistedCarById(idList);
    }
}
