package net.manirai.rental.client;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
@Path("/clients")
@Produces(APPLICATION_JSON)
public class ClientResource {

    @Inject
    private ClientDao clientDao;
    
    @GET
    public List<ClientDto> getClients() {
        return Lists.transform(this.clientDao.listAll(), ClientDto.fromClient());
    }
    
    @POST
    public Response createClient(@Valid ClientDto dto) {
        Client client = new Client(dto.getName());
        this.clientDao.save(client);
        URI uri = UriBuilder.fromResource(ClientResource.class).path("{id}").build(client.getId());
        return Response.created(uri).entity(ClientDto.fromClient().apply(client)).build();
    }
}
