package dao

import akka.actor.{Actor, ActorRef}
import akka.actor.Actor.Receive
import com.aerospike.client.listener.RecordListener
import com.aerospike.client.policy.GenerationPolicy
import com.aerospike.client._
import com.aerospike.client.async.AsyncClient
import com.typesafe.config.Config
import service.{ServiceProps}

import scala.util.Try

class AerospikeDao(config: Config, props: ServiceProps){
  val client = new AsyncClient(config.getString("aerospike.host"), config.getInt("aerospike.port"))

  def updateData(updateData: Map[Int, Int]) = {
    for ((clientId: Int, value: Int) <- updateData) if (value > 0) {
      var succeed = false

      while (!succeed) {
        val record = getRecord(clientId)

        val valueFromDb = record match {
          case Some(record: Record) => record.getInt("money_spent")
          case None => 0
        }

        val result = Try {
          write(record, new Key(props.namespace, props.statisticsSet, clientId), new Bin("money_spent", value + valueFromDb))
        }

        succeed = result.isSuccess
      }
    }
  }

  def getDataSync(clientId: Int) = getRecord(clientId).get.getInt("money_spent")

  def getRecord(clientId: Int): Option[Record] = {
    val key = new Key(props.namespace, props.statisticsSet, clientId)
    val policy = client.getAsyncReadPolicyDefault
    policy.timeout = 50

    client.get(policy, key) match {
      case record: Record => Some(record)
      case _ => None
    }
  }

  def getDataAsync(clientId: Int, listener: RecordListener) = {
    val key = new Key(props.namespace, props.statisticsSet, clientId)
    val policy = client.getAsyncReadPolicyDefault
    policy.timeout = 50

    client.get(policy, listener, key)
  }

  def write(record: Option[Record], key: Key, bin: Bin) = {
    val policy = client.getAsyncWritePolicyDefault // Initialize policy.
    policy.generationPolicy = GenerationPolicy.EXPECT_GEN_EQUAL
    policy.generation = record.fold(0) { record => record.generation }
    client.put(policy, key, bin)
  }

  def getDefaultAsyncReadListener(sender: ActorRef) = new AsyncReadListener(sender)

  class AsyncReadListener(sender: ActorRef) extends RecordListener {
    override def onFailure(exception: AerospikeException) = {
      sender ! exception
    }

    override def onSuccess(key: Key, record: Record) ={
      sender ! record.getInt("money_spent")
    }
  }
}
