package com.community.tools.controller;

import com.community.tools.service.EstimateTaskService;
import com.community.tools.service.StateMachineService;
import com.github.seratch.jslack.api.model.view.ViewState.Value;
import com.github.seratch.jslack.app_backend.interactive_messages.payload.BlockActionPayload;
import com.github.seratch.jslack.common.json.GsonFactory;
import com.google.gson.Gson;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class InteractiveMessageController {

  @Autowired
  StateMachineService stateMachineService;

  /**
   * Endpoint /. Method POST
   *
   * @param payload JSON of BlockActionPayload
   * @throws Exception Exception
   */
  @ApiOperation(value = "Deserializes Slack payload and handler action interactive message")
  @ApiImplicitParam(name = "payload", dataType = "string", paramType = "query",
      required = true, value = "payload")
  @PostMapping
  public void handlerInteractiveMessage(@RequestParam(name = "payload") String payload)
      throws Exception {
    Gson snakeCase = GsonFactory.createSnakeCase();
    BlockActionPayload pl = snakeCase.fromJson(payload, BlockActionPayload.class);

    Map<String, Map<String, Value>> values = pl.getView().getState().getValues();

    stateMachineService.estimate(values, pl.getUser().getId());
  }
}
