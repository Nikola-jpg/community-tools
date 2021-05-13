package com.community.tools.controller;

import com.github.seratch.jslack.api.model.view.ViewState.Value;
import com.github.seratch.jslack.app_backend.interactive_messages.payload.BlockActionPayload;
import com.github.seratch.jslack.common.json.GsonFactory;
import com.google.gson.Gson;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import java.util.Map;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/")
public class InteractiveMessageController {

  private static final Logger logger = Logger
      .getLogger(InteractiveMessageController.class.getName());

  /**
   * Endpoint /. Method POST
   *
   * @param payload JSON of BlockActionPayload
   * @throws Exception Exception
   */
  @ApiOperation(value = "Deserializes Slack payload and sends message to user")
  @ApiImplicitParam(name = "payload", dataType = "string", paramType = "query",
      required = true, value = "payload")
  @RequestMapping(value = "/", method = RequestMethod.POST)
  public void action(@RequestParam(name = "payload") String payload) throws Exception {

    Gson snakeCase = GsonFactory.createSnakeCase();
    BlockActionPayload pl = snakeCase.fromJson(payload, BlockActionPayload.class);
    Map<String, Map<String, Value>> values = pl.getView().getState().getValues();
    logger.info("url: /" + values.toString());
  }
}
