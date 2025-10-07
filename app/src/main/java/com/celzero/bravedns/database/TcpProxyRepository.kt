/*
 * Copyright 2023 RethinkDNS and its authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rethinkdns.retrixed.database

import androidx.room.Transaction

class TcpProxyRepository(private val tcpProxyDAO: TcpProxyDAO) {

    @Transaction
    suspend fun update(wgConfigFiles: TcpProxyEndpoint) {
        tcpProxyDAO.update(wgConfigFiles)
    }

    suspend fun insertAll(wgConfigFiles: List<TcpProxyEndpoint>): LongArray {
        return tcpProxyDAO.insertAll(wgConfigFiles)
    }

    suspend fun insert(wgConfigFiles: TcpProxyEndpoint): Long {
        return tcpProxyDAO.insert(wgConfigFiles)
    }

    suspend fun getTcpProxies(): List<TcpProxyEndpoint> {
        return tcpProxyDAO.getTcpProxies()
    }

    suspend fun delete(wgConfigFiles: TcpProxyEndpoint) {
        tcpProxyDAO.delete(wgConfigFiles)
    }

    suspend fun deleteConfig(id: Int) {
        tcpProxyDAO.deleteById(id)
    }
}
