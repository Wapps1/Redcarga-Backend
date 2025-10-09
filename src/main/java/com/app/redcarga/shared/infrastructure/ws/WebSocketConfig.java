package com.app.redcarga.shared.infrastructure.ws;

import com.app.redcarga.shared.ws.auth.MembershipVerifierPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.socket.config.annotation.*;

import java.util.Arrays;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${redcarga.ws.allowed-origins:*}")
    private String allowedOriginsCsv;

    @Value("${redcarga.ws.heartbeat.server-ms:10000}")
    private long heartbeatServerMs;

    @Value("${redcarga.ws.heartbeat.client-ms:10000}")
    private long heartbeatClientMs;

    @Value("${redcarga.ws.message-size.max-bytes:65536}")
    private int messageSizeLimit;

    @Value("${iam.jwt.issuer:}")
    private String iamIssuer;

    // === Solo dependencias “seguras” (no canales del broker) ===
    private final JwtDecoder jwtDecoder;
    private final MembershipVerifierPort membershipVerifierPort;
    private final ApplicationEventPublisher applicationEventPublisher;

    public WebSocketConfig(@Qualifier("iamJwtDecoder") JwtDecoder jwtDecoder,
                           MembershipVerifierPort membershipVerifierPort,
                           ApplicationEventPublisher applicationEventPublisher) {
        this.jwtDecoder = jwtDecoder;
        this.membershipVerifierPort = membershipVerifierPort;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(Arrays.stream(allowedOriginsCsv.split(","))
                        .map(String::trim).toArray(String[]::new))
                .addInterceptors(jwtHandshakeAuthInterceptor())
                .setHandshakeHandler(handshakeHandler());
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic", "/queue")
        .setHeartbeatValue(new long[]{heartbeatServerMs, heartbeatClientMs})
        .setTaskScheduler(wsMessageBrokerTaskScheduler());
        config.setUserDestinationPrefix("/user");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(messageSizeLimit);
        registry.setSendBufferSizeLimit(messageSizeLimit * 2);
        registry.setSendTimeLimit(30_000);
    }

    // Registra el interceptor creando la instancia aquí (sin @Bean y sin canales)
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Interceptor sin SimpMessagingTemplate para evitar ciclo de beans; listener separado enviará feedback
        registration.interceptors(new StompAuthChannelInterceptor(membershipVerifierPort, applicationEventPublisher));
    }

    // Exponer bean propio para el broker para evitar nombre que colisione con Spring
    @Bean(name = "wsMessageBrokerTaskScheduler")
    public ThreadPoolTaskScheduler wsMessageBrokerTaskScheduler() {
        ThreadPoolTaskScheduler ts = new ThreadPoolTaskScheduler();
        ts.setPoolSize(1);
        ts.setThreadNamePrefix("wss-broker-scheduler-");
        ts.initialize();
        return ts;
    }

    @Bean
    public PrincipalFromAttributesHandshakeHandler handshakeHandler() {
        return new PrincipalFromAttributesHandshakeHandler();
    }

    @Bean
    public JwtHandshakeAuthInterceptor jwtHandshakeAuthInterceptor() {
        return new JwtHandshakeAuthInterceptor(jwtDecoder(), iamIssuer);
    }

    @Bean
    public org.springframework.web.socket.messaging.StompSubProtocolErrorHandler stompSubProtocolErrorHandler() {
        return new StompErrorHandler();
    }

    private JwtDecoder jwtDecoder() { return this.jwtDecoder; }
}
