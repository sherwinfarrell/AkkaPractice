package akka.streams

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.Future

object FirstPrinciples  extends  App{
  implicit val system = ActorSystem("FirstPrinciples")
  implicit val materializer = ActorMaterializer()
  val source = Source(1 to 10)
  val sink = Sink.foreach[Int](println)

  val graph = source.to(sink)
  graph.run()
  //flows
  val flow = Flow[Int].map(x => x + 1)
  val sourceWithFlow= source.via(flow)
  val flowWithSink = flow.to(sink)

  //Multiple ways of joining source, flow and sink -- Commented to not interfeer
//  sourceWithFlow.to(sink).run()
//  source.to(flowWithSink).run()
//  source.via(flow).to(sink).run()

  //Nulls are not allowed to be sent -- This will be commented to not throw errors
//  val illegalSource = Source.single[String](null)
//  illegalSource.to(Sink.foreach(println)).run()
  // Use options instead of nulls

  //Various Kinds of sources
  val finiteSource  = Source.single(1)
  val anotherFiniteSource = Source(List(1,2,3))
  val emptySource = Source.empty[Int]
  val infiniteSource = Source(Stream.from(1))

  import scala.concurrent.ExecutionContext.Implicits.global
  val futureSource = Source.fromFuture(Future(42))


  //Various Sink
  val theMostBoringSink = Sink.ignore
  val foreachSink = Sink.foreach[String](println)
  val headSInk = Sink.head[Int]
  val foldSink = Sink.fold[Int, Int](0)((a,b) => a + b)

  //Various Flows
  val mapFlow = Flow[Int].map(x => 2*x)
  val takeFlow = Flow[Int].take(5)
  // Do not have flatmap


  //Formal way of constructing akka streams
  // source -> flow -> flow -> ... -> sink

  val doubleFlowGraph = source.via(mapFlow).via(takeFlow).to(sink)
  doubleFlowGraph.run()

  //Syntactic Sugars
  val mapSource = Source(1 to 10).map(x => x * 2)
  //run the stream directly when attaching the sink using runForEach
  mapSource.runForeach(println)


  //Operations  == Components (used Interchangeably)

  /**
   * Exercise: Create a stream that takes the names of persions, then keep the first two names with length > 5 characters
   */

  val sourceList = List("Sandy", "Anderson", "Marie", "Callom", "Thomas")
  val exerSource = Source(sourceList)
  exerSource.filter(x => x.length>5).take(5).runForeach(println)


}
