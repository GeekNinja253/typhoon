package com.typhoon;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typhoon.dto.PredictionRequestDTO;

public class TestJson {
    public static void main(String[] args) throws Exception {
        PredictionRequestDTO dto = new PredictionRequestDTO();
        dto.setWind_speed(10.5);
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(dto));
    }
}
