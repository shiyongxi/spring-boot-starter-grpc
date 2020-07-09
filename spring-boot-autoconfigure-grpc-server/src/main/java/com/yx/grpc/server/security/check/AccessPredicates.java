package com.yx.grpc.server.security.check;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;

import java.util.function.Predicate;

/**
 * Helper class that contains some internal constants for {@link AccessPredicate}s.
 *
 * @Auther: shiyongxi
 * @Date: 2020-03-19 16:35
 * @Description: GrpcSecurityMetadataSource
 * */
final class AccessPredicates {

    /**
     * A marker constant that indicates that all restrictions should be disabled. This instance should never be
     * executed, mutated or used in mutation. It should only be used in {@code ==} comparisons.
     */
    static final AccessPredicate PERMIT_ALL = new AccessPredicate() {

        /**
         * @deprecated Should never be called
         */
        @Override
        @Deprecated // Should never be called
        public boolean test(final Authentication t) {
            throw new InternalAuthenticationServiceException(
                    "Tried to execute the 'permit-all' access predicate. The server's security configuration is broken.");
        }

        /**
         * @deprecated Should never be called
         */
        @Override
        @Deprecated // Should never be called
        public AccessPredicate and(final Predicate<? super Authentication> other) {
            throw new UnsupportedOperationException("Not allowed for 'permit-all' access predicate");
        }

        /**
         * @deprecated Should never be called
         */
        @Override
        @Deprecated // Should never be called
        public AccessPredicate or(final Predicate<? super Authentication> other) {
            throw new UnsupportedOperationException("Not allowed for 'permit-all' access predicate");
        }

        /**
         * @deprecated Should never be called
         */
        @Override
        @Deprecated // Should never be called
        public AccessPredicate negate() {
            throw new UnsupportedOperationException("Not allowed for 'permit-all' access predicate");
        }

    };

    private AccessPredicates() {}
}
