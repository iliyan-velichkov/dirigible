package org.eclipse.dirigible.components.base.tenant;

/**
 * The Interface Tenant.
 */
public interface Tenant {

    /**
     * Gets the id.
     *
     * @return the id
     */
    String getId();

    /**
     * Checks if is default.
     *
     * @return true, if is default
     */
    boolean isDefault();

    /**
     * Gets the name.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets the subdomain.
     *
     * @return the subdomain
     */
    String getSubdomain();
}
