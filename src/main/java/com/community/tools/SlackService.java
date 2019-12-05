package com.community.tools;

import com.github.seratch.jslack.*;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.response.chat.ChatPostMessageResponse;
import com.github.seratch.jslack.api.methods.response.im.ImListResponse;
import com.github.seratch.jslack.api.methods.response.users.UsersListResponse;
import com.github.seratch.jslack.api.model.Im;
import com.github.seratch.jslack.api.model.User;
import com.github.seratch.jslack.api.rtm.*;
import com.github.seratch.jslack.api.rtm.message.Message;
import com.github.seratch.jslack.api.rtm.message.Typing;
import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.DeploymentException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class SlackService {

    private String getToken() throws IOException {
        Properties property = new Properties();
        FileInputStream fis = new FileInputStream("src/main/resources/slack.properties");
        property.load(fis);

        return property.getProperty("token");
    }

    public String sendPrivateMessage(String username, String messageText)
            throws IOException, SlackApiException {
        Slack slack = Slack.getInstance();

        User user = slack.methods(getToken()).usersList(req -> req).getMembers().stream()
                .filter(u -> u.getProfile().getDisplayName().equals(username))
                .findFirst().get();

        ChatPostMessageResponse postResponse =
            slack.methods(getToken()).chatPostMessage(
                    req -> req.channel(user.getId()).asUser(true).text(messageText));

        return postResponse.getTs();
    }

    public void sendMessage(String username, String messageText)
            throws DeploymentException, IOException, SlackApiException {
        JsonParser jsonParser = new JsonParser();
        Logger log = LoggerFactory.getLogger("main");

        Slack slack = Slack.getInstance();

        User user = slack.methods(getToken()).usersList(req -> req).getMembers().stream()
                .filter(u -> u.getProfile().getDisplayName().equals(username))
                .findFirst().get();

        Im im = slack.methods(getToken()).imList(req -> req).getIms().stream()
                .filter(i -> i.getUser().equals(user.getId()))
                .findFirst().get();

        try (RTMClient rtm = new Slack().rtm(getToken())) {

            rtm.addMessageHandler((message) -> {
                JsonObject json = jsonParser.parse(message).getAsJsonObject();
                if (json.get("type") != null) {
                    log.info("Handled type: {}", json.get("type").getAsString());
                }
            });

            RTMMessageHandler handler2 = (message) -> {
                log.info("hello");
            };

            rtm.addMessageHandler(handler2);

            rtm.connect();

            rtm.sendMessage(Typing.builder()
                    .id(System.currentTimeMillis())
                    .channel(im.getId())
                    .build().toJSONString());

            rtm.sendMessage(Message.builder()
                    .id(System.currentTimeMillis())
                    .channel(im.getId())
                    .text(messageText)
                    .build().toJSONString());

            rtm.removeMessageHandler(handler2);
        }
    }

}
