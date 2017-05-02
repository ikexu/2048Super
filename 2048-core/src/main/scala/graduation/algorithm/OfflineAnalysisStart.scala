package graduation.algorithm

import com.mongodb.spark.MongoSpark
import graduation.util.{CoreCommon, CoreEnv}
import com.mongodb.spark.config._

/**
  * Created by KeXu on 2017/5/2.
  */
object OfflineAnalysisStart {


  def main(args: Array[String]): Unit = {

      val conf=Array(
        ("spark.mongodb.input.uri",CoreEnv.sparkMongodbInputUri),
        ("spark.mongodb.input.database",CoreEnv.sparkMongodbInputDatabase),
        ("spark.mongodb.input.collection",CoreEnv.sparkMongodbInputCollection),
        ("spark.mongodb.input.readPreference.name",CoreEnv.sparkMongodbInputReadPreference)
      )
      val spark=CoreCommon.instanceSpark(CoreEnv.master,conf)
      val readConfig = ReadConfig(
        Map.empty[String, String], Some(ReadConfig(spark)))
      val customRdd = MongoSpark.load(spark, readConfig)
    customRdd.foreach(println(_))
  }

}
