package com.community.tools;

import com.github.seratch.jslack.*;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.response.chat.ChatPostMessageResponse;
import com.github.seratch.jslack.api.model.Im;
import com.github.seratch.jslack.api.model.User;
import com.github.seratch.jslack.api.rtm.*;
import com.github.seratch.jslack.api.rtm.message.Message;
import com.github.seratch.jslack.api.rtm.message.Typing;
import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.websocket.DeploymentException;
import java.io.IOException;

@Service("slack")
public class SlackService {
    
    @Value("${slack.token}")
    private String token;

    public String sendPrivateMessage(String username, String messageText)
            throws IOException, SlackApiException {
        Slack slack = Slack.getInstance();

        User user = slack.methods(token).usersList(req -> req).getMembers().stream()
                .filter(u -> u.getProfile().getDisplayName().equals(username))
                .findFirst().get();

        ChatPostMessageResponse postResponse =
            slack.methods(token).chatPostMessage(
                    req -> req.channel(user.getId()).asUser(true).text(messageText));

        return postResponse.getTs();
    }

}
