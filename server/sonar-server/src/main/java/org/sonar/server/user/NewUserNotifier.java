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
package org.sonar.server.user;

import org.slf4j.LoggerFactory;
import org.sonar.api.ServerComponent;
import org.sonar.api.platform.NewUserHandler;

/**
 * @since 3.2
 */
public class NewUserNotifier implements ServerComponent {

  private NewUserHandler[] handlers;

  public NewUserNotifier(NewUserHandler[] handlers) {
    this.handlers = handlers;
  }

  public NewUserNotifier() {
    this(new NewUserHandler[0]);
  }

  public void onNewUser(NewUserHandler.Context context) {
    LoggerFactory.getLogger(NewUserNotifier.class).debug("User created: " + context.getLogin() + ". Notifying " + NewUserHandler.class.getSimpleName() + " handlers...");
    for (NewUserHandler handler : handlers) {
      handler.doOnNewUser(context);
    }
  }
}