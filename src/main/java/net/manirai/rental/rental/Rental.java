package net.manirai.rental.rental;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;

import net.manirai.rental.client.Client;
import net.manirai.rental.provider.Car;

/**
 * 
 * @author Mani
 *
 */
@Entity
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    
    private String status;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Car car;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;
    
    @NotNull
    @Column(name = "rent_from")
    private LocalDate from;
    
    @NotNull
    @Column(name = "rent_to")
    private LocalDate to;
    
    public Rental() {
    }
    
    public Rental(Car car, Client client, LocalDate from, LocalDate to) {
        this.car = car;
        this.client = client;
        this.from = from;
        this.to = to;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
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
}
