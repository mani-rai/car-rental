package net.manirai.rental.provider;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import net.manirai.rental.test.AbstractIT;

/**
 * 
 * @author Mani
 *
 */
@Test
public class ProviderResourceIT extends AbstractIT {

    private ProviderApi providerApi;

    @Override
    @BeforeMethod
    public void setUp() {
        super.setUp();

        this.providerApi = new ProviderApi(api);
    }

    @Test
    public void shouldReturnEmptyListWhenNoProviders() {
        assertThat(getProviders(), is(empty()));
    }

    @Test
    public void shouldReturn404WhenProviderDoesntExist() {
        assertThat(getProvider(15).getStatus(), is(NOT_FOUND.getStatusCode()));
    }

    @Test
    public void shouldReturnProviderAfterCreation() {
        ProviderDto provider = createProvider("Jimmy");
        Response resp = getProvider(provider.getId());
        assertThat(resp.getStatus(), is(OK.getStatusCode()));
        assertThat(resp.readEntity(ProviderDto.class), is(provider));
    }

    @Test
    public void shouldCreateProvidersWithPost() {
        postProvider("Jimmy");
        postProvider("Timmy");

        assertThat(getNames(getProviders()),
                containsInAnyOrder("Jimmy", "Timmy"));
    }

    @Test
    public void shouldCreateAndUpdateProviderWithPostAndPut() {
        ProviderDto dto = createProvider("Jimmy");

        assertThat(getProviders(), contains(dto));

        putProvider(dto.getId(), "James");

        assertThat(getProviders(), contains(provider(dto.getId(), "James")));
    }

    @Test
    public void shouldRequireNameForProvider() {
        assertThat(putProvider(1, null).getStatus(),
                is(Status.BAD_REQUEST.getStatusCode()));
    }

    private Iterable<String> getNames(List<ProviderDto> providers) {
        return Iterables.transform(providers,
                new Function<ProviderDto, String>() {
                    @Override
                    public String apply(ProviderDto dto) {
                        return dto.getName();
                    }
                });
    }

    protected ProviderDto provider(int id, String name) {
        return new ProviderDto(id, name);
    }

    public Response postProvider(String name) {
        return providerApi.postProvider(name);
    }

    public ProviderDto createProvider(String name) {
        return providerApi.createProvider(name);
    }

    public Response putProvider(int id, String name) {
        return providerApi.putProvider(id, name);
    }

    public List<ProviderDto> getProviders() {
        return providerApi.getProviders();
    }

    public Response getProvider(int id) {
        return providerApi.getProvider(id);
    }

}
