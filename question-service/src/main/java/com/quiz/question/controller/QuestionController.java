package com.quiz.question.controller;

import com.quiz.question.model.Question;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private static final List<Question> QUESTIONS = List.of(
            new Question(1L, "JAVA", "What is JVM?", "Java Virtual Machine"),
            new Question(2L, "SPRING", "What is dependency injection?", "Inversion of control pattern"),
            new Question(3L, "KAFKA", "What is a Kafka topic?", "A category/feed name")
    );

    @GetMapping
    public List<Question> getAll() {
        return QUESTIONS;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getById(@PathVariable("id") Long id) {
        return QUESTIONS.stream().filter(q -> q.id().equals(id)).findFirst()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
