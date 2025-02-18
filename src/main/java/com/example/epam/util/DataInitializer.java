//package com.example.epam.util;
//
//import com.example.task_Spring_EPAM.entity.Trainee;
//import com.example.task_Spring_EPAM.entity.Trainer;
//import com.example.task_Spring_EPAM.entity.Training;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.InputStream;
//import java.util.List;
//
//public class DataInitializer {
//    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
//
//    public static List<Trainee> loadTraineesFromFile(String filePath) {
//        return loadFromFile(filePath, new TypeReference<List<Trainee>>() {});
//    }
//
//    public static List<Trainer> loadTrainersFromFile(String filePath) {
//        return loadFromFile(filePath, new TypeReference<List<Trainer>>() {});
//    }
//
//    public static List<Training> loadTrainingsFromFile(String filePath) {
//        return loadFromFile(filePath, new TypeReference<List<Training>>() {});
//    }
//
//    private static <T> List<T> loadFromFile(String filePath, TypeReference<List<T>> typeReference) {
//        try {
//            logger.info("Loading data from file: {}", filePath);
//            ObjectMapper mapper = new ObjectMapper();
//            InputStream inputStream = DataInitializer.class.getResourceAsStream(filePath);
//            List<T> data = mapper.readValue(inputStream, typeReference);
//            logger.info("Successfully loaded data from file: {}", filePath);
//            return data;
//        } catch (Exception e) {
//            logger.error("Failed to load data from file: {}", filePath, e);
//            throw new RuntimeException("Failed to load data from file: " + filePath, e);
//        }
//    }
//}
