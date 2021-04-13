package com.community.tools.util.statemachie;

import static com.community.tools.util.statemachie.State.NEW_USER;

import com.community.tools.util.statemachie.actions.configs.ActionConfig;
import com.community.tools.util.statemachie.actions.configs.information.ChannelInformationActionConf;
import com.community.tools.util.statemachie.actions.configs.questions.FirstQuestionActionConfig;
import com.community.tools.util.statemachie.actions.configs.questions.SecondQuestionActionConfig;
import com.community.tools.util.statemachie.actions.configs.questions.ThirdQuestionActionConfig;
import com.community.tools.util.statemachie.actions.configs.tasks.ChangeTaskActionConfig;
import com.community.tools.util.statemachie.actions.configs.tasks.CheckForNewTaskActionConfig;
import com.community.tools.util.statemachie.actions.configs.tasks.GetTheFirstTaskActionConfig;
import com.community.tools.util.statemachie.actions.configs.tasks.LastTaskActionConfig;
import com.community.tools.util.statemachie.actions.configs.verifications.AddGitNameActionConfig;
import com.community.tools.util.statemachie.actions.configs.verifications.AgreeLicenseActionConfig;
import com.community.tools.util.statemachie.actions.configs.verifications.DidNotPassVerificationGitLoginConf;
import com.community.tools.util.statemachie.actions.configs.verifications.VerificationLoginActionConfig;
import com.community.tools.util.statemachie.actions.error.ErrorAction;
import com.community.tools.util.statemachie.actions.guard.HideGuard;
import com.community.tools.util.statemachie.actions.guard.LastTaskGuard;
import com.community.tools.util.statemachie.actions.information.ChannelInformationAction;
import com.community.tools.util.statemachie.actions.questions.FirstQuestionAction;
import com.community.tools.util.statemachie.actions.questions.SecondQuestionAction;
import com.community.tools.util.statemachie.actions.questions.ThirdQuestionAction;
import com.community.tools.util.statemachie.actions.tasks.ChangeTaskAction;
import com.community.tools.util.statemachie.actions.tasks.CheckForNewTaskAction;
import com.community.tools.util.statemachie.actions.tasks.GetTheFirstTaskAction;
import com.community.tools.util.statemachie.actions.tasks.LastTaskAction;
import com.community.tools.util.statemachie.actions.verifications.AddGitNameAction;
import com.community.tools.util.statemachie.actions.verifications.AgreeLicenseAction;
import com.community.tools.util.statemachie.actions.verifications.DidNotPassVerificationGitLogin;
import com.community.tools.util.statemachie.actions.verifications.VerificationLoginAction;

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
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;

@Configuration
@EnableStateMachineFactory
public class StateMachineConf extends EnumStateMachineConfigurerAdapter<State, Event> {

  private static final int BEANS_QUANTITY = 10;
  private static final int FIRST_INDEX = 0;

  @Autowired
  private StateMachinePersister persister;

  @Autowired
  private FirstQuestionActionConfig firstQuestionActionConfig;

  @Autowired
  private SecondQuestionActionConfig secondQuestionActionConfig;

  @Autowired
  private ThirdQuestionActionConfig thirdQuestionActionConfig;

  @Autowired
  private ChangeTaskActionConfig changeTaskActionConfig;

  @Autowired
  private CheckForNewTaskActionConfig checkForNewTaskActionConfig;

  @Autowired
  private GetTheFirstTaskActionConfig getTheFirstTaskActionConfig;

  @Autowired
  private LastTaskActionConfig lastTaskActionConfig;

  @Autowired
  private AddGitNameActionConfig addGitNameActionConfig;

  @Autowired
  private AgreeLicenseActionConfig agreeLicenseActionConfig;

  @Autowired
  private DidNotPassVerificationGitLoginConf didNotPassVerificationGitLoginConf;

  @Autowired
  private VerificationLoginActionConfig verificationLoginActionConfig;

  @Autowired
  private ChannelInformationActionConf channelInformationActionConf;

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
        .listener(new StateMachineApplicationListener());
  }

  @Override
  public void configure(final StateMachineTransitionConfigurer<State, Event> transitions)
      throws Exception {
    ExternalTransitionConfigurer<State, Event> firstQuestion = firstQuestionActionConfig
        .configure(transitions);
    ActionConfig[] actionConfigs = getConfigBeansArray();
    transitionChains(firstQuestion, actionConfigs, FIRST_INDEX);
  }

  private void transitionChains(ExternalTransitionConfigurer<State, Event> question,
                                ActionConfig[] beans, int index) throws Exception {
    ExternalTransitionConfigurer<State, Event> trans = beans[index].configure(question);
    if (index != BEANS_QUANTITY) {
      transitionChains(trans, beans, index + 1);
    }
  }

  private ActionConfig[] getConfigBeansArray() {
    return new ActionConfig[]{
        secondQuestionActionConfig, thirdQuestionActionConfig, channelInformationActionConf,
        agreeLicenseActionConfig, verificationLoginActionConfig,
        didNotPassVerificationGitLoginConf, addGitNameActionConfig,
        getTheFirstTaskActionConfig, checkForNewTaskActionConfig,
        changeTaskActionConfig, lastTaskActionConfig
    };
  }

  @Bean
  public Action<State, Event> didntPassVerificationGitLogin() {
    return new DidNotPassVerificationGitLogin();
  }

  @Bean
  public Action<State, Event> verificationLoginAction() {
    return new VerificationLoginAction();
  }

  @Bean
  public Action<State, Event> firstQuestionAction() {
    return new FirstQuestionAction();
  }

  @Bean
  public Action<State, Event> secondQuestionAction() {
    return new SecondQuestionAction();
  }

  @Bean
  public Action<State, Event> thirdQuestionAction() {
    return new ThirdQuestionAction();
  }

  @Bean
  Action<State, Event> informationChannelsAction() {
    return new ChannelInformationAction();
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