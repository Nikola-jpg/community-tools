package com.community.tools.service;

import com.community.tools.dto.Message;

public interface EventListener {

  void memberJoin(Message message);

  void messageReceived(Message message);
}
