package org.example;

import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

public class Client {
    public static void main(String[] args) {
        final String sensorName = "Sensor 8";

        HttpStatus status = registerSensor(sensorName);
        if (status.value()!=400) {

            Random random = new Random();

            double maxTemperature = 45.0;   //min=0
            //посылаем n POST запросов с JSON с рандомными полями к определенному сенсору
            for (int i = 0; i < 100; i++) {
                System.out.println(i);
                sendMeasurement(random.nextDouble() * maxTemperature,
                        random.nextBoolean(), sensorName);
            }
        }
        //deleteSensor("Sensor 8");
    }

    private static HttpStatus registerSensor(String sensorName) {
        final String url = "http://localhost:8080/sensors/registration";
        Map<String, Object> jsonData = new HashMap<>();

        jsonData.put("name", sensorName);

        return makePostRequestWithJsonData(url, jsonData);
    }

    private static void sendMeasurement(double value, boolean raining, String sensorName) {
        final String url = "http://localhost:8080/measurements/add";
        Map<String, Object> jsonData = new HashMap<>();

        jsonData.put("value", value);
        jsonData.put("raining", raining);
        jsonData.put("sensor", Map.of("name", sensorName));

        makePostRequestWithJsonData(url, jsonData);
    }

    private static HttpStatus makePostRequestWithJsonData(String url, Map<String, Object> jsonData) {
        final RestTemplate restTemplate = new RestTemplate();
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //передаем map в HttpEntity для преобразования Map в JSON
        HttpEntity<Object> request = new HttpEntity<>(jsonData, headers);
        HttpStatus status = HttpStatus.valueOf(400);
        try {
            status = restTemplate.postForObject(url, request, HttpStatus.class);
            if (url.endsWith("/registration"))
                System.out.println("Регистрация сенсора прошла успешно");
            else System.out.println("Измерение успешно отправлено на сервер");
        } catch (HttpClientErrorException e) {
            System.out.println("Ошибка!");
            System.out.println(e.getMessage());
        }
        return status;
    }

    private static void deleteSensor(String nameSensor) {
        String url = "http://localhost:8080/sensors/" + nameSensor;
        final RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.delete(url);
            System.out.println("Удаление сенсора и его измерений с сервера прошла успешно");
        } catch (HttpClientErrorException e) {
            System.out.println("Ошибка!");
            System.out.println(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
