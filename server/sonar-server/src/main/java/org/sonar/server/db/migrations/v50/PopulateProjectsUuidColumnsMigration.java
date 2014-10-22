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

package org.sonar.server.db.migrations.v50;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.sonar.api.resources.Scopes;
import org.sonar.core.persistence.DbSession;
import org.sonar.core.persistence.migration.v50.Component;
import org.sonar.core.persistence.migration.v50.Migration50Mapper;
import org.sonar.server.db.DbClient;
import org.sonar.server.db.migrations.DatabaseMigration;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Used in the Active Record Migration 705
 *
 * @since 5.0
 */
public class PopulateProjectsUuidColumnsMigration implements DatabaseMigration {

  private final DbClient db;

  public PopulateProjectsUuidColumnsMigration(DbClient db) {
    this.db = db;
  }

  @Override
  public void execute() {
    DbSession session = db.openSession(true);
    try {
      Migration50Mapper mapper = session.getMapper(Migration50Mapper.class);

      for (Component project : mapper.selectEnabledRootTrkProjects()) {
        Map<Long, String> uuidByComponentId = newHashMap();
        Map<Long, Component> componentsBySnapshotId = newHashMap();

        List<Component> components = mapper.selectComponentChildrenForProjects(project.getId());
        components.add(project);
        for (Component component : components) {
          componentsBySnapshotId.put(component.getSnapshotId(), component);

          component.setUuid(getOrCreateUuid(component.getId(), uuidByComponentId));
          component.setProjectUuid(getOrCreateUuid(project.getId(), uuidByComponentId));
        }

        for (Component component : components) {
          String snapshotPath = component.getSnapshotPath();
          StringBuilder moduleUuidPath = new StringBuilder();
          Component lastModule = null;
          if (!Strings.isNullOrEmpty(snapshotPath)) {
            for (String s : Splitter.on(".").omitEmptyStrings().split(snapshotPath)) {
              Long snapshotId = Long.valueOf(s);
              Component currentComponent = componentsBySnapshotId.get(snapshotId);
              if (currentComponent.getScope().equals(Scopes.PROJECT)) {
                lastModule = currentComponent;
                moduleUuidPath.append(currentComponent.getUuid()).append(".");
              }
            }
          }
          if (moduleUuidPath.length() > 0) {
            component.setModuleUuidPath(moduleUuidPath.toString());
          }

          // Module UUID should contains direct module of a component, but it should be null on the first module
          if (lastModule != null && !lastModule.getId().equals(project.getId())) {
            component.setModuleUuid(getOrCreateUuid(lastModule.getId(), uuidByComponentId));
          }

          mapper.updateComponentUuids(component);
        }
      }

      session.commit();
    } finally {
      session.close();
    }
  }

  private static String getOrCreateUuid(Long componentId, Map<Long, String> uuidByComponentId) {
    String uuid = uuidByComponentId.get(componentId);
    if (uuid == null) {
      String newUuid = UUID.randomUUID().toString();
      uuidByComponentId.put(componentId, newUuid);
      return newUuid;
    }
    return uuid;
  }

}