package service

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import com.aerospike.client.AerospikeClient
import com.jayway.awaitility.scala.AwaitilitySupport
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuiteLike}

import scala.concurrent.duration._

class AerospikeServiceTest extends TestKit(ActorSystem("testSystem"))
with FunSuiteLike
with BeforeAndAfterAll
with BeforeAndAfter
with ImplicitSender
with AwaitilitySupport {
  var aerospikeService: TestActorRef[AerospikeService] = _
  var client: AerospikeClient = _
  var config: Config = _

  override def beforeAll() = {
    config = ConfigFactory.load()
  }

  override def afterAll() = {
    TestKit.shutdownActorSystem(system, 3.seconds, verifySystemShutdown = true)
  }

  before {
    aerospikeService = TestActorRef(new AerospikeService(config))
  }

  test("queries client spent money from cache") {
    aerospikeService ! QueryMoneySpend(1)
    expectMsg(MoneySpend(10))
  }

  test("increments client money spend to cache") {
    ???
  }

  test("syncs cache with aerospike") {
    ???
  }

}
