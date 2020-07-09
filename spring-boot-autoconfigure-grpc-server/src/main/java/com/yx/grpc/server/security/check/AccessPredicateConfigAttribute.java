package com.yx.grpc.server.security.check;

import org.springframework.security.access.ConfigAttribute;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * A {@link ConfigAttribute} which uses the embedded {@link AccessPredicate} for the decisions.
 *
 * @Auther: shiyongxi
 * @Date: 2020-03-19 16:35
 * @Description: GrpcSecurityMetadataSource
 */
public final class AccessPredicateConfigAttribute implements ConfigAttribute {

    private static final long serialVersionUID = 2906954441251029428L;

    private final AccessPredicate accessPredicate;

    /**
     * Creates a new AccessPredicateConfigAttribute with the given {@link AccessPredicate}.
     *
     * @param accessPredicate The access predicate to use.
     */
    public AccessPredicateConfigAttribute(final AccessPredicate accessPredicate) {
        this.accessPredicate = requireNonNull(accessPredicate, "accessPredicate");
    }

    /**
     * Gets the access predicate that belongs to this instance.
     *
     * @return The associated access predicate.
     */
    public AccessPredicate getAccessPredicate() {
        return this.accessPredicate;
    }

    @Override
    public String getAttribute() {
        return null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.accessPredicate);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AccessPredicateConfigAttribute other = (AccessPredicateConfigAttribute) obj;
        return Objects.equals(this.accessPredicate, other.accessPredicate);
    }

    @Override
    public String toString() {
        return "AccessPredicateConfigAttribute [" + this.accessPredicate + "]";
    }

}
