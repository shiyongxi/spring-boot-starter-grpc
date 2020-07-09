package com.yx.grpc.server.security.check;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

import java.util.Collection;

/**
 * An {@link AccessDecisionVoter} that checks for {@link AccessPredicateConfigAttribute}s.
 *
 * @Auther: shiyongxi
 * @Date: 2020-03-19 16:35
 * @Description: GrpcSecurityMetadataSource
 * */
public class AccessPredicateVoter implements AccessDecisionVoter<Object> {

    @Override
    public boolean supports(final ConfigAttribute attribute) {
        return attribute instanceof AccessPredicateConfigAttribute;
    }

    @Override
    public boolean supports(final Class<?> clazz) {
        return true;
    }

    @Override
    public int vote(final Authentication authentication, final Object object,
                    final Collection<ConfigAttribute> attributes) {
        final AccessPredicateConfigAttribute attr = find(attributes);
        if (attr == null) {
            return ACCESS_ABSTAIN;
        }
        final boolean allowed = attr.getAccessPredicate().test(authentication);
        return allowed ? ACCESS_GRANTED : ACCESS_DENIED;
    }

    /**
     * Finds the first AccessPredicateConfigAttribute in the given collection.
     *
     * @param attributes The attributes to search in.
     * @return The first found AccessPredicateConfigAttribute or null, if no such elements were found.
     */
    private AccessPredicateConfigAttribute find(final Collection<ConfigAttribute> attributes) {
        for (final ConfigAttribute attribute : attributes) {
            if (attribute instanceof AccessPredicateConfigAttribute) {
                return (AccessPredicateConfigAttribute) attribute;
            }
        }
        return null;
    }

}
