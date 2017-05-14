package graduation.util

import scalaj.http._

/**
  * Created by KeXu on 2017/5/7.
  */

object HttpUtil {

  def sendPost(url:String,message:String):HttpResponse[String]={
    val response: HttpResponse[String] = Http(url).postData(message)
      .header("Content-Type", "application/json").asString
    response
  }
}
