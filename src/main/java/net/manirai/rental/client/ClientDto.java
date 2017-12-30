package net.manirai.rental.client;

import java.util.Objects;

import org.hibernate.validator.constraints.NotBlank;

import com.google.common.base.Function;

/**
 * 
 * @author Mani
 *
 */
public class ClientDto {
    private Integer clientId;

    @NotBlank
    private String name;

    public ClientDto() {
    }

    public ClientDto(Integer id, String name) {
        this.clientId = id;
        this.name = name;
    }

    public ClientDto(String name) {
        this.name = name;
    }

    public Integer getId() {
        return clientId;
    }

    public void setId(Integer id) {
        this.clientId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(clientId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ClientDto)) {
            return false;
        }
        ClientDto other = (ClientDto) obj;
        return Objects.equals(clientId, other.clientId)
                && Objects.equals(name, other.name);
    }

    @Override
    public String toString() {
        return "Client " + clientId + " / " + name;
    }

    public static Function<Client, ClientDto> fromClient() {
        return new Function<Client, ClientDto>() {

            @Override
            public ClientDto apply(Client client) {
                return new ClientDto(client.getId(), client.getName());
            }
            
        };
    }
}
