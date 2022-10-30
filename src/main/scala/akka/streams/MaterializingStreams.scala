package akka.streams

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}

import scala.util.{Failure, Success}

object MaterializingStreams extends App {
  implicit val system = ActorSystem("MaterializingStreams")
  //Materializer is object that allocates the right objects to an akka stream
  implicit val materializer = ActorMaterializer()

  import system.dispatcher // Dispatcher is an execution context

  val simpleGraph = Source(1 to 10).to(Sink.foreach(println))
  // val simpleMaterializedValue = simpleGraph.run() // The type of simpleMaterializedValue = NotUsed == No meaningful value

  val source = Source(1 to 10)
  val sink = Sink.reduce[Int]((a, b) => a + b)
  // More meningful value => Sink[Int, Future[Int]] -- Means after computed, it will return a future of int val

  val sumFuture = source.runWith(sink) //runWith -- Connects source to sink and runs it as well
  //sumFuture is future[int]

  sumFuture.onComplete {
    case Success(value) => println(f"Sum of all elements is ${value}")
    case Failure(exception) => println(f"Sum of elements could not be computed: ${exception}")
  }


  //choosing materialized values
  val simpleSource = Source(1 to 10)
  val simpleFlow = Flow[Int].map(x => x + 1)
  val simpleSink = Sink.foreach[Int](println)
  simpleSource.viaMat(simpleFlow)((sourceMat, flowMat) => flowMat) // Way of choosign materialized values
  //Another way would be
  simpleSource.viaMat(simpleFlow)(Keep.right)
  simpleSource.viaMat(simpleFlow)(Keep.left)
  simpleSource.viaMat(simpleFlow)(Keep.both)

  val graph = simpleSource.viaMat(simpleFlow)(Keep.right).toMat(simpleSink)(Keep.right)
  //graph = Future[Done]

  graph.run().onComplete {
    case Success(_) => println("Stream processing finishes")
    case Failure(ex) => println(f"Stream processing failure: ${ex}")
  }

  //Syntactic Sugars
  val sum = Source(1 to 10).runWith(Sink.reduce[Int](_ + _))
  Source(1 to 10).runReduce(_ + _)

  //Running it backwards
  Sink.foreach[Int](println).runWith(Source.single(42))

  //Running it both ways
  Flow[Int].map(x => 2 * x).runWith(simpleSource, simpleSink)

  /**
   * 1. Return the last element of a source. Hint: (Sink.last)
   * 2. Compute the total word count of a stream of sentences
   */

  val result = Source(1 to 10).runWith(Sink.last[Int])
  result.onComplete {
    case Success(value) => println(value + " == " + 10)
    case _ => println("Value couldn't compute")
 }

  val sentences = List("I have the most words", "I play football", "Donal Trump is no longer the president", "Joe Biden is")
  Source(sentences).viaMat(Flow[String].map(x => x.split(" ").length))(Keep.left).runWith(Sink.foreach(println))

  system.terminate()
}
