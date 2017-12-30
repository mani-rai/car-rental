package net.manirai.rental.provider;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Mani
 *
 */
@Repository
@Transactional
public class ProviderDao {

    @PersistenceContext
    private EntityManager em;

    @SuppressWarnings("unchecked")
    public List<Provider> listAll() {
        return em.createQuery("from Provider order by name").getResultList();
    }

    public Provider get(int id) {
        return em.find(Provider.class, id);
    }

    public void save(Provider provider) {
        em.persist(provider);
    }

    public void saveOrUpdate(Provider provider) {
        em.merge(provider);
    }


}
