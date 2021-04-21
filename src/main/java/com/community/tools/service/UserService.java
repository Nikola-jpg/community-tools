package com.community.tools.service;

import com.github.seratch.jslack.api.model.User;
import java.util.Set;

public interface UserService {

  String getUserById(String id);

  String getIdByUser(String id);

  String getIdByUsername(String username);

  Set<User> getAllUsers();

}
