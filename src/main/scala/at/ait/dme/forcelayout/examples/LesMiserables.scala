package at.ait.dme.forcelayout.examples

import rapture.io._
import scala.io.Source
import java.awt.Dimension
import javax.swing.JFrame
import at.ait.dme.forcelayout.{ Edge, Node, SpringGraph }
import at.ait.dme.forcelayout.renderer.BufferedInteractiveGraphRenderer
import java.awt.Graphics2D
import java.awt.geom.Ellipse2D
import java.awt.Color
import java.awt.BasicStroke
import at.ait.dme.forcelayout.renderer.Node2D

object LesMiserables extends App {
  
  val json = Json.parse(Source.fromFile("src/test/resources/examples/miserables.json").mkString)
  
  val nodes: Seq[Node] = json.nodes.get[List[Json]].map(json => {
      val name = json.name.get[String].toString
      val group = json.group.get[Int]
      Node(name, name, 1.0, group)
    })
    
  val edges = json.links.get[List[Json]].map(json => {
    val value = json.value.get[Int]
    Edge(nodes(json.source.get[Int]), nodes(json.target.get[Int]), value.toDouble)
  })
    
  val graph = new SpringGraph(nodes, edges) 
  
  val vis = new BufferedInteractiveGraphRenderer(graph)
  
  val nodePainter = (nodes: Seq[Node2D], showLabels: Boolean, g2d: Graphics2D) => {
    nodes.foreach(n2d => {
      val (x, y, n) = (n2d.x, n2d.y, n2d.node)
      val size = Math.max(8, n.mass / 2)
      g2d.setColor(vis.palette(n.group % vis.palette.size))
      g2d.fill(new Ellipse2D.Double(x - size / 2, y - size / 2, size, size))
      g2d.setStroke(new BasicStroke(2));
      g2d.setColor(Color.WHITE)
      g2d.draw(new Ellipse2D.Double(x - size / 2, y - size / 2, size, size))
      
      if (showLabels) {
        g2d.setColor(Color.BLACK)
        g2d.drawString(n.label, x + 5, y - 2)
      } 
    })    
  }
  vis.setNodePainter(nodePainter)
    
  val frame = new JFrame("Les Miserables")
  frame.setPreferredSize(new Dimension(920,720))
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  frame.getContentPane().add(vis) 
  frame.pack()
  frame.setVisible(true)
  
  vis.start
  
}
