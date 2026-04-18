package com.quiz.quiz.model;

import java.util.List;

public record Quiz(Long id, String title, List<Question> questions) {
}
