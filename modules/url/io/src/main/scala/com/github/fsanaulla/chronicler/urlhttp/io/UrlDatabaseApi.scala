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

package com.github.fsanaulla.chronicler.urlhttp.io

import com.github.fsanaulla.chronicler.core.alias.ErrorOr
import com.github.fsanaulla.chronicler.core.api.DatabaseApi
import com.github.fsanaulla.chronicler.core.components.{BodyBuilder, ResponseHandler}
import com.github.fsanaulla.chronicler.core.enums.{Epoch, Epochs}
import com.github.fsanaulla.chronicler.core.model.Functor
import com.github.fsanaulla.chronicler.urlhttp.shared.Url
import com.github.fsanaulla.chronicler.urlhttp.shared.handlers.{UrlQueryBuilder, UrlRequestExecutor}
import jawn.ast.JArray
import requests.Response

import scala.util.Try

final class UrlDatabaseApi(dbName: String, gzipped: Boolean)
                          (implicit qb: UrlQueryBuilder, bd: BodyBuilder[String],
                           re: UrlRequestExecutor, rh: ResponseHandler[Response],
                           F: Functor[Try])
  extends DatabaseApi[Try, Response, Url, String](dbName, gzipped) {

  def readChunkedJson(query: String,
                      epoch: Epoch = Epochs.None,
                      pretty: Boolean = false,
                      chunkSize: Int = 10000): Iterator[ErrorOr[Array[JArray]]] = {
    val uri = readFromInfluxSingleQuery(dbName, query, epoch, pretty, chunkSize)
    re.executeStreaming(uri)
  }
}
