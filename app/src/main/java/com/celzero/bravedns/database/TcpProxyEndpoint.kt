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

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TcpProxyEndpoint")
class TcpProxyEndpoint {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
    var name: String = ""
    var token: String = ""
    var url: String = ""
    var paymentStatus: Int = 0
    var isActive: Boolean = false

    override fun equals(other: Any?): Boolean {
        if (other !is TcpProxyEndpoint) return false
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return this.id.hashCode()
    }

    constructor(
        id: Int,
        name: String,
        token: String,
        url: String,
        paymentStatus: Int,
        isActive: Boolean
    ) {
        this.id = id
        this.name = name
        this.token = token
        this.url = url
        this.paymentStatus = paymentStatus
        this.isActive = isActive
    }
}
