package com.github.fsanaulla.chronicler.urlhttp

import java.nio.file.Paths

import com.github.fsanaulla.chronicler.testing.it.DockerizedInfluxDB
import com.github.fsanaulla.chronicler.urlhttp.io.{InfluxIO, UrlIOClient}
import com.github.fsanaulla.chronicler.urlhttp.management.{InfluxMng, UrlManagementClient}
import org.scalatest.concurrent.{Eventually, IntegrationPatience}
import org.scalatest.{FlatSpec, Matchers}

class CompressionSpec
  extends FlatSpec
  with Matchers
  with DockerizedInfluxDB
  with Eventually
  with IntegrationPatience {

  override def afterAll(): Unit = {
    mng.close()
    io.close()
    super.afterAll()
  }

  val testDB = "db"

  lazy val mng: UrlManagementClient =
    InfluxMng(s"http://$host", port, Some(creds))

  lazy val io: UrlIOClient =
    InfluxIO(s"http://$host", port, Some(creds), compress = true)

  lazy val db: io.Database = io.database(testDB)

  it should "ping database" in {
    eventually {
      io.ping.get.right.get.version shouldEqual version
    }
  }

  it should "write data from file" in {
    mng.createDatabase(testDB).get.right.get shouldEqual 200

    db.writeFromFile(Paths.get(getClass.getResource("/large_batch.txt").getPath))
      .get
      .right
      .get shouldEqual 204

    db.readJson("SELECT * FROM test1").get.right.get.length shouldEqual 10000
  }
}
