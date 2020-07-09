package com.yx.grpc.client.nameresolver;

import com.google.common.collect.ImmutableList;
import io.grpc.NameResolverProvider;
import io.grpc.NameResolverRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-17 16:34
 * @Description: NameResolverRegistration
 */
@Slf4j
public class NameResolverRegistration implements DisposableBean {
    private final List<NameResolverRegistry> registries = new ArrayList<>(1);
    private final List<NameResolverProvider> providers;

    /**
     * Creates a new NameResolverRegistration with the given list of providers.
     *
     * @param providers The providers that should be managed.
     */
    public NameResolverRegistration(List<NameResolverProvider> providers) {
        this.providers = providers == null ? ImmutableList.of() : ImmutableList.copyOf(providers);
    }

    /**
     * Register all NameResolverProviders in the given registry and store a reference to it for later de-registration.
     *
     * @param registry The registry to add the providers to.
     */
    public void register(NameResolverRegistry registry) {
        this.registries.add(registry);
        for (NameResolverProvider provider : this.providers) {
            try {
                registry.register(provider);
                log.info("{} is available -> Added to the NameResolverRegistry", provider);
            } catch (IllegalArgumentException e) {
                log.info("{} is not available -> Not added to the NameResolverRegistry", provider);
            }
        }
    }

    @Override
    public void destroy() {
        for (NameResolverRegistry registry : this.registries) {
            for (NameResolverProvider provider : this.providers) {
                registry.deregister(provider);
                log.info("{} was removed from the NameResolverRegistry", provider);
            }
        }
        this.registries.clear();
    }
}
