/*
 * Backend "Elektronischer Münzwurf"
 * Copyright (C) 2025 Institut für Arbeitsmarkt und Berufsforschung <https://www.iab.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.arbeitsagentur.iab.emu.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Component
public class TenantIdInterceptor implements HandlerInterceptor  {
	
    private final Logger logger = LoggerFactory.getLogger(TenantIdInterceptor.class);
	
	private final TenantResolver tenantResolver;


	public TenantIdInterceptor(TenantResolver tenantResolver) {
		this.tenantResolver = tenantResolver;
	}

	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    	@SuppressWarnings("rawtypes")
		final Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    	if (pathVariables != null) {
	    	Object tenantId = pathVariables.get("tenantId");
	    	if (tenantId != null) {
	    		tenantResolver.setCurrentTenant(tenantId.toString());
	    	
	    		logger.debug("Tenant id: {}", tenantId);
	    	}
    	}
    	
        return true;
    }

   
}