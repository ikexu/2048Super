package graduation.util

/**
  * Created by KeXu on 2017/5/2.
  */
object CoreEnv {

  val kafkaBroker=Constant.getString("kafkaBroker").get
  val coreStreamingCheckPoint=Constant.getString("coreStreamingCheckPoint").get
  val master=Constant.getString("master").get
  val streamingDurationMs=Constant.getInt("streamingDurationMs").get

}
