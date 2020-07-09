package com.yx.grpc.client.nameresolver;

import com.google.common.collect.ImmutableList;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;

import java.util.Collection;

import static java.util.Objects.requireNonNull;

/**
 * A {@link NameResolver} that will always respond with a static set of target addresses.
 * @Auther: shiyongxi
 * @Date: 2020-03-23 16:38
 */
public class StaticNameResolver extends NameResolver {

    private final String authority;
    private final ResolutionResult result;

    /**
     * Creates a static name resolver with only a single target server.
     *
     * @param authority The authority this name resolver was created for.
     * @param target The target address of the server to use.
     */
    public StaticNameResolver(final String authority, final EquivalentAddressGroup target) {
        this(authority, ImmutableList.of(requireNonNull(target, "target")));
    }

    /**
     * Creates a static name resolver with multiple target servers.
     *
     * @param authority The authority this name resolver was created for.
     * @param targets The target addresses of the servers to use.
     */
    public StaticNameResolver(final String authority, final Collection<EquivalentAddressGroup> targets) {
        this.authority = requireNonNull(authority, "authority");
        if (requireNonNull(targets, "targets").isEmpty()) {
            throw new IllegalArgumentException("Must have at least one target");
        }
        this.result = ResolutionResult.newBuilder()
                .setAddresses(ImmutableList.copyOf(targets))
                .build();
    }

    /**
     * Creates a static name resolver with multiple target servers.
     *
     * @param authority The authority this name resolver was created for.
     * @param result The resolution result to use..
     */
    public StaticNameResolver(final String authority, final ResolutionResult result) {
        this.authority = requireNonNull(authority, "authority");
        this.result = requireNonNull(result, "result");
    }

    @Override
    public String getServiceAuthority() {
        return this.authority;
    }

    @Override
    public void start(final Listener2 listener) {
        listener.onResult(this.result);
    }

    @Override
    public void refresh() {
        // Does nothing
    }

    @Override
    public void shutdown() {
        // Does nothing
    }

    @Override
    public String toString() {
        return "StaticNameResolver [authority=" + this.authority + ", result=" + this.result + "]";
    }

}
