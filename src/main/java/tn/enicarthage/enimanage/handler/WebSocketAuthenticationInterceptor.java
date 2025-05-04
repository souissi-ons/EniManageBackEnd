package tn.enicarthage.enimanage.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import tn.enicarthage.enimanage.service.JwtService;

import java.util.Map;

@Slf4j
@Component
public class WebSocketAuthenticationInterceptor implements HandshakeInterceptor {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public WebSocketAuthenticationInterceptor(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info("Intercepting WebSocket handshake request");

        // Méthode 1: Extraire le token de l'URL ou des paramètres
        String query = ((ServletServerHttpRequest) request).getServletRequest().getQueryString();
        if (query != null && query.contains("token=")) {
            String token = query.substring(query.indexOf("token=") + 6);
            if (token.contains("&")) {
                token = token.substring(0, token.indexOf("&"));
            }

            return validateToken(token, attributes);
        }

        // Méthode 2: Extraire le token des en-têtes HTTP
        String header = request.getHeaders().getFirst("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            return validateToken(token, attributes);
        }

        log.warn("No authentication token found in WebSocket handshake");
        // Pour le développement, vous pouvez autoriser les connexions sans token
        // return true;

        // Pour la production, refusez les connexions sans token
        return false;
    }

    private boolean validateToken(String token, Map<String, Object> attributes) {
        try {
            String username = jwtService.extractUsername(token);
            log.info("Extracted username from token: {}", username);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(token, userDetails)) {
                // Stocker l'utilisateur dans les attributs pour référence dans le handler
                attributes.put("user", userDetails);
                attributes.put("username", username);
                log.info("Token validated successfully for user: {}", username);
                return true;
            }

            log.warn("Invalid token for user: {}", username);
            return false;
        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // Cette méthode est appelée après que la poignée de main soit terminée
        if (exception != null) {
            log.error("Exception during handshake: {}", exception.getMessage(), exception);
        }
    }
}