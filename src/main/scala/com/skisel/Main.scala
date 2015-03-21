package com.skisel

import akka.actor._
import akka.kernel.Bootable
import com.skisel.Pinger.Ping

import scala.concurrent.{Await, Future}

/**
 * User: sergeykisel
 * Date: 21/03/15
 * Time: 11:36
 */
object Main extends App {

  start(new Master)

  def start(container: ActorContainer): ActorContainer = {
    container.startup()
    addShutdownHook(List(container))
    container
  }

  private def addShutdownHook(bootables: Seq[Bootable]): Unit = {
    Runtime.getRuntime.addShutdownHook(new Thread(new Runnable {
      def run = {
        println("")
        println("Shutting down Akka...")

        for (bootable ← bootables) {
          println("Shutting down " + bootable.getClass.getName)
          bootable.shutdown()
        }

        println("Successfully shut down Akka")
      }
    }))
  }
}


trait ActorContainer extends Bootable {
  def system: ActorSystem

}


class Master extends ActorContainer {
  val system: ActorSystem = ActorSystem("autorestart")

  override def startup(): Unit = {
    system.actorOf(Props[Pinger], "pinger")
    system.actorOf(Props[Destroyer], "destroyer")
  }

  override def shutdown(): Unit = {
    import scala.concurrent.duration._
    val whenTerminated: Future[Terminated] = system.terminate()
    Await.result(whenTerminated, 15 seconds)
  }
}

case object Destroy

class Destroyer extends Actor {

  import scala.concurrent.duration._
  import context.dispatcher

  context.system.scheduler.scheduleOnce(30 seconds, self, Destroy)

  override def receive: Receive = {
    case Destroy =>
      context.system.terminate()
  }
}

class Pinger extends Actor {

  import scala.concurrent.duration._
  import context.dispatcher

  context.system.scheduler.schedule(1 second, 1 second, self, Ping)

  override def receive: Receive = {
    case Ping =>
      println("ping")
  }
}

object Pinger {

  case object Ping

}