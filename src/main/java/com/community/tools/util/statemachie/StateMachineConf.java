package com.community.tools.util.statemachie;

import static com.community.tools.util.statemachie.Event.*;
import static com.community.tools.util.statemachie.State.*;

import com.community.tools.util.statemachie.actions.*;

import java.util.EnumSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;

@Configuration
@EnableStateMachineFactory
public class StateMachineConf extends EnumStateMachineConfigurerAdapter<State, Event> {

  @Autowired
  StateMachinePersister persister;

  @Override
  public void configure(final StateMachineStateConfigurer<State, Event> states)
      throws Exception {
    states.withStates().initial(NEW_USER).states(EnumSet.allOf(State.class));
  }

  @Override
  public void configure(
      final StateMachineConfigurationConfigurer<State, Event> config)
      throws Exception {
    config
        .withConfiguration()
        .autoStartup(true)
        .listener(new StateMachineApplicationListeer());
  }

  @Override
  public void configure(final StateMachineTransitionConfigurer<State, Event> transitions)
      throws Exception {
    transitions
        .withExternal()
        .source(NEW_USER)
        .target(FIRST_LICENSE_MESS)
        .event(FIRST_AGREE_MESS)
        .action(firstAgreeLicenseAction(), errorAction())

        .and()
        .withExternal()
        .source(FIRST_LICENSE_MESS)
        .target(SECOND_LICENSE_MESS)
        .event(SECOND_AGREE_MESS)
        .action(secondAgreeLicenseAction(), errorAction())

        .and()
        .withExternal()
        .source(SECOND_LICENSE_MESS)
        .target(AGREED_LICENSE)
        .event(AGREE_LICENSE)
        .action(agreeLicenseAction(), errorAction())

        .and()
        .withExternal()
        .source(AGREED_LICENSE)
        .target(CHECK_LOGIN)
        .event(LOGIN_CONFIRMATION)
        .guard(hideGuard())
        .action(verificationLoginAction(), errorAction())

        .and()
        .withExternal()
        .source(CHECK_LOGIN)
        .target(AGREED_LICENSE)
        .event(DID_NOT_PASS_VERIFICATION_GIT_LOGIN)
        .action(didntPassVerificationGitLogin(), errorAction())

        .and()
        .withExternal()
        .source(CHECK_LOGIN)
        .target(ADDED_GIT)
        .event(ADD_GIT_NAME)
        .action(addGitNameAction(), errorAction())

        .and()
        .withExternal()
        .source(ADDED_GIT)
        .target(GOT_THE_FIRST_TASK)
        .event(GET_THE_FIRST_TASK)
        .action(getTheFirstTaskAction(), errorAction())

        .and()
        .withExternal()
        .source(GOT_THE_FIRST_TASK)
        .target(CHECK_FOR_NEW_TASK)
        .event(GET_THE_NEW_TASK)
        .action(checkForNewTaskAction(), errorAction())

        .and()
        .withExternal()
        .source(CHECK_FOR_NEW_TASK)
        .target(GOT_THE_FIRST_TASK)
        .event(CHANGE_TASK)
        .action(changeTaskAction(), errorAction())

        .and()
        .withExternal()
        .source(CHECK_FOR_NEW_TASK)
        .target(CONGRATS_LAST_TASK)
        .event(LAST_TASK)
        .guard(lastTaskGuard())
        .action(lastTaskAction(), errorAction());
  }

  @Bean
  public Action <State, Event> didntPassVerificationGitLogin(){ return new DidNotPassVerificationGitLogin(); }

  @Bean
  public Action<State, Event> verificationLoginAction() { return new VerificationLoginAction(); }

  @Bean
  public Action<State, Event> firstAgreeLicenseAction() {
    return new FirstAgreeLicenseAction();
  }

  @Bean
  public Action<State, Event> secondAgreeLicenseAction() {
    return new SecondAgreeLicenseAction();
  }

  @Bean
  public Action<State, Event> agreeLicenseAction() {
    return new AgreeLicenseAction();
  }

  @Bean
  public Action<State, Event> addGitNameAction() {
    return new AddGitNameAction();
  }

  @Bean
  public Action<State, Event> getTheFirstTaskAction() {
    return new GetTheFirstTaskAction();
  }

  @Bean
  public Action<State, Event> checkForNewTaskAction() {
    return new CheckForNewTaskAction();
  }

  @Bean
  public Action<State, Event> changeTaskAction() {
    return new ChangeTaskAction();
  }

  @Bean
  public Action<State, Event> lastTaskAction() {
    return new LastTaskAction();
  }

  @Bean
  public Action<State, Event> errorAction() {
    return new ErrorAction();
  }

  @Bean
  public Guard<State, Event> hideGuard() {
    return new HideGuard();
  }

  @Bean
  public Guard<State, Event> lastTaskGuard() {
    return new LastTaskGuard();
  }

  @Bean
  public org.springframework.statemachine.persist.StateMachinePersister persister() {
    return new DefaultStateMachinePersister<>(persister);
  }
}