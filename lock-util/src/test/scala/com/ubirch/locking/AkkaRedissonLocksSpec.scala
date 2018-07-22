package com.ubirch.locking

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.pattern._
import akka.testkit.{ImplicitSender, TestActors, TestKit}
import akka.util.Timeout
import com.typesafe.scalalogging.slf4j.StrictLogging
import com.ubirch.locking.config.LockingConfig
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

class AkkaRedissonLocksSpec extends
  TestKit(ActorSystem("MyTestSystem1"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll
  with Matchers
  with StrictLogging {

  implicit val timeout: Timeout = Timeout(5 seconds)

  private val system2 = ActorSystem("MyTestSystem2")

  private val lockActor1 = system.actorOf(LockManagerTesterActor.props())
  private val lockActor2 = system2.actorOf(LockManagerTesterActor.props())

  override def afterAll {
    TestKit.shutdownActorSystem(system)
    TestKit.shutdownActorSystem(system2)
  }

  "basic akka test" must {

    "dummy test" in {
      1 shouldBe 1
    }

    "send back messages unchanged" in {
      val echo = system.actorOf(TestActors.echoActorProps)
      echo ! "hello world"
      expectMsg("hello world")
    }

    "simple locking inside an actor" in {

      lockActor1 ! "lock"

      expectMsg("okay")

      lockActor1 ! "unlock"
      expectMsg("okay")
    }

    "simple locking inside an other actor" in {


      val r = Await.result(lockActor2 ? "lock", 5 seconds)
      r match {
        case "okay" =>
          logger.debug("received okay")
          1 shouldBe 1
        case "nok" =>
          fail("received nok")
      }

      val r2 = Await.result(lockActor2 ? "unlock", 5 seconds)
      r2 match {
        case "okay" =>
          logger.debug("received okay")
          1 shouldBe 1
        case "nok" =>
          fail("received nok")
      }
    }

    "simple locking with 2 actors" in {

      val r = Await.result(lockActor2 ? "lock", 5 seconds)
      r match {
        case "okay" =>
          logger.debug("received okay")
          1 shouldBe 1
        case "nok" =>
          fail("received nok")
      }

      lockActor1 ! "lock"
      expectMsg("nok")

      lockActor1 ! "unlock"
      expectMsg("nok")

      lockActor1 ! "lock"
      expectMsg("nok")

      val r2 = Await.result(lockActor2 ? "unlock", 5 seconds)
      r2 match {
        case "okay" =>
          logger.debug("received okay")
          1 shouldBe 1
        case "nok" =>
          fail("received nok")
      }

      lockActor1 ! "lock"
      expectMsg("okay")

      lockActor1 ! "unlock"
      expectMsg("okay")
    }

  }
}

class LockManagerTesterActor extends Actor with ActorLogging {

  val redisson = LockingConfig.redisson
  val lockName = "lockingActor"

  override def receive: Receive = {
    case "lock" =>
      log.debug("try to get lock")
      if (!redisson.getLock(lockName).isLocked) {
        redisson.getLock(lockName).tryLock()
        sender ! "okay"
      }
      else
        sender ! "nok"
    case "unlock" =>
      log.debug("try unlock")
      if (redisson.getLock(lockName).isLocked && redisson.getLock(lockName).isHeldByCurrentThread) {
        redisson.getLock(lockName).unlock()
        sender ! "okay"
      }
      else
        sender ! "nok"
  }
}

object LockManagerTesterActor {
  def props(): Props = {
    Props(new LockTesterActor)
  }
}

