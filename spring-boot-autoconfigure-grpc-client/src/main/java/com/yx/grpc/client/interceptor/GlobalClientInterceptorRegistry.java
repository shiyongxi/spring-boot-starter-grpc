package com.yx.grpc.client.interceptor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.grpc.ClientInterceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-17 10:25
 * @Description: GlobalClientInterceptorRegistry
 */
public class GlobalClientInterceptorRegistry implements ApplicationContextAware {
    private final List<ClientInterceptor> clientInterceptors = Lists.newArrayList();
    private ImmutableList<ClientInterceptor> sortedClientInterceptors;
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        final Map<String, GlobalClientInterceptorConfigurer> map =
                this.applicationContext.getBeansOfType(GlobalClientInterceptorConfigurer.class);
        for (final GlobalClientInterceptorConfigurer globalClientInterceptorConfigurer : map.values()) {
            globalClientInterceptorConfigurer.addClientInterceptors(this);
        }
    }

    /**
     * Adds the given {@link ClientInterceptor} to the list of globally registered interceptors.
     *
     * @param interceptor The interceptor to add.
     * @return This instance for chaining.
     */
    public GlobalClientInterceptorRegistry addClientInterceptors(final ClientInterceptor interceptor) {
        this.sortedClientInterceptors = null;
        this.clientInterceptors.add(interceptor);
        return this;
    }

    /**
     * Gets the immutable and sorted list of global server interceptors.
     *
     * @return The list of globally registered server interceptors.
     */
    public ImmutableList<ClientInterceptor> getClientInterceptors() {
        if (this.sortedClientInterceptors == null) {
            List<ClientInterceptor> temp = Lists.newArrayList(this.clientInterceptors);
            sortInterceptors(temp);
            this.sortedClientInterceptors = ImmutableList.copyOf(temp);
        }
        return this.sortedClientInterceptors;
    }

    /**
     * Sorts the given list of interceptors. Use this method if you want to sort custom interceptors. The default
     * implementation will sort them by using then {@link AnnotationAwareOrderComparator}.
     *
     * @param interceptors The interceptors to sort.
     */
    public void sortInterceptors(List<? extends ClientInterceptor> interceptors) {
        interceptors.sort(AnnotationAwareOrderComparator.INSTANCE);
    }
}
