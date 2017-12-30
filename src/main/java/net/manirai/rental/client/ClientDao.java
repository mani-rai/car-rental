package net.manirai.rental.client;

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
public class ClientDao {

    @PersistenceContext
    private EntityManager em;
    
    @SuppressWarnings("unchecked")
    public List<Client> listAll() {
        return this.em.createQuery("from Client order by name").getResultList();
    }

    public void save(Client client) {
        this.em.persist(client);
    }

    public Client get(int id) {
        return this.em.find(Client.class, id);
    }

}
