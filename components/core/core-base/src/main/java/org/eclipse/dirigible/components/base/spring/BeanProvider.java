package org.eclipse.dirigible.components.base.spring;

import org.eclipse.dirigible.components.base.tenant.TenantContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class BeanProvider implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext argApplicationContext) throws BeansException {
        context = argApplicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        if (!isInitialzed()) {
            throw new IllegalStateException("Spring is not initialized yet.");
        }
        return context.getBean(clazz);
    }

    public static TenantContext getTenantContext() {
        return getBean(TenantContext.class);
    }

    public static boolean isInitialzed() {
        return context != null;
    }

}
