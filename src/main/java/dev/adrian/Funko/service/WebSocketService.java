package dev.adrian.Funko.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final WebSocketHandler webSocketHandler;

    public void sendMessage(String message) {
        log.debug("WebSocketService -> Enviando mensaje a los clientes: {}", message);
        webSocketHandler.sendMessage(message);
    }
}