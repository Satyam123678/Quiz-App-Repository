package com.quiz.quiz.service;

import com.quiz.quiz.client.QuestionClient;
import com.quiz.quiz.model.CreateQuizRequest;
import com.quiz.quiz.model.Question;
import com.quiz.quiz.model.Quiz;
import com.quiz.quiz.model.QuizCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuestionClient questionClient;

    @Mock
    private KafkaTemplate<String, QuizCreatedEvent> kafkaTemplate;

    private QuizService quizService;

    @BeforeEach
    void setUp() {
        quizService = new QuizService(questionClient, kafkaTemplate, "quiz-created");
    }

    @Test
    void createQuiz_fetchesQuestionsAndPublishesEvent() {
        when(questionClient.getQuestionById(1L)).thenReturn(new Question(1L, "JAVA", "Q1", "A1"));
        when(questionClient.getQuestionById(2L)).thenReturn(new Question(2L, "SPRING", "Q2", "A2"));

        Quiz quiz = quizService.createQuiz(new CreateQuizRequest("Backend Quiz", List.of(1L, 2L)));

        assertThat(quiz.id()).isEqualTo(1L);
        assertThat(quiz.questions()).hasSize(2);

        ArgumentCaptor<QuizCreatedEvent> eventCaptor = ArgumentCaptor.forClass(QuizCreatedEvent.class);
        verify(kafkaTemplate).send(eq("quiz-created"), eq("1"), eventCaptor.capture());
        assertThat(eventCaptor.getValue().title()).isEqualTo("Backend Quiz");
        assertThat(eventCaptor.getValue().questionCount()).isEqualTo(2);
    }
}
