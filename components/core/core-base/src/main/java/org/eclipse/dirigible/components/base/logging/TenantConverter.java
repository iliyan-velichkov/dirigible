package org.eclipse.dirigible.components.base.logging;

import org.eclipse.dirigible.components.base.spring.BeanProvider;
import org.eclipse.dirigible.components.base.tenant.TenantContext;
import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class TenantConverter extends ClassicConverter {

    private static final String BACKGROUND_TENANT_VALUE = "background";
    private TenantContext tenantContext;

    @Override
    public String convert(ILoggingEvent event) {
        TenantContext ctx = getTenantContext();
        if (null == ctx) {
            return BACKGROUND_TENANT_VALUE;
        }
        return ctx.isInitialized() ? ctx.getCurrentTenant()
                                        .getId()
                : BACKGROUND_TENANT_VALUE;
    }

    private TenantContext getTenantContext() {
        if (null != tenantContext) {
            return tenantContext;
        }

        tenantContext = BeanProvider.isInitialzed() ? BeanProvider.getTenantContext() : null;
        return tenantContext;
    }

}
