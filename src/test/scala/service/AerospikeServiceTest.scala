package service

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import com.aerospike.client.AerospikeClient
import com.jayway.awaitility.scala.AwaitilitySupport
import com.typesafe.config.{Config, ConfigFactory}
import dao.AerospikeDao
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuiteLike}
import service.AerospikeServiceEvents.{GetData, MakePurchase, SynchronizeData}

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
    aerospikeService = TestActorRef(new AerospikeService((config)))
  }

  test("queries client spent money from cache") {
    aerospikeService ! MakePurchase(1, 0)
    aerospikeService ! GetData(1)
    expectMsg(_: Int)
  }

  test("service synchronize data with cache") {
    val dao = new AerospikeDao(config, AerospikeServiceProps)
    val increment = 100

    dao.updateData(Map(1->0))
    val before = dao.getDataSync(1)

    aerospikeService.receive(MakePurchase(1, increment))
    aerospikeService.receive(SynchronizeData)
    TimeUnit.SECONDS.sleep(1)

    assert(dao.getDataSync(1) - before == increment)
  }
}
