package graduation.kafka

import kafka.api.{FetchRequestBuilder, TopicMetadataRequest}
import kafka.common.TopicAndPartition
import kafka.consumer.SimpleConsumer
import kafka.message.ByteBufferMessageSet
import org.I0Itec.zkclient.ZkClient
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

class KafkaSimpleConsumer(
                           zkQuorum: String,
                           groupId: String,
                           topic: String,
                           partition: Int,
                           maxBatchByteSize: Int
                         ) extends Serializable {
  val logger = LoggerFactory.getLogger(KafkaSimpleConsumer.getClass)

  private var brokers: Seq[String] = _
  private var leader: String = _
  private var consumer: SimpleConsumer = null
  private val soTimeout = 60000
  private val bufferSize = maxBatchByteSize
  private var replicaBrokers: Seq[(String)] = null

  private def init(): Unit = {
    brokers = KafkaSimpleConsumer.getBrokers(zkQuorum)
    val data = findLeaderAndReplicaBrokers(brokers)
    leader = data._1
    replicaBrokers = data._2
    val ipPort = leader.split(":")
    val ip = ipPort(0)
    val port = ipPort(1).toInt
    consumer = new SimpleConsumer(ip, port, soTimeout, bufferSize, groupId)
  }

  private def getOffset(consumer: SimpleConsumer, topic: String,
                        partition: Int, whichTime: Long, clientId: Int): Long = {
    val topicAndPartition = new TopicAndPartition(topic, partition)
    consumer.earliestOrLatestOffset(topicAndPartition, whichTime, clientId)
  }

  def getEarliestOffset: Long = {
    if (consumer == null) {
      init()
    }
    getOffset(consumer, topic, partition, kafka.api.OffsetRequest.EarliestTime, 1)
  }

  def getLatestOffset: Long = {
    if (consumer == null) {
      init()
    }
    getOffset(consumer, topic, partition, kafka.api.OffsetRequest.LatestTime, 1)
  }

  private def findNewLeader(
                             oldLeader: String,
                             replicaBrokers: Seq[String]
                           ): (String, Seq[String]) = {
    for (i <- 0 until 3) {
      var goToSleep = false
      try {
        val data = findLeaderAndReplicaBrokers(replicaBrokers)
        val newLeader = data._1
        if (oldLeader.equalsIgnoreCase(newLeader) && i == 0) {
          goToSleep = true
        }
        return data
      } catch {
        case _: Throwable => goToSleep = true
      }
      if (goToSleep) {
        try {
          Thread.sleep(1000 * (i + 1))
        } catch {
          case _: Throwable =>
        }
      }
    }
    throw new Exception("Unable to find new leader after Broker failure. Exiting")
  }

  def fetch(startPositionOffset: Long): ByteBufferMessageSet = {
    if (consumer == null) {
      init()
    }
    val builder = new FetchRequestBuilder()
    val req = builder.addFetch(topic, partition, startPositionOffset, maxBatchByteSize)
      .clientId(groupId).build()
    val fetchResponse = consumer.fetch(req)
    var numErrors = 0
    if (fetchResponse.hasError) {
      numErrors = numErrors + 1
      val errorCode = fetchResponse.errorCode(topic, partition)
      if (errorCode == kafka.common.ErrorMapping.OffsetOutOfRangeCode)
        return fetchResponse.messageSet(topic, partition)
      if (numErrors > 5) {
        throw new Exception("Error fetching data from the Broker:" + leader + " Reason: " + errorCode, kafka.common.ErrorMapping.exceptionFor(errorCode))
      }
      close()
      if (errorCode > 5 && errorCode < 10) {
        val data = findNewLeader(leader, replicaBrokers)
        leader = data._1
        replicaBrokers = data._2
      }
      Thread.sleep(2000)
      init()
      return fetch(startPositionOffset)
    }
    fetchResponse.messageSet(topic, partition)
  }

  private def findLeaderAndReplicaBrokers(broker: String): (String, Seq[(String)]) = {
    var result: (String, Seq[String]) = null
    val tmp = broker.split(":")
    val ip = tmp(0)
    val port = tmp(1).toInt
    var consumer: SimpleConsumer = null
    try {
      consumer = new SimpleConsumer(ip, port, 100000, 64 * 1024, "leaderLookup")
      val req = new TopicMetadataRequest(Seq(topic), 1)
      val resp = consumer.send(req)
      val metaData = resp.topicsMetadata
      for (
        item <- metaData if result == null;
        part <- item.partitionsMetadata if part.partitionId == partition
      ) {
        part.leader match {
          case Some(leaderPart) =>
            result = (leaderPart.host + ":" + leaderPart.port, part.replicas.map(brk => (brk.host + ":"
              + brk.port)))
          case None =>
        }
      }
      result
    } catch {
      case e: Throwable => throw new Exception("Error communicating with Broker [" + broker
        + "] to find Leader for [" + topic + "] Reason: " + e)
    } finally {
      if (consumer != null) {
        consumer.close()
      }
    }
  }

  private def findLeaderAndReplicaBrokers(brokers: Seq[String]): (String, Seq[(String)]) = {
    var result: (String, Seq[String]) = null
    for (broker <- brokers if result == null) {
      try {
        result = findLeaderAndReplicaBrokers(broker)
      } catch {
        case e: Throwable => logger.error(s"find leader failed by broker: $broker. ", e)
      }
    }
    if (result == null) {
      throw new Exception("not found leader.")
    } else {
      result
    }
  }

  def close(): Unit = {
    if (consumer != null) {
      consumer.close()
    }
  }

  def commitOffsetToZookeeper(offset: Long) {
    val dir = "/consumers/" + groupId + "/offsets/" + topic + "/" + partition
    val zk = new ZkClient(zkQuorum, 30 * 1000, 30 * 1000)
    try {
      if (!zk.exists(dir)) {
        zk.createPersistent(dir, true)
      }
      zk.writeData(dir, offset.toString)
    } catch {
      case e: Throwable => logger.error("Error saving Kafka offset to Zookeeper dir: " + dir, e)
    } finally {
      zk.close()
    }
  }
}

object KafkaSimpleConsumer {
  val logger = LoggerFactory.getLogger(KafkaSimpleConsumer.getClass)

  private def getBrokerFromJson(json: String): String = {
    import scala.util.parsing.json.JSON
    val broker = JSON.parseFull(json)
    broker match {
      case Some(m: Map[String, Any]) =>
        m("host") + ":" + m("port").asInstanceOf[Double].toInt.toString
      case _ => throw new Exception("incorrect broker info in zookeeper")
    }
  }

  def getBrokers(zkQuorum: String): Seq[String] = {
    val list = new ArrayBuffer[String]()
    val dir = "/brokers/ids"
    val zk = new ZkClient(zkQuorum, 30 * 1000, 30 * 1000)
    try {
      if (zk.exists(dir)) {
        val ids = zk.getChildren(dir)
        for (id <- ids.asScala) {
          val json = zk.readData[String](dir + "/" + id)
          list.append(getBrokerFromJson(json))
        }
      }
    } catch {
      case e: Throwable => logger.error("Error reading Kafka brokers Zookeeper data", e)
    } finally {
      zk.close()
    }
    list.toSeq
  }

  def getEndOffsetPositionFromZookeeper(groupId: String, zkQuorum: String, topic: String,
                                        partition: Int): Long = {
    val dir = "/consumers/" + groupId + "/offsets/" + topic + "/" + partition
    val zk = new ZkClient(zkQuorum, 30 * 1000, 30 * 1000)
    try {
      if (zk.exists(dir)) {
        val offset = zk.readData[String](dir)
        return offset.toInt
      }
    } catch {
      case e: Throwable => logger.error("Error reading Kafka brokers Zookeeper data", e)
    } finally {
      zk.close()
    }
    0L
  }

  def getTopicPartitionList(zkQuorum: String, topic: String): Seq[Int] = {
    val list = new ArrayBuffer[Int]()
    val dir = "/brokers/topics/" + topic + "/partitions"
    val zk = new ZkClient(zkQuorum, 30 * 1000, 30 * 1000)
    try {
      if (zk.exists(dir)) {
        val ids = zk.getChildren(dir)
        for (id <- ids.asScala) {
          val idStr: String = id
          list.append(idStr.toInt)
        }
      }
    } catch {
      case e: Throwable => logger.error("Error reading Kafka partitions list Zookeeper data", e)
    } finally {
      zk.close()
    }
    list
  }
}
