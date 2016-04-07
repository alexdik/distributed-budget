package service

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestFSMRef, TestKit}
import cache.DbCache
import cache.DbCacheProps.{CacheData, CacheState}
import com.jayway.awaitility.scala.AwaitilitySupport
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuiteLike}
import service.AerospikeServiceEvents.{AddOrUpdateData, MakePurchase, SynchronizeData}

import scala.concurrent.duration._

class DbCacheTest extends TestKit(ActorSystem("testSystem"))
  with FunSuiteLike
  with BeforeAndAfterAll
  with BeforeAndAfter
  with ImplicitSender
  with AwaitilitySupport {

  var dbCache = TestFSMRef(new DbCache)
  val mustBeTypedProperly: TestActorRef[DbCache] = dbCache

  override def afterAll() = {
    TestKit.shutdownActorSystem(system, 3.seconds, verifySystemShutdown = true)
  }

  test("initial state and data of cache are correct") {
    assert(dbCache.stateName == CacheState)
    assert(dbCache.stateData == CacheData(Map()))
  }

  test("cache data returns on transition from full to empty cache state") {
    dbCache.setState(CacheState)
    dbCache ! MakePurchase(1, 1)
    dbCache ! SynchronizeData
    expectMsg(AddOrUpdateData(Map(1 -> 1)))
    assert(dbCache.stateData == CacheData(Map())
    )
  }
}

