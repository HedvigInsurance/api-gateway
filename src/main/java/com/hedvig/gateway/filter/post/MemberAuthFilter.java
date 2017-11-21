package com.hedvig.gateway.filter.post;

import com.hedvig.gateway.enteties.AuthorizationRow;
import com.hedvig.gateway.enteties.AuthorizationRowRepository;
import com.hedvig.gateway.filter.pre.SessionControllerFilter;
import com.netflix.util.Pair;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class MemberAuthFilter extends ZuulFilter {

    private static Logger log = LoggerFactory.getLogger(MemberAuthFilter.class);
    private final AuthorizationRowRepository repo;

    public MemberAuthFilter(AuthorizationRowRepository repo) {
        this.repo = repo;
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
        return requestUri.startsWith("/hedvig/collect");
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        List<Pair<String, String>> headers = ctx.getZuulResponseHeaders();

        Optional<String> userId = headers.stream().filter(p -> Objects.equals(p.first(), "Hedvig.Id")).map(Pair::second).findFirst();
        userId.ifPresent(uid -> {
            String jwt = null;

            jwt = SessionControllerFilter.getJwtToken(request);

            log.info("Updating sessionMap(%s,%s)", jwt, uid);
            AuthorizationRow authRow = repo.findOne(jwt);
            authRow.memberId = uid;
            repo.save(authRow);

        });

        List<Pair<String, String>> filteredHeaders = headers.stream().filter(p -> !Objects.equals(p.first(), "Hedvig.Id")).collect(Collectors.toList());
        ctx.put("zuulResponseHeaders", filteredHeaders);

        return null;
    }
}
