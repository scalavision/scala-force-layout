package at.ait.dme.forcelayout.examples

import scala.io.Source
import java.util.zip.GZIPInputStream
import java.io.BufferedInputStream
import java.io.FileInputStream
import at.ait.dme.forcelayout.Node
import at.ait.dme.forcelayout.Edge
import at.ait.dme.forcelayout.SpringGraph
import at.ait.dme.forcelayout.renderer.BufferedInteractiveGraphRenderer
import javax.swing.JFrame
import java.awt.Dimension

object UKWebHosts1996 extends App {

  print("Reading nodes ")
  val nodes = readGZippedData.map(record => {
    val from = record(1)
    val to = record(2).split("\t")(0)
    Seq(Node(from, from), Node(to, to))  
  }).flatten.toSeq.groupBy(_.id).mapValues(_.head)
  println("- " + nodes.size)
  
  print("Reading edges ")
  val edges = readGZippedData.map(record => {
    val from = nodes.get(record(1))
    val to = nodes.get(record(2).split("\t")(0))
    val size = record(2).split("\t")(1).toInt
    if (from.isDefined && to.isDefined)
      Edge(from.get, to.get, size)
    else
      null
  }).filter(_ != null).toSeq
  println("- " + edges.size)
  val graph = new SpringGraph(nodes.values.toSeq, edges) 
  
  val vis = new BufferedInteractiveGraphRenderer(graph)
  
  val frame = new JFrame("1996 UK Web Hosts")
  frame.setPreferredSize(new Dimension(920,720))
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  frame.getContentPane().add(vis) 
  frame.pack()
  frame.setVisible(true)
  
  vis.start
  
  def readGZippedData = 
    Source.fromInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream("src/test/resources/examples/1996-uk-web-hosts.tsv.gz")))).getLines.map(_.split("\\|"))
  
}