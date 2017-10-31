package com.hedvig.gateway.filter.pre;

import com.hedvig.gateway.GatewayApplication;
import com.hedvig.gateway.HedvigToken;
import com.hedvig.gateway.NotLoggedInException;
import com.hedvig.gateway.RedirectController;
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
    public String filterType()
    {
        return "pre";
    }

    @Override
    public int filterOrder()
    {
        return 1;
    }

    @Override
    public boolean shouldFilter()
    {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String requestUri = request.getRequestURI();
        return !requestUri.startsWith("/asset/image") &&
                !requestUri.startsWith("/helloHedvig") &&
                !requestUri.startsWith("/claim/file/") &&
                !requestUri.startsWith("/insurance/contract");
    }

    @Override
    public Object run() {

        RequestContext ctx = RequestContext.getCurrentContext();

        HttpServletRequest request = ctx.getRequest();
        String jwt = getJwtToken(request);


        HedvigToken hid;
        try {
			hid = GatewayApplication.getToken(jwt);
		} catch (NotLoggedInException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
			log.info("jwt:" + jwt);
			
			/*
			 * Harsh response. Puts responsibility for login in on client
			 * */
			ctx.unset();
			ctx.setResponseBody(e.getMessage());
			ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
			
			/*
			 * Nice response. Server logs in user with new hedvig token.
			 *
			if (jwt == null) { uid = UUID.randomUUID(); }
			httpSession.setAttribute(GatewayApplication.HEDVIG_SESSION, uid);
			HedvigToken ht = RedirectController.login(uid);
			log.info("Set " + HEADER + " to " + ht.toString());
			ctx.addZuulRequestHeader(HEADER, ht.toString());*/
			return null;
		}
        log.info("read this?");
        ctx.addZuulRequestHeader(HEADER, hid.toString());
        log.info(String.format("%s request to %s with jwt:%s and userId:%s", request.getMethod(), request.getRequestURL().toString(),jwt, hid.getToken()));
       
        return null;
    }

    public static String getJwtToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        String jwt = null;
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
        }
        return jwt;
    }
}