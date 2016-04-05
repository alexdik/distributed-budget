package service

import akka.actor.{FSM, ActorSystem}
import akka.testkit.{ImplicitSender, TestActorRef, TestFSMRef, TestKit}
import cache.{DbCache, DbCacheProps}
import com.jayway.awaitility.scala.AwaitilitySupport
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuiteLike}
import service.AerospikeServiceEvents.{AddOrUpdateData, SynchronizeData, MakePurchase}
import DbCacheProps.{NonEmptyCache, CacheData, EmptyCache}

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
    assert(dbCache.stateName == EmptyCache)
    assert(dbCache.stateData == CacheData(Map()))
  }

  test("cache change its state to NonEmpty and vice versa") {
    dbCache.setState(EmptyCache)
    dbCache ! MakePurchase(1, 1)
    assert(dbCache.stateName == NonEmptyCache)
    dbCache ! SynchronizeData
    assert(dbCache.stateName == EmptyCache
    )
  }

  test("cache data returns on transition from full to empty cache state") {
    dbCache.setState(EmptyCache)
    dbCache ! MakePurchase(1, 1)
    dbCache.setState(EmptyCache)
    expectMsg(AddOrUpdateData(Map(1 -> 1)))
  }
}

