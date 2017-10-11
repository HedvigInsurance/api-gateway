package com.hedvig.gateway.filter.post;

import com.hedvig.gateway.GatewayApplication;
import com.hedvig.gateway.HedvigToken;
import com.hedvig.gateway.RedirectController;
import com.hedvig.gateway.filter.pre.SessionControllerFilter;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.util.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class MemberAuthFilter extends ZuulFilter {

    private static Logger log = LoggerFactory.getLogger(MemberAuthFilter.class);

    public MemberAuthFilter() {
        log.info("MemberAuthFilter created!");
    }

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 5;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String requestUri = request.getRequestURI();
        return requestUri.startsWith("/member/bankid/collect");
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        List<Pair<String, String>> headers = ctx.getZuulResponseHeaders();

        Optional<String> userId = headers.stream().filter(p -> Objects.equals(p.first(), "Hedvig.Id")).map(Pair::second).findFirst();
        userId.ifPresent(uid -> {
            HedvigToken hid = new HedvigToken();
            hid.setToken(uid);

            String jwt = SessionControllerFilter.getJwtToken(request);
            log.info("Updating sessionMap(%s,%s)", jwt, hid.getToken());
            GatewayApplication.sessionMap.put(jwt, hid);
        });

        List<Pair<String, String>> filteredHeaders = headers.stream().filter(p -> !Objects.equals(p.first(), "Hedvig.Id")).collect(Collectors.toList());
        ctx.put("zuulResponseHeaders", filteredHeaders);

        return null;
    }
}
