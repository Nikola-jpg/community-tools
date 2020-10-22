package com.community.tools.controller;

import static com.community.tools.util.CreateImageWithLeaderBoard.createImage;
import static com.community.tools.util.GetServerAddress.getAddress;

import com.community.tools.util.statemachie.jpa.StateEntity;
import com.community.tools.util.statemachie.jpa.StateMachineRepository;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/leaderboard")
public class LeaderBoardController {

  @Autowired
  StateMachineRepository stateMachineRepository;

  /**
   * This method return webpage with table of trainee's rating.
   * @param model Model
   * @return webpage with template "leaderboard"
   */
  @GetMapping("/")
  public String getLeaderboard(Model model) {
    List<StateEntity> list = stateMachineRepository.findAll();
    list.sort(Comparator.comparing(StateEntity::getTotalPoints).reversed());
    model.addAttribute("entities", list);
    return "leaderboard";
  }

  /**
   * This vmethod return image with table, which contains first 5 trainees of leaderboard.
   * @param response HttpServletResponse
   * @throws EntityNotFoundException EntityNotFoundException
   * @throws IOException IOException
   */
  @RequestMapping(value = "/best", method = RequestMethod.GET)
  public void getImage(HttpServletResponse response) throws EntityNotFoundException, IOException {
    String url = getAddress();
    byte[] data = createImage(url);
    response.setContentType(MediaType.IMAGE_PNG_VALUE);
    response.getOutputStream().write(data);
    response.setContentLength(data.length);
  }

  /**
   * Tjis vmethod return webpage with table, which contains first 5 trainees of leaderboard.
   * @param model Model
   * @return webpage with template "leaderboard"
   */
  @GetMapping("/better")
  public String getLeaderboardFirstFive(Model model) {
    List<StateEntity> list = stateMachineRepository.findAll();
    list.sort(Comparator.comparing(StateEntity::getTotalPoints).reversed());
    List<StateEntity> listFirst = list.stream().limit(5).collect(Collectors.toList());
    model.addAttribute("entities", listFirst);
    return "leaderboard";
  }

}
