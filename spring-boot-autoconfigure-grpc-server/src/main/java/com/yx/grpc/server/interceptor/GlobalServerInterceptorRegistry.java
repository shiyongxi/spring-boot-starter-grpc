package com.yx.grpc.server.interceptor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.grpc.ServerInterceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-19 11:56
 * @Description: GlobalServerInterceptorRegistry
 */
public class GlobalServerInterceptorRegistry implements ApplicationContextAware {
    private final List<ServerInterceptor> serverInterceptors = Lists.newArrayList();
    private ImmutableList<ServerInterceptor> sortedServerInterceptors;
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        final Map<String, GlobalServerInterceptorConfigurer> map =
                this.applicationContext.getBeansOfType(GlobalServerInterceptorConfigurer.class);
        for (final GlobalServerInterceptorConfigurer globalServerInterceptorConfigurerAdapter : map.values()) {
            globalServerInterceptorConfigurerAdapter.addServerInterceptors(this);
        }
    }

    /**
     * Adds the given {@link ServerInterceptor} to the list of globally registered interceptors.
     *
     * @param interceptor The interceptor to add.
     * @return This instance for chaining.
     */
    public GlobalServerInterceptorRegistry addServerInterceptors(final ServerInterceptor interceptor) {
        this.sortedServerInterceptors = null;
        this.serverInterceptors.add(interceptor);
        return this;
    }

    /**
     * Gets the immutable and sorted list of global server interceptors.
     *
     * @return The list of globally registered server interceptors.
     */
    public ImmutableList<ServerInterceptor> getServerInterceptors() {
        if (this.sortedServerInterceptors == null) {
            List<ServerInterceptor> temp = Lists.newArrayList(this.serverInterceptors);
            sortInterceptors(temp);
            this.sortedServerInterceptors = ImmutableList.copyOf(temp);
        }
        return this.sortedServerInterceptors;
    }

    /**
     * Sorts the given list of interceptors. Use this method if you want to sort custom interceptors. The default
     * implementation will sort them by using then {@link AnnotationAwareOrderComparator}.
     *
     * @param interceptors The interceptors to sort.
     */
    public void sortInterceptors(List<? extends ServerInterceptor> interceptors) {
        interceptors.sort(AnnotationAwareOrderComparator.INSTANCE);
    }
}
