package dev.adrian.Funko.service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Notificacion<T> {
    private String entidad;
    private String tipo;
    private T data;
    private String timestamp;
}