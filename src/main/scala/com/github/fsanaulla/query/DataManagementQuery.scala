package com.github.fsanaulla.query

import akka.http.scaladsl.model.Uri
import com.github.fsanaulla.model.InfluxCredentials
import com.github.fsanaulla.utils.QueryBuilder

import scala.collection.mutable

/**
  * Created by fayaz on 27.06.17.
  */
private[fsanaulla] trait DataManagementQuery extends QueryBuilder {

  protected def createDatabaseQuery(dbName: String,
                                    duration: Option[String],
                                    replication: Option[Int],
                                    shardDuration: Option[String],
                                    rpName: Option[String])(implicit credentials: InfluxCredentials): Uri = {

    val sb = StringBuilder.newBuilder

    sb.append(s"CREATE DATABASE $dbName")

    if (duration.isDefined || replication.isDefined || shardDuration.isDefined || rpName.isDefined) {
      sb.append(" WITH")
    }

    for (d <- duration) {
      sb.append(s" DURATION $d")
    }

    for (r <- replication) {
      sb.append(s" REPLICATION $r")
    }

    for (sd <- shardDuration) {
      sb.append(s" SHARD DURATION $sd")
    }

    for (rp <- rpName) {
      sb.append(s" NAME $rp")
    }

    buildQuery("/query", buildQueryParams(sb.toString()))
  }

  protected def dropDatabaseQuery(dbName: String)(implicit credentials: InfluxCredentials): Uri = {
    buildQuery("/query", buildQueryParams(s"DROP DATABASE $dbName"))
  }

  protected def dropSeriesQuery(dbName: String, seriesName: String)(implicit credentials: InfluxCredentials): Uri = {
    buildQuery("/query", buildQueryParams(mutable.Map("db" -> dbName, "q" -> s"DROP SERIES FROM $seriesName")))
  }

  protected def dropMeasurementQuery(dbName: String, measurementName: String)(implicit credentials: InfluxCredentials): Uri = {
    buildQuery("/query", buildQueryParams(mutable.Map("db" -> dbName, "q" -> s"DROP MEASUREMENT $measurementName")))
  }

  protected def deleteAllSeriesQuery(dbName: String, seriesName: String)(implicit credentials: InfluxCredentials): Uri = {
    buildQuery("/query", buildQueryParams(mutable.Map("db" -> dbName, "q" -> s"DELETE FROM $seriesName")))
  }

  protected def showMeasurementQuery(dbName: String)(implicit credentials: InfluxCredentials): Uri = {
    buildQuery("/query", buildQueryParams(mutable.Map("db" -> dbName, "q" -> s"SHOW MEASUREMENTS")))
  }

  protected def showRetentionPoliciesQuery(dbName: String)(implicit credentials: InfluxCredentials): Uri = {
    buildQuery("/query", buildQueryParams(mutable.Map("db" -> dbName, "q" -> "SHOW RETENTION POLICIES")))
  }

  protected def showDatabasesQuery()(implicit credentials: InfluxCredentials): Uri = {
    buildQuery("/query", buildQueryParams(s"SHOW DATABASES"))
  }

  protected def showFieldKeysQuery(dbName: String, measurementName: String)(implicit credentials: InfluxCredentials): Uri = {
    buildQuery("/query", buildQueryParams(s"SHOW FIELD KEYS ON $dbName FROM $measurementName"))
  }

  protected def showTagKeysQuery(dbName: String,
                                 measurementName: String,
                                 whereClause: Option[String],
                                 limit: Option[Int],
                                 offset: Option[Int])
                                (implicit credentials: InfluxCredentials): Uri = {
    val sb = StringBuilder.newBuilder

    sb.append("SHOW TAG KEYS ON ")
      .append(dbName)
      .append(" FROM ")
      .append(measurementName)

    for (where <- whereClause) {
      sb.append(" WHERE ").append(where)
    }

    for (l <- limit) {
      sb.append(" LIMIT ").append(l)
    }

    for (o <- offset) {
      sb.append(" OFFSET ").append(o)
    }

    buildQuery("/query", buildQueryParams(sb.toString()))
  }

  protected def showTagValuesQuery(dbName: String, measurementName: String, withKey: Seq[String], whereClause: Option[String], limit: Option[Int], offset: Option[Int])(
      implicit credentials: InfluxCredentials): Uri = {
    require(withKey.nonEmpty, "Keys can't be empty")
    val sb = StringBuilder.newBuilder

    sb.append("SHOW TAG VALUES ON ")
      .append(dbName)
      .append(" FROM ")
      .append(measurementName)

    if (withKey.size == 1) {
      sb.append(" WITH KEY = ").append(withKey.head)
    } else {
      sb.append(" WITH KEY IN ").append(s"(${withKey.mkString(",")})")
    }

    for (where <- whereClause) {
      sb.append(" WHERE ").append(where)
    }

    for (l <- limit) {
      sb.append(" LIMIT ").append(l)
    }

    for (o <- offset) {
      sb.append(" OFFSET ").append(o)
    }

    buildQuery("/query", buildQueryParams(sb.toString()))
  }
}