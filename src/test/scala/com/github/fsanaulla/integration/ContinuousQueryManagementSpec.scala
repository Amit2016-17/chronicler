package com.github.fsanaulla.integration

import com.github.fsanaulla.InfluxClientsFactory
import com.github.fsanaulla.model.ContinuousQuery
import com.github.fsanaulla.utils.TestHelper._
import com.github.fsanaulla.utils.TestSpec

/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 09.08.17 on Anna's birthday
  */
class ContinuousQueryManagementSpec extends TestSpec {

  val testDB = "cq_spec_db"
  val testCQ = "test_cq"
  val query = "SELECT mean(\"value\") AS \"mean_value\" INTO \"aggregate\" FROM \"cpu_load_short\" GROUP BY time(30m)"
  val updateQuery = "SELECT mean(\"value\") AS \"mean_value\" INTO \"new_aggregate\" FROM \"cpu_load_short\" GROUP BY time(30m)"

  "CQ management operation" should "work correctly" in {

    // INIT INFLUX CLIENT
    val influx = InfluxClientsFactory.createHttpClient(host = influxHost, username = credentials.username, password = credentials.password)

    influx.createDatabase(testDB).futureValue shouldEqual OkResult

    influx.showDatabases().futureValue.queryResult.contains(testDB) shouldEqual true

    influx.createCQ(testDB, testCQ, query).futureValue shouldEqual OkResult

    influx.showCQ(testDB).futureValue.queryResult shouldEqual Seq(ContinuousQuery(testCQ, s"CREATE CONTINUOUS QUERY $testCQ ON $testDB BEGIN SELECT mean(value) AS mean_value INTO $testDB.autogen.aggregate FROM $testDB.autogen.cpu_load_short GROUP BY time(30m) END"))

    influx.updateCQ(testDB, testCQ, updateQuery).futureValue shouldEqual OkResult

    influx.showCQ(testDB).futureValue.queryResult.contains(ContinuousQuery(testCQ, s"CREATE CONTINUOUS QUERY $testCQ ON $testDB BEGIN SELECT mean(value) AS mean_value INTO $testDB.autogen.new_aggregate FROM $testDB.autogen.cpu_load_short GROUP BY time(30m) END")) shouldEqual true

    influx.dropCQ(testDB, testCQ).futureValue shouldEqual OkResult

    influx.showCQ(testDB).futureValue.queryResult shouldEqual Nil

    influx.dropDatabase(testDB).futureValue shouldEqual OkResult

    influx.close()
  }
}