/*
 * Copyright 2017-2019 Faiaz Sanaulla
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.fsanaulla.chronicler.urlhttp.shared.handlers

import com.github.fsanaulla.chronicler.core.components.QueryBuilder
import com.github.fsanaulla.chronicler.core.model.InfluxCredentials
import com.github.fsanaulla.chronicler.urlhttp.shared.Url

private[urlhttp] class UrlQueryBuilder(host: String,
                                       port: Int,
                                       credentials: Option[InfluxCredentials],
                                       ssl: Boolean) extends QueryBuilder[Url](credentials) {

  // used as a stub class to collect all request information
  override def buildQuery(uri: String, queryParams: Map[String, String]): Url =
    new Url(host + ":" + port + uri, queryParams.toList, ssl)
}
