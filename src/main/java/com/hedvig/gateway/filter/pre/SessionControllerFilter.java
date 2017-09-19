package com.hedvig.gateway.filter.pre;

import com.hedvig.gateway.GatewayApplication;
import com.hedvig.gateway.HedvigToken;
import com.hedvig.gateway.NotLoggedInException;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class SessionControllerFilter extends ZuulFilter {

	private static Logger log = LoggerFactory.getLogger(SessionControllerFilter.class);
    private static final String HEADER="hedvig.token";

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        
        HttpServletRequest request = ctx.getRequest();
        HttpSession httpSession = request.getSession();
        UUID uid = (UUID) httpSession.getAttribute(GatewayApplication.HEDVIG_SESSION);
        HedvigToken hid = null;
        try {
			hid = GatewayApplication.getToken(uid);
		} catch (NotLoggedInException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
			ctx.unset();
			ctx.setResponseBody(e.getMessage());
			ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
			return null;
		}
        log.info("read this?");
        ctx.addZuulRequestHeader(HEADER, hid.toString());
        log.info(String.format("%s request to %s hedvig.session:%s", request.getMethod(), request.getRequestURL().toString(),uid.toString()));

        return null;
    }
}