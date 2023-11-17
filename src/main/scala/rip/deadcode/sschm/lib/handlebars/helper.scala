package rip.deadcode.sschm.lib.handlebars

import com.github.jknack.handlebars.{Context, Template}

private val resolver = new ScalaValueResolver

trait TemplateContext

private val scalaValueResolver = new ScalaValueResolver
private val scalaMapResolver = new ScalaMapResolver

extension (template: Template)
  def render(model: TemplateContext | Map[_, _]): String =
    val ctx = createContext(model)
    template.apply(ctx)

def createContext(model: TemplateContext | Map[_, _]): Context =
  Context
    .newBuilder(model)
    .resolver(scalaValueResolver, scalaMapResolver)
    .build()

private[handlebars] def convertScalaValues(obj: Any): Any =
  import scala.jdk.CollectionConverters.*
  obj match
    case Some(value)  => value
    case None         => null
    case xs: Seq[_]   => xs.asJava
    case m: Map[_, _] => m.asJava
    case _            => obj
