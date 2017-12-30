package net.manirai.rental.provider;

import java.util.Objects;

import org.hibernate.validator.constraints.NotBlank;

import com.google.common.base.Function;

/**
 * 
 * @author Mani
 *
 */
public class ProviderDto {
    private Integer providerId;

    @NotBlank
    private String name;

    public ProviderDto() {
    }

    public ProviderDto(Integer id, String name) {
        this.providerId = id;
        this.name = name;
    }

    public ProviderDto(String name) {
        this.name = name;
    }

    public Integer getId() {
        return providerId;
    }

    public void setId(Integer id) {
        this.providerId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(providerId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ProviderDto)) {
            return false;
        }
        ProviderDto other = (ProviderDto) obj;
        return Objects.equals(providerId, other.providerId)
                && Objects.equals(name, other.name);
    }

    @Override
    public String toString() {
        return "Provider " + providerId + " / " + name;
    }

    public static Function<Provider, ProviderDto> fromProvider() {
        return new Function<Provider, ProviderDto>() {
            @Override
            public ProviderDto apply(Provider provider) {
                return new ProviderDto(provider.getId(), provider.getName());
            }
        };
    }

}
