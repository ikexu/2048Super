package com.kexu.graduation.kafka

class KafkaConsumer(zkQuorum: String, group: String, topic: String, partition: Int, maxBatchByteSize: Int) {
  private val kac: KafkaSimpleConsumer = new KafkaSimpleConsumer(zkQuorum, group, topic, partition, maxBatchByteSize)
  var offset: Long = KafkaSimpleConsumer.getEndOffsetPositionFromZookeeper(group, zkQuorum, topic, partition)
  if (offset == 0L)
    offset = kac.getLatestOffset - 1
  if (offset == 0L)
    offset = -1

  private val queue = scala.collection.mutable.Queue[(Long, String)]()

  def fetchOneMessage(): Option[(Long, String)] = {
    if (queue.isEmpty) {
      val messageSet = kac.fetch(offset)
      val itr = messageSet.iterator
      while (itr.hasNext) {
        val messageAndOffset = itr.next()
        val payload = messageAndOffset.message.payload
        val bytes = new Array[Byte](payload.limit)
        payload.get(bytes)
        if (messageAndOffset.offset > offset) {
          queue.enqueue((messageAndOffset.offset, new String(bytes, "UTF-8")))
        }
      }
    }
    if (!queue.isEmpty) {
      val msg = queue.dequeue()
      offset = msg._1
      Some(msg)
    } else
      None
  }

  def fetchOneMessageForJava(): String = {
    fetchOneMessage() match {
      case Some(msg) => msg._2
      case None => null
    }
  }

  def commitOffsetToZookeeper(): Unit = {
    kac.commitOffsetToZookeeper(offset)
  }

  def close(): Unit = {
    kac.close
  }

}

object KafkaConsumer {
  def getTopicPartitionListForJava(zkQuorum: String, topic: String): Array[java.lang.Integer] = {
    val partitions = KafkaSimpleConsumer.getTopicPartitionList(zkQuorum, topic)
    val res = new Array[java.lang.Integer](partitions.size)
    for (idx <- 0 until partitions.size) {
      res(idx) = partitions(idx)
    }
    res
  }

  def main(args: Array[String]) {
    println("params: " + args.mkString(", "))
    val kc: KafkaConsumer = new KafkaConsumer(args(0), args(1), args(2), args(3).toInt, 1024)
    while (true) {
      kc.fetchOneMessage() match {
        case Some(msg) =>
          println(s"msg: ${msg._1}-->${msg._2}")
          kc.commitOffsetToZookeeper()
        case None =>
      }
      Thread.sleep(1000)
    }
  }

}