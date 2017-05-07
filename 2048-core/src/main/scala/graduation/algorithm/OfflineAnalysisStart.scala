package graduation.algorithm

import java.util

import breeze.linalg.DenseMatrix
import com.mongodb.spark.MongoSpark
import com.mongodb.spark.config._
import graduation.util.{Constant, CoreCommon, CoreEnv, MatrixUtil}
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

import scala.collection.JavaConverters._

/**
  * Created by KeXu on 2017/5/2.
  */
object OfflineAnalysisStart {

  def init(): Unit = {
    Constant.ConstantInit()
  }

  def main(args: Array[String]): Unit = {

    init()
    val conf = Array(
      ("spark.mongodb.input.uri", CoreEnv.sparkMongodbInputUri),
      ("spark.mongodb.input.database", CoreEnv.sparkMongodbInputDatabase),
      ("spark.mongodb.input.collection", CoreEnv.sparkMongodbInputCollection),
      ("spark.mongodb.input.readPreference.name", CoreEnv.sparkMongodbInputReadPreference)
    )
    val spark = CoreCommon.instanceSpark(CoreEnv.master, conf)
    val readConfig = ReadConfig(
      Map.empty[String, String], Some(ReadConfig(spark)))
    val mongodbRdd = MongoSpark.load(spark, readConfig)

    val baseData = mongodbRdd.map { doc =>
      try {
        val grid = doc.get("grid").asInstanceOf[util.ArrayList[util.ArrayList[util.ArrayList[Int]]]].asScala
        val headMatrix: DenseMatrix[Double] = DenseMatrix(grid.head.asScala.map(i =>
          Tuple4(i.get(0).toDouble, i.get(1).toDouble, i.get(2).toDouble, i.get(3).toDouble)): _*)
        val lastMatrix: DenseMatrix[Double] = DenseMatrix(grid.last.asScala.map(i =>
          Tuple4(i.get(0).toDouble, i.get(1).toDouble, i.get(2).toDouble, i.get(3).toDouble)): _*)
        val convert = MatrixUtil.convertMatrix(headMatrix, lastMatrix)
        (convert.data.mkString(" "), doc.get("score").asInstanceOf[Int])
      } catch {
        case e: Throwable =>
          println(e.getMessage)
          null
      }
    }.filter(_ != null) //.foreach(println(_))

    val excellent = CoreEnv.excellentThreshold
    val analysisData: RDD[LabeledPoint] = baseData.map { data =>
      val lable = if (data._2 > excellent) 1 else 0
      val parses = lable + "," + data._1
      val point = LabeledPoint.parse(parses)
      point
    }
    analysisData.foreach(println(_))

    var training: RDD[LabeledPoint] = null
    var test: RDD[LabeledPoint] = null
    val splits = analysisData.randomSplit(Array(0.8, 0.2), seed = 11L)
    training = splits(0)
    test = splits(1)

    val model = new LogisticRegressionWithLBFGS().
      setNumClasses(2).
      run(training)

    val predictionAndLabels = test.map {
      case LabeledPoint(label, features) =>
        val prediction = model.predict(features)
        (prediction, label)
    }

    //误差计算
    val metrics = new MulticlassMetrics(predictionAndLabels)
    val precision = metrics.precision
    println("Precision=" + precision)

    //模型保存
    model.save(spark, CoreEnv.modelSavePath)


  }

}
