package com.quiz.quiz.service;

import com.quiz.quiz.client.QuestionClient;
import com.quiz.quiz.model.CreateQuizRequest;
import com.quiz.quiz.model.Question;
import com.quiz.quiz.model.Quiz;
import com.quiz.quiz.model.QuizCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class QuizService {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuizService.class);

    private final AtomicLong idSequence = new AtomicLong(0);
    private final Map<Long, Quiz> quizzes = new ConcurrentHashMap<>();

    private final QuestionClient questionClient;
    private final KafkaTemplate<String, QuizCreatedEvent> kafkaTemplate;
    private final String quizCreatedTopic;

    public QuizService(QuestionClient questionClient,
                       KafkaTemplate<String, QuizCreatedEvent> kafkaTemplate,
                       @Value("${quiz.kafka.topic.quiz-created:quiz-created}") String quizCreatedTopic) {
        this.questionClient = questionClient;
        this.kafkaTemplate = kafkaTemplate;
        this.quizCreatedTopic = quizCreatedTopic;
    }

    public Quiz createQuiz(CreateQuizRequest request) {
        List<Question> questions = request.questionIds().stream()
                .map(this::fetchQuestion)
                .toList();

        long quizId = idSequence.incrementAndGet();
        Quiz quiz = new Quiz(quizId, request.title(), questions);
        quizzes.put(quizId, quiz);

        QuizCreatedEvent event = new QuizCreatedEvent(quiz.id(), quiz.title(), quiz.questions().size());
        try {
            kafkaTemplate.send(quizCreatedTopic, String.valueOf(quiz.id()), event);
        } catch (RuntimeException ex) {
            LOGGER.warn("Quiz created but Kafka publish failed for quizId={}", quiz.id(), ex);
        }
        return quiz;
    }

    public Quiz getQuiz(Long id) {
        return quizzes.get(id);
    }

    private Question fetchQuestion(Long questionId) {
        Question question;
        try {
            question = questionClient.getQuestionById(questionId);
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Failed to fetch questionId=" + questionId + " from question-service", ex);
        }
        if (question == null) {
            throw new IllegalStateException("Question service returned no data for questionId=" + questionId);
        }
        return question;
    }
}
