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

package org.sonar.server.computation.measure;

import org.sonar.core.measure.db.MetricDto;
import org.sonar.core.persistence.DbSession;
import org.sonar.server.db.DbClient;
import org.sonar.server.util.cache.CacheLoader;

import java.util.Collection;
import java.util.Map;

public class MetricCacheLoader implements CacheLoader<String, MetricDto> {

  private final DbClient dbClient;

  public MetricCacheLoader(DbClient dbClient) {
    this.dbClient = dbClient;
  }

  @Override
  public MetricDto load(String key) {
    try (DbSession dbSession = dbClient.openSession(false)) {
      return dbClient.metricDao().getNullableByKey(dbSession, key);
    }
  }

  @Override
  public Map<String, MetricDto> loadAll(Collection<? extends String> keys) {
    throw new UnsupportedOperationException("see MetricCacheLoader.load");
  }


}
