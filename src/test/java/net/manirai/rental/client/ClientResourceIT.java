package net.manirai.rental.client;

import static javax.ws.rs.core.Response.Status.CREATED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import javax.ws.rs.core.Response;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.manirai.rental.test.AbstractIT;

/**
 * 
 * @author Mani
 *
 */
@Test
public class ClientResourceIT extends AbstractIT {

    private ClientApi clientApi;

    @Override
    @BeforeMethod
    public void setUp() {
        super.setUp();

        this.clientApi = new ClientApi(api);
    }

    @Test
    public void shouldReturnEmptyListWhenNoClients() {
        assertThat(clientApi.getClients(), is(empty()));
    }

    @Test
    public void shouldReturnClientAfterCreation() {
        Response resp = clientApi.postClient("Jimmy");
        assertThat(resp.getStatus(), is(CREATED.getStatusCode()));

        ClientDto client = resp.readEntity(ClientDto.class);
        assertThat(client.getId(), is(notNullValue()));
        assertThat(client.getName(), is("Jimmy"));

        assertThat(clientApi.getClients(), contains(client));
    }
}
