package models.local

import
  akka.{ actor, pattern },
    actor.Actor,
    pattern.ask

import org.nlogo.api.{ WorldDimensions, ModelReader, ModelSection }

private[local] class CompilerManager extends Actor {

  import CompilerMessages._

  private var compiler             = NetLogoCompiler()
  private var (source, dimensions) = ("", WorldDimensions(-16, 16, -16, 16))

  // Indices of the dimensions in interface section of nlogo files
  val dimIndices = 17 to 20

  override def receive = {
    case Execute(agentType, cmd) => sender ! updateCompiler(compiler(agentType, cmd))
    case GetModelState           => sender ! updateCompiler(compiler.generateModelState(source, dimensions))
    case Open(nlogoContents)     => sender ! openModel(nlogoContents)
    case Compile(source)         => sender ! setActiveCode(source)
  }

  def openModel(nlogoContents: String): String = {
    val modelMap  = ModelReader.parseModel(nlogoContents)
    val interface = modelMap(ModelSection.Interface)
    val dims      = dimIndices map { x => interface(x).toInt} 
    val source    = modelMap(ModelSection.Code).mkString("\n")

    dimensions    = WorldDimensions(dims(0), dims(1), dims(2), dims(3))
    setActiveCode(source)
  }

  def setActiveCode(source: String): String = {
    this.source  = source
    val response = updateCompiler(compiler.generateModelState(source, dimensions))
    response
  }

  private def updateCompiler(jsAndCompiler: (String, NetLogoCompiler)): String = {
    val (js, newCompiler) = jsAndCompiler
    compiler = newCompiler
    js
  }

}

protected[local] object CompilerMessages {
  case class  Execute(agentType: String, cmd: String)
  case object GetModelState
  case class  Open(nlogoContents: String)
  case class  Compile(source: String)
}

