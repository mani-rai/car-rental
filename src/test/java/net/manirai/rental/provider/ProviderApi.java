package net.manirai.rental.provider;

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
public class ProviderApi {
    private WebTarget api;

    public ProviderApi(WebTarget apiRoot) {
        this.api = apiRoot.path("providers");
    }

    public Response postProvider(String name) {
        return api.request().post(json(new ProviderDto(name)));
    }

    public ProviderDto createProvider(String name) {
        return postProvider(name).readEntity(ProviderDto.class);
    }

    public Response putProvider(int id, String name) {
        return api.path(String.valueOf(id)).request()
                .put(json(new ProviderDto(id, name)));
    }

    public List<ProviderDto> getProviders() {
        return api.request().get(new GenericType<List<ProviderDto>>() {
        });
    }

    public Response getProvider(int id) {
        return api.path(String.valueOf(id)).request().get();
    }

}
