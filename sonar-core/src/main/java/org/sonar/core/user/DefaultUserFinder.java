/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2014 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.core.user;

import com.google.common.collect.Lists;
import org.sonar.api.user.User;
import org.sonar.api.user.UserFinder;
import org.sonar.api.user.UserQuery;

import javax.annotation.CheckForNull;
import java.util.Collection;
import java.util.List;

/**
 * @since 3.6
 */
public class DefaultUserFinder implements UserFinder {

  private final UserDao userDao;

  public DefaultUserFinder(UserDao userDao) {
    this.userDao = userDao;
  }

  @Override
  @CheckForNull
  public User findByLogin(String login) {
    UserDto dto = userDao.selectActiveUserByLogin(login);
    return dto != null ? dto.toUser() : null;
  }

  @Override
  public List<User> findByLogins(List<String> logins) {
    List<UserDto> dtos = userDao.selectUsersByLogins(logins);
    return toUsers(dtos);
  }

  @Override
  public List<User> find(UserQuery query) {
    List<UserDto> dtos = userDao.selectUsers(query);
    return toUsers(dtos);
  }

  private List<User> toUsers(Collection<UserDto> dtos) {
    List<User> users = Lists.newArrayList();
    for (UserDto dto : dtos) {
      users.add(dto.toUser());
    }
    return users;
  }
}
