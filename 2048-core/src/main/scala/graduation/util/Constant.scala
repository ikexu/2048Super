package graduation.util

import java.io.{File, FileInputStream}
import java.util.Properties

import org.slf4j.LoggerFactory

/**
  * Created by KeXu on 2017/4/6.
  * 加载处理spark-prestat配置文件
  */
object Constant {

  private val conf_File_Name = "2048Super.properties"
  private val logger = LoggerFactory.getLogger(Constant.getClass)
  var prop: Properties = null


  def ConstantInit(): Unit = {
    prop = new Properties()
    var cfgFile: File = new File(conf_File_Name)
    if (!cfgFile.exists && ClassLoader.getSystemResource("") != null) {
      cfgFile = new File(ClassLoader.getSystemResource("").getPath + conf_File_Name)
    }
    if (!cfgFile.exists) {
      logger.error("参数配置文件" + cfgFile.getPath + "不存在，程序退出。")
      System.exit(1)
    }
    try {
      prop.load(new FileInputStream(cfgFile))
      val builder: StringBuilder = new StringBuilder
      import scala.collection.JavaConversions._
      for (key <- prop.stringPropertyNames) {
        builder.append("\n" + key + ":" + prop.get(key)).append(",")
      }
      logger.info("加载配置文件[" + cfgFile.getAbsolutePath + "]成功,配置列表:" + builder.toString)
    }
    catch {
      case e: Throwable => {
        logger.error(s"加载配置文件[${cfgFile.getPath}]出错：", e)
        System.exit(1)
      }
    }
  }

  def getString(key: String): Option[String] = {
    val value = prop.getProperty(key)
    Option(value)
  }

  def getInt(key: String): Option[Int] = {
    val value = prop.getProperty(key)
    if (value == null) {
      None
    } else {
      Some(value.toInt)
    }
  }

  def getDouble(key:String):Option[Double] = {
    val value = prop.getProperty(key)
    if (value == null) {
      None
    } else {
      Some(value.toDouble)
    }
  }

  def main(args: Array[String]): Unit = {

    ConstantInit()
    println(getInt("test1").getOrElse())
  }

}
