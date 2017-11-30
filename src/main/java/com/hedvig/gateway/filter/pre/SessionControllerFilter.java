package com.hedvig.gateway.filter.pre;

import com.hedvig.gateway.NotLoggedInException;
import com.hedvig.gateway.enteties.AuthorizationRow;
import com.hedvig.gateway.enteties.AuthorizationRowRepository;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

import static net.logstash.logback.argument.StructuredArguments.value;

public class SessionControllerFilter extends ZuulFilter {

	private static Logger log = LoggerFactory.getLogger(SessionControllerFilter.class);
    private static final String HEADER="hedvig.token";
    private final AuthorizationRowRepository authorizationRowRepository;

    public SessionControllerFilter(AuthorizationRowRepository authorizationRowRepository) {
        this.authorizationRowRepository = authorizationRowRepository;
    }

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



        AuthorizationRow hid;
        String jwt = "";
        try {
            jwt = getJwtToken(request);
			hid = authorizationRowRepository.findOne(jwt);
			if(hid == null) {

			    throw new NotLoggedInException("Not logged in");
            }

		} catch (Throwable e) {
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
        ctx.addZuulRequestHeader(HEADER, hid.memberId);
        log.info("{} request to {} from memberId:{}", request.getMethod(), request.getRequestURL().toString(), value("memberId", hid.memberId));

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