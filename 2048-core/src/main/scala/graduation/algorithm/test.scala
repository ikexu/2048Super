package graduation.algorithm

import org.apache.spark.SparkContext
import org.apache.spark.mllib.classification.{LogisticRegressionModel, LogisticRegressionWithLBFGS}
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

/**
  * Created by KeXu on 2017/4/10.
  */
class test {

  def main(args: Array[String]): Unit = {

    var training: RDD[LabeledPoint]=null
    var test: RDD[LabeledPoint]=null
    val data: RDD[LabeledPoint]=null
    val splits=data.randomSplit(Array(0.8,0.2),seed = 11L)
    training=splits(0)
    test=splits(1)


    val model =new LogisticRegressionWithLBFGS().
      setNumClasses(2).
      run(training)

    val predictionAndLabels=test.map{
      case LabeledPoint(label,features) =>
        val prediction =model.predict(features)
        (prediction,label)
    }

    val print_predict=predictionAndLabels.take(20)
    println("prediction"+"\t"+"label")
    for (i <- print_predict){
      println(i._1+"\t"+i._2)
    }

    val metrics=new MulticlassMetrics(predictionAndLabels)
    val precision=metrics.precision
    println("Precision="+precision)
    val sc=new SparkContext()
    val savePath="/user/kexu/LogisticRegressionModel"
    model.save(sc,savePath)

    val newModel= LogisticRegressionModel.load(sc,savePath)
  }

}
