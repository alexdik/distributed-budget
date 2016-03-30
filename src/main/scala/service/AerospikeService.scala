package service

import akka.actor.Actor
import com.aerospike.client._
import com.aerospike.client.async.AsyncClient
import com.typesafe.config.Config
import com.typesafe.scalalogging.StrictLogging

case class QueryMoneySpend(clientId: Int)

case class MoneySpend(spent: Int)

object AerospikeService {
  val namespace = "test"
  val statisticsSet = "statistics"
  val moneySpent = new Key(namespace, statisticsSet, "moneySpent")
}

class AerospikeService(config: Config) extends Actor with StrictLogging {

  import AerospikeService._

  val client = new AsyncClient(config.getString("aerospike.host"), config.getInt("aerospike.port"))

  def receive: Receive = {
    case QueryMoneySpend(1) =>
      sender ! MoneySpend(10)
  }

}
