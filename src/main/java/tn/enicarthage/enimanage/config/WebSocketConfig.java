package tn.enicarthage.enimanage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import tn.enicarthage.enimanage.handler.ChatWebSocketHandler;
import tn.enicarthage.enimanage.handler.WebSocketAuthenticationInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketHandler chatWebSocketHandler;
    private final WebSocketAuthenticationInterceptor webSocketAuthenticationInterceptor;

    public WebSocketConfig(ChatWebSocketHandler chatWebSocketHandler,
                           WebSocketAuthenticationInterceptor webSocketAuthenticationInterceptor) {
        this.chatWebSocketHandler = chatWebSocketHandler;
        this.webSocketAuthenticationInterceptor = webSocketAuthenticationInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler, "/ws/chat")
                .addInterceptors(webSocketAuthenticationInterceptor)
                .setAllowedOrigins("http://localhost:4200");
        // Removed .withSockJS() for native WebSocket support
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);
        container.setMaxSessionIdleTimeout(3600000L);
        return container;
    }
}