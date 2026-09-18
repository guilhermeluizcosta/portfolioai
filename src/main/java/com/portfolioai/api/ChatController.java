package com.portfolioai.api;

import com.portfolioai.domain.Question;
import dev.langchain4j.model.chat.ChatModel;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;

@Controller("/chat")
public class ChatController {

    public final ChatModel chatModel;

    public ChatController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Post
    @ExecuteOn(TaskExecutors.BLOCKING)
    public HttpResponse<String> chat(@Body Question question){

        return HttpResponse.ok(chatModel.chat(question.question()));
    }

}
