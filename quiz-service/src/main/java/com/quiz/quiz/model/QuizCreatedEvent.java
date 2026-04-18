package com.quiz.quiz.model;

public record QuizCreatedEvent(Long quizId, String title, int questionCount) {
}
