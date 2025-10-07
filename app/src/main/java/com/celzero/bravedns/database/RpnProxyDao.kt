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

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface RpnProxyDao {

    @Update fun update(rpnProxy: RpnProxy): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(rpnProxies: List<RpnProxy>): LongArray

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(rpnProxy: RpnProxy): Long

    @Delete fun delete(proxy: RpnProxy)

    @Query("delete from RpnProxy where id = :id") fun deleteById(id: Int)

    @Query("select * from RpnProxy where id = :id") fun getProxyById(id: Int): RpnProxy?

    @Query("select * from RpnProxy") fun getAllProxies(): List<RpnProxy>
}
