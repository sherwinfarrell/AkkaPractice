## Akka Streams

### Basics

**Source = "Publisher"**  
- Emits elements asynchronously
- may or may not terminate

**Sink = "Subscriber"**  
- Receives elements
- terminates only when publisher terminates

**Flow = "Processor"**
- transforms elements


### Materializing

Components are static until they are run: 

``val graph = source.via(flow).to(sink)``

``val result = graph.run()``

result here is called a materialized value

 > Graph is a blueprint for a stream  
 Running a graph allocates right resources for dataflow  
 Running a graph is also called Materializing a graph
 

**Materializing a graph = Materiazling all values init**
- Each component produces a materialized value when run
- the graph produces a single materialized value
- Our job is to choose which value needs to be picked

**A component can materialize mutliple times**
- You can reuse the same component multiple times 
- different runs = different materializations

> Materialized value can be anything at all



