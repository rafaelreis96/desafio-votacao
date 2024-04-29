package dev.rafaelreis.desafiovotacao.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

import java.util.Locale;

/**
 * Esta classe contém utilitários para testes
 */
public class TestUtils {

    public static Faker fakerBR() {
        return Faker.instance(new Locale("pt-BR"));
    }

    public static String toJson(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao converter objeto para JSON: " + e.getMessage());
        }
    }

    public static <T> T toObject(String json, Class<T> t) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, t);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao converter objeto para JSON: " + e.getMessage());
        }
    }

}
