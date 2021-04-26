package com.community.tools.service.discord;

import com.community.tools.model.Messages;
import com.community.tools.model.User;
import com.community.tools.service.BlockService;
import com.community.tools.service.MessageService;
import com.community.tools.service.StateMachineService;
import com.community.tools.service.payload.Payload;
import com.community.tools.service.payload.QuestionPayload;
import com.community.tools.service.payload.SinglePayload;
import com.community.tools.service.payload.VerificationPayload;
import com.community.tools.service.slack.MessagesToSlack;
import com.community.tools.util.statemachine.Event;
import com.community.tools.util.statemachine.State;
import com.community.tools.util.statemachine.jpa.StateMachineRepository;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DiscordEventListener extends ListenerAdapter {

  @Autowired
  private MessageService messageService;
  @Autowired
  private StateMachineService stateMachineService;
  @Autowired
  private StateMachineRepository stateMachineRepository;
  @Autowired
  private BlockService blockService;

  @Override
  public void onGuildMemberJoin(GuildMemberJoinEvent event) {
    try {
      String user = event.getUser().getId();
      User stateEntity = new User();
      stateEntity.setUserID(user);
      stateMachineRepository.save(stateEntity);

      stateMachineService.persistMachineForNewUser(user);
      messageService.sendPrivateMessage(event.getUser().getName(), Messages.WELCOME);
      messageService
          .sendBlocksMessage(event.getUser().getName(),
              blockService.createBlockMessage(MessagesToSlack.MESSAGE_ABOUT_RULES,
                  MessagesToDiscord.MESSAGE_ABOUT_RULES));
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }

  }


  @Override
  public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
    try {
      String id = event.getAuthor().getId();
      StateMachine<State, Event> machine = stateMachineService.restoreMachine(id);
      String userForQuestion = machine.getExtendedState().getVariables().get("id").toString();

      String message = Messages.DEFAULT_MESSAGE;
      String gitNick = "";
      Event stateMachineEvent = null;
      Payload payload = null;

      switch (machine.getState().getId()) {
        case NEW_USER:
          if (event.getMessage().getContentRaw().equalsIgnoreCase("ready")) {
            payload = new SinglePayload(id);
            stateMachineEvent = Event.QUESTION_FIRST;
          } else {
            message = Messages.NOT_THAT_MESSAGE;
          }
          break;
        case FIRST_QUESTION:
          payload = new QuestionPayload(id, event.getMessage().getContentRaw(), userForQuestion);
          stateMachineEvent = Event.QUESTION_SECOND;
          break;
        case SECOND_QUESTION:
          payload = new QuestionPayload(id, event.getMessage().getContentRaw(), userForQuestion);
          stateMachineEvent = Event.QUESTION_THIRD;
          break;
        case THIRD_QUESTION:
          payload = new QuestionPayload(id, event.getMessage().getContentRaw(), userForQuestion);
          stateMachineEvent = Event.CONSENT_TO_INFORMATION;
          break;
        case AGREED_LICENSE:
          gitNick = event.getMessage().getContentRaw();
          payload = new VerificationPayload(id, gitNick);
          stateMachineEvent = Event.LOGIN_CONFIRMATION;
          break;
        case CHECK_LOGIN:
          if (event.getMessage().getContentRaw().equalsIgnoreCase("yes")) {
            stateMachineEvent = Event.ADD_GIT_NAME;
          } else if (event.getMessage().getContentRaw().equalsIgnoreCase("no")) {
            stateMachineEvent = Event.DID_NOT_PASS_VERIFICATION_GIT_LOGIN;
          } else {
            message = Messages.NOT_THAT_MESSAGE;
          }
          payload = new VerificationPayload(id, gitNick);
          break;
        case ADDED_GIT:
          payload = new SinglePayload(id);
          stateMachineEvent = Event.GET_THE_FIRST_TASK;
          break;
        default:
          stateMachineEvent = null;
          payload = null;
      }

      if (stateMachineEvent == null) {
        messageService.sendPrivateMessage(
            messageService.getUserById(event.getAuthor().getId()),
            message);
      } else {
        stateMachineService
            .doAction(machine, payload, stateMachineEvent);
      }

    } catch (Exception exception) {
      throw new RuntimeException("Impossible to answer request with id = "
          + event.getAuthor().getId(), exception);
    }

  }

  @Override
  public void onReady(ReadyEvent event) {
    super.onReady(event);
    log.info("{} is ready", event.getJDA().getSelfUser());

  }


}
