package com.yx.grpc.client.security;

import com.yx.grpc.client.inject.StubTransformer;
import io.grpc.CallCredentials;
import io.grpc.Metadata;
import io.grpc.SecurityLevel;
import io.grpc.Status;
import io.grpc.stub.AbstractStub;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.Executor;

import static com.yx.grpc.common.security.SecurityConstants.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-17 18:04
 * @Description: CallCredentialsHelper
 */
public class CallCredentialsHelper {
    /**
     * Creates a new {@link StubTransformer} that will assign the given credentials to the given {@link AbstractStub}.
     *
     * @param credentials The call credentials to assign.
     * @return The transformed stub.
     * @see AbstractStub#withCallCredentials(CallCredentials)
     */
    public static StubTransformer fixedCredentialsStubTransformer(final CallCredentials credentials) {
        requireNonNull(credentials, "credentials");
        return (name, stub) -> stub.withCallCredentials(credentials);
    }

    /**
     * Creates a new {@link StubTransformer} that will assign credentials to the given {@link AbstractStub} based on the
     * name. If the given map does not contain a value for the given name, then the call credentials will be omitted.
     *
     * @param credentialsByName The map that contains the call credentials.
     * @return The transformed stub.
     * @see #mappedCredentialsStubTransformer(Map, CallCredentials)
     * @see AbstractStub#withCallCredentials(CallCredentials)
     */
    public static StubTransformer mappedCredentialsStubTransformer(
            final Map<String, CallCredentials> credentialsByName) {
        return mappedCredentialsStubTransformer(credentialsByName, null);
    }

    /**
     * Creates a new {@link StubTransformer} that will assign credentials to the given {@link AbstractStub} based on the
     * name. If the given map does not contain a value for the given name, then the optional fallback will be used
     * otherwise the call credentials will be omitted.
     *
     * @param credentialsByName The map that contains the call credentials.
     * @param fallback The optional fallback to use.
     * @return The transformed stub.
     * @see AbstractStub#withCallCredentials(CallCredentials)
     */
    public static StubTransformer mappedCredentialsStubTransformer(
            final Map<String, CallCredentials> credentialsByName,
            @Nullable final CallCredentials fallback) {
        requireNonNull(credentialsByName, "credentials");
        return (name, stub) -> {
            final CallCredentials credentials = credentialsByName.getOrDefault(name, fallback);
            if (credentials == null) {
                return stub;
            } else {
                return stub.withCallCredentials(credentials);
            }
        };
    }

    /**
     * Creates a new call credential with the given token for bearer auth.
     *
     * <p>
     * <b>Note:</b> This method uses experimental grpc-java-API features.
     * </p>
     *
     * @param token the bearer token to use
     * @return The newly created bearer auth credentials.
     */
    public static CallCredentials bearerAuth(final String token) {
        final Metadata extraHeaders = new Metadata();
        extraHeaders.put(AUTHORIZATION_HEADER, BEARER_AUTH_PREFIX + token);
        return new StaticSecurityHeaderCallCredentials(extraHeaders);
    }

    /**
     * Creates a new call credential with the given username and password for basic auth.
     *
     * <p>
     * <b>Note:</b> This method uses experimental grpc-java-API features.
     * </p>
     *
     * @param username The username to use.
     * @param password The password to use.
     * @return The newly created basic auth credentials.
     */
    public static CallCredentials basicAuth(final String username, final String password) {
        final Metadata extraHeaders = new Metadata();
        extraHeaders.put(AUTHORIZATION_HEADER, encodeBasicAuth(username, password));
        return new StaticSecurityHeaderCallCredentials(extraHeaders);
    }

    /**
     * Encodes the given username and password as basic auth. The header value will be encoded with
     * {@link StandardCharsets#UTF_8 UTF_8}.
     *
     * @param username The username to use.
     * @param password The password to use.
     * @return The encoded basic auth header value.
     */
    public static String encodeBasicAuth(final String username, final String password) {
        requireNonNull(username, "username");
        requireNonNull(password, "password");
        final String auth = username + ':' + password;
        byte[] encoded;
        try {
            encoded = Base64.getEncoder().encode(auth.getBytes(UTF_8));
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to encode basic authentication token", e);
        }
        return BASIC_AUTH_PREFIX + new String(encoded, UTF_8);
    }

    /**
     * The static security header {@link CallCredentials} simply add a set of predefined headers to the call. Their
     * specific meaning is server specific. This implementation can be used, for example, for BasicAuth.
     */
    private static final class StaticSecurityHeaderCallCredentials extends CallCredentials {

        private final Metadata extraHeaders;

        StaticSecurityHeaderCallCredentials(final Metadata extraHeaders) {
            this.extraHeaders = requireNonNull(extraHeaders, "extraHeaders");
        }

        @Override
        public void applyRequestMetadata(final RequestInfo requestInfo, final Executor appExecutor,
                                         final MetadataApplier applier) {
            applier.apply(this.extraHeaders);
        }

        @Override
        public void thisUsesUnstableApi() {} // API evolution in progress

        @Override
        public String toString() {
            return "StaticSecurityHeaderCallCredentials [extraHeaders=" + this.extraHeaders + "]";
        }

    }

    /**
     * Checks whether the given security level provides privacy for all data being send on the connection.
     *
     * <p>
     * <b>Note:</b> This method uses experimental grpc-java-API features.
     * </p>
     *
     * @param securityLevel The security level to check.
     * @return True, if and only if the given security level ensures privacy. False otherwise.
     */
    public static boolean isPrivacyGuaranteed(final SecurityLevel securityLevel) {
        return SecurityLevel.PRIVACY_AND_INTEGRITY == securityLevel;
    }

    /**
     * Wraps the given call credentials in a new layer, which ensures that the credentials are only send, if the
     * connection guarantees privacy. If the connection doesn't do that, the call will be aborted before sending any
     * data.
     *
     * <p>
     * <b>Note:</b> This method uses experimental grpc-java-API features.
     * </p>
     *
     * @param callCredentials The call credentials to wrap.
     * @return The newly created call credentials.
     */
    public static CallCredentials requirePrivacy(final CallCredentials callCredentials) {
        return new RequirePrivacyCallCredentials(callCredentials);
    }

    /**
     * A call credentials implementation with slightly increased security requirements. It ensures that the credentials
     * aren't send via an insecure connection. However, it does not prevent requests via insecure connections. This
     * wrapper does not have any other influence on the security of the underlying {@link CallCredentials}
     * implementation.
     */
    private static final class RequirePrivacyCallCredentials extends CallCredentials {

        private static final Status STATUS_LACKING_PRIVACY = Status.UNAUTHENTICATED
                .withDescription("Connection security level does not ensure credential privacy");

        private final CallCredentials callCredentials;

        RequirePrivacyCallCredentials(final CallCredentials callCredentials) {
            this.callCredentials = callCredentials;
        }

        @Override
        public void applyRequestMetadata(final RequestInfo requestInfo, final Executor appExecutor,
                                         final MetadataApplier applier) {
            if (isPrivacyGuaranteed(requestInfo.getSecurityLevel())) {
                this.callCredentials.applyRequestMetadata(requestInfo, appExecutor, applier);
            } else {
                applier.fail(STATUS_LACKING_PRIVACY);
            }
        }

        @Override
        public void thisUsesUnstableApi() {} // API evolution in progress

        @Override
        public String toString() {
            return "RequirePrivacyCallCredentials [callCredentials=" + this.callCredentials + "]";
        }

    }

    /**
     * Wraps the given call credentials in a new layer, that will only include the credentials if the connection
     * guarantees privacy. If the connection doesn't do that, the call will continue without the credentials.
     *
     * <p>
     * <b>Note:</b> This method uses experimental grpc-java-API features.
     * </p>
     *
     * @param callCredentials The call credentials to wrap.
     * @return The newly created call credentials.
     */
    public static CallCredentials includeWhenPrivate(final CallCredentials callCredentials) {
        return new IncludeWhenPrivateCallCredentials(callCredentials);
    }

    /**
     * A call credentials implementation with increased security requirements. It ensures that the credentials and
     * requests aren't send via an insecure connection. This wrapper does not have any other influence on the security
     * of the underlying {@link CallCredentials} implementation.
     */
    private static final class IncludeWhenPrivateCallCredentials extends CallCredentials {

        private final CallCredentials callCredentials;

        IncludeWhenPrivateCallCredentials(final CallCredentials callCredentials) {
            this.callCredentials = callCredentials;
        }

        @Override
        public void applyRequestMetadata(final RequestInfo requestInfo, final Executor appExecutor,
                                         final MetadataApplier applier) {
            if (isPrivacyGuaranteed(requestInfo.getSecurityLevel())) {
                this.callCredentials.applyRequestMetadata(requestInfo, appExecutor, applier);
            }
        }

        @Override
        public void thisUsesUnstableApi() {} // API evolution in progress

        @Override
        public String toString() {
            return "IncludeWhenPrivateCallCredentials [callCredentials=" + this.callCredentials + "]";
        }

    }

    private CallCredentialsHelper() {}
}
