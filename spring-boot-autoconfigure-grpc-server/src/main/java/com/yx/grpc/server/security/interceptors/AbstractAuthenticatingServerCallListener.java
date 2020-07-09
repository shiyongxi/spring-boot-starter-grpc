package com.yx.grpc.server.security.interceptors;

import io.grpc.Context;
import io.grpc.ForwardingServerCallListener;
import io.grpc.ServerCall;
import lombok.extern.slf4j.Slf4j;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-19 16:31
 * @Description: AbstractAuthenticatingServerCallListener
 */
@Slf4j
public abstract class AbstractAuthenticatingServerCallListener<ReqT> extends ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT> {

    private final Context context;

    /**
     * Creates a new AbstractAuthenticatingServerCallListener which will attach the given security context before
     * delegating to the given listener.
     *
     * @param delegate The listener to delegate to.
     * @param context The context to attach.
     */
    protected AbstractAuthenticatingServerCallListener(final ServerCall.Listener<ReqT> delegate, final Context context) {
        super(delegate);
        this.context = context;
    }

    /**
     * Gets the {@link Context} associated with the call.
     *
     * @return The context of the current call.
     */
    protected final Context context() {
        return this.context;
    }

    /**
     * Attaches the authentication context before the actual call.
     *
     * <p>
     * This method is called after the grpc context is attached.
     * </p>
     */
    protected abstract void attachAuthenticationContext();

    /**
     * Detaches the authentication context after the actual call.
     *
     * <p>
     * This method is called before the grpc context is detached.
     * </p>
     */
    protected abstract void detachAuthenticationContext();

    @Override
    public void onMessage(final ReqT message) {
        final Context previous = this.context.attach();
        try {
            attachAuthenticationContext();
            log.debug("onMessage - Authentication set");
            super.onMessage(message);
        } finally {
            detachAuthenticationContext();
            this.context.detach(previous);
            log.debug("onMessage - Authentication cleared");
        }
    }

    @Override
    public void onHalfClose() {
        final Context previous = this.context.attach();
        try {
            attachAuthenticationContext();
            log.debug("onHalfClose - Authentication set");
            super.onHalfClose();
        } finally {
            detachAuthenticationContext();
            this.context.detach(previous);
            log.debug("onHalfClose - Authentication cleared");
        }
    }

    @Override
    public void onCancel() {
        final Context previous = this.context.attach();
        try {
            attachAuthenticationContext();
            log.debug("onCancel - Authentication set");
            super.onCancel();
        } finally {
            detachAuthenticationContext();
            log.debug("onCancel - Authentication cleared");
            this.context.detach(previous);
        }
    }

    @Override
    public void onComplete() {
        final Context previous = this.context.attach();
        try {
            attachAuthenticationContext();
            log.debug("onComplete - Authentication set");
            super.onComplete();
        } finally {
            detachAuthenticationContext();
            log.debug("onComplete - Authentication cleared");
            this.context.detach(previous);
        }
    }

    @Override
    public void onReady() {
        final Context previous = this.context.attach();
        try {
            attachAuthenticationContext();
            log.debug("onReady - Authentication set");
            super.onReady();
        } finally {
            detachAuthenticationContext();
            log.debug("onReady - Authentication cleared");
            this.context.detach(previous);
        }
    }

}
