package net.manirai.rental.client;

import static javax.ws.rs.client.Entity.json;

import java.util.List;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

/**
 * 
 * @author Mani
 *
 */
public class ClientApi {
    private WebTarget api;

    public ClientApi(WebTarget apiRoot) {
        this.api = apiRoot.path("clients");
    }

    public List<ClientDto> getClients() {
        return api.request().get(new GenericType<List<ClientDto>>() {
        });
    }

    public Response postClient(String name) {
        return api.request().post(json(new ClientDto(name)));
    }

    public ClientDto createClient(String name) {
        return postClient(name).readEntity(ClientDto.class);
    }

}
