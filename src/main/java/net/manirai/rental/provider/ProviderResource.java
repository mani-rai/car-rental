package net.manirai.rental.provider;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

/**
 * 
 * @author Mani
 *
 */
@Component
@Transactional
@Path("/providers")
@Produces(APPLICATION_JSON)
public class ProviderResource {
    @Inject
    private ProviderDao dao;

    @GET
    public List<ProviderDto> getProviders() {
        return Lists.transform(dao.listAll(), ProviderDto.fromProvider());
    }

    @GET
    @Path("/{id}")
    public ProviderDto getProvider(@PathParam("id") int id) {
        Provider provider = dao.get(id);
        if (provider == null) {
            throw new NotFoundException();
        } else {
            return ProviderDto.fromProvider().apply(provider);
        }
    }

    @PUT
    @Path("/{id}")
    public void createUpdateProvider(@PathParam("id") int id,
            @Valid ProviderDto dto) {
        dao.saveOrUpdate(new Provider(id, dto.getName()));
    }

    @POST
    public Response createProvider(@Valid ProviderDto dto) {
        Provider provider = new Provider(dto.getName());
        dao.save(provider);
        URI uri = UriBuilder.fromResource(ProviderResource.class).path("{id}")
                .build(provider.getId());
        return Response.created(uri)
                .entity(ProviderDto.fromProvider().apply(provider)).build();
    }
}
