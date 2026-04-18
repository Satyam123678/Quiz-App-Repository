package com.quiz.quiz.client;

import com.quiz.quiz.model.Question;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "questionClient", url = "${question.service.url:http://localhost:8081}")
public interface QuestionClient {

    @GetMapping("/api/questions/{id}")
    Question getQuestionById(@PathVariable("id") Long id);
}
