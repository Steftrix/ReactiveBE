package org.mastersdbis.mtsdreactive.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Reactive JWT filter.
 *
 * Flow:
 *  1. Extract Bearer token from Authorization header
 *  2. Quick validity check (signature + expiry) — no DB call yet
 *  3. Load UserDetails reactively from DB
 *  4. Build Authentication and inject into ReactiveSecurityContext
 *  5. Continue filter chain
 *
 * If no token or invalid token → continue without authentication.
 * Spring Security will then apply the access rules defined in SecurityConfig.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationWebFilter implements WebFilter {

    private final JwtUtil jwtUtil;
    private final ReactiveUserDetailsServiceImpl userDetailsService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);

        // Quick structural check before hitting the database
        if (!jwtUtil.isTokenValid(token)) {
            return chain.filter(exchange);
        }

        String username;
        try {
            username = jwtUtil.extractUsername(token);
        } catch (Exception e) {
            return chain.filter(exchange);
        }

        return userDetailsService.findByUsername(username)
                .filter(userDetails -> jwtUtil.validateToken(token, userDetails))
                .map(userDetails -> new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                ))
                .flatMap(auth ->
                        chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
                )
                .switchIfEmpty(chain.filter(exchange));
    }
}