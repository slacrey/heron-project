/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.loadburn.heron.storage.convertion.handler;

import com.loadburn.heron.storage.config.EntityMetadata;
import com.loadburn.heron.storage.convertion.ResultHandler;
import com.loadburn.heron.storage.convertion.ResultSetConverter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class BeanListHandler<T> implements ResultHandler<List<T>> {

    private final Class<T> type;

    public BeanListHandler(Class<T> type) {
        this.type = type;
    }

    @Override
    public List<T> handle(final EntityMetadata.EntityDescriptor entityDescriptor, ResultSet rs) throws SQLException {
        return ResultSetConverter.delegation(entityDescriptor).toBeanList(rs, type);
    }
}
