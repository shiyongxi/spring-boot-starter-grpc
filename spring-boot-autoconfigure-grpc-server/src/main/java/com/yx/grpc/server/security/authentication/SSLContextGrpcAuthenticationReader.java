package com.yx.grpc.server.security.authentication;

import io.grpc.Grpc;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;

import javax.annotation.Nullable;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

/**
 * An {@link GrpcAuthenticationReader} that will try to use the peer certificates to extract the client
 * {@link Authentication}. Currently this class only supports {@link X509Certificate}s.
 *
 * @Auther: shiyongxi
 * @Date: 2020-03-19 16:24
 * @Description: GrpcAuthenticationReader
 */
@Slf4j
public class SSLContextGrpcAuthenticationReader implements GrpcAuthenticationReader {

    @Override
    public Authentication readAuthentication(final ServerCall<?, ?> call, final Metadata metadata) {
        final SSLSession sslSession = call.getAttributes().get(Grpc.TRANSPORT_ATTR_SSL_SESSION);
        if (sslSession == null) {
            log.trace("Peer not verified via SSL");
            return null;
        }
        Certificate[] certs;
        try {
            certs = sslSession.getPeerCertificates();
        } catch (final SSLPeerUnverifiedException e) {
            log.trace("Peer not verified via certificate", e);
            return null;
        }
        return fromCertificate(certs[certs.length - 1]);
    }

    /**
     * Tries to prepare an {@link Authentication} using the given certificate.
     *
     * @param cert The certificate to use.
     * @return The authentication instance created with the certificate or null if the certificate type is unsupported.
     */
    @Nullable
    protected Authentication fromCertificate(final Certificate cert) {
        if (cert instanceof X509Certificate) {
            log.debug("Found X509 certificate");
            return new X509CertificateAuthentication((X509Certificate) cert);
        } else {
            log.debug("Unsupported certificate type: {}", cert.getType());
            return null;
        }
    }

}
