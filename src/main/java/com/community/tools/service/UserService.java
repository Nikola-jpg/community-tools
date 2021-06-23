package com.community.tools.service;

import java.util.Map;
import java.util.Set;

public interface UserService<E> {

  String getUserById(String id);

  String getIdByUser(String id);

  String getIdByUsername(String username);

  Set<E> getAllUsers();

  Map<String, String> getIdWithName();

}
