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

class WgHopMapRepository(private val dao: WgHopMapDao) {

    @Transaction
    suspend fun update(map: WgHopMap) {
        dao.update(map)
    }

    suspend fun insertAll(maps: List<WgHopMap>): LongArray {
        return dao.insertAll(maps)
    }

    suspend fun insert(map: WgHopMap): Long {
        return dao.insert(map)
    }

    suspend fun getAll(): List<WgHopMap> {
        return dao.getAll()
    }

    suspend fun getBySrc(src: String): WgHopMap? {
        return dao.getBySrc(src)
    }

    suspend fun delete(map: WgHopMap) {
        dao.delete(map)
    }

    suspend fun deleteBySrcAndHop(src: String, hop: String): Int {
        return dao.deleteBySrcAndHop(src, hop)
    }

    suspend fun deleteById(id: Int) {
        dao.deleteById(id)
    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }
}
