package graduation.util

import scalaj.http._

/**
  * Created by KeXu on 2017/5/7.
  */
class HttpUtil {

  val uri=CoreEnv.httpPostUri

  def sendPost(message:String):HttpResponse[String]={
      val response: HttpResponse[String] = Http(uri).postData(message)
        .header("Content-Type", "application/json").asString
      response
  }
}

object HttpUtil {

  def main(args: Array[String]): Unit = {
    Constant.ConstantInit()
    new HttpUtil().sendPost("123")
  }

}
