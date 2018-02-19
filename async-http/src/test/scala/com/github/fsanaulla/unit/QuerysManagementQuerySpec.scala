package com.github.fsanaulla.unit

import com.github.fsanaulla.core.query.QuerysManagementQuery
import com.github.fsanaulla.core.test.utils.TestSpec
import com.github.fsanaulla.handlers.AsyncQueryHandler
import com.github.fsanaulla.utils.TestHelper._
import com.softwaremill.sttp.Uri

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 20.08.17
  */
class QuerysManagementQuerySpec
  extends TestSpec
    with AsyncQueryHandler
    with QuerysManagementQuery[Uri] {

  val host = "localhost"
  val port = 8086

  "show query" should "correctly work" in {
    showQuerysQuery() shouldEqual queryTesterAuth("SHOW QUERIES")

    showQuerysQuery()(emptyCredentials) shouldEqual queryTester("SHOW QUERIES")
  }

  "kill query" should "correctly work" in {
    killQueryQuery(5) shouldEqual queryTesterAuth("KILL QUERY 5")

    killQueryQuery(5)(emptyCredentials) shouldEqual queryTester("KILL QUERY 5")
  }
}
