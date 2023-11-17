package rip.deadcode.sschm.lib.handlebars

import com.github.jknack.handlebars.ValueResolver
import com.github.jknack.handlebars.context.MapValueResolver

class ScalaMapResolver extends ValueResolver:

  private val delegate = MapValueResolver.INSTANCE

  import scala.jdk.CollectionConverters.*

  override def resolve(context: Any, name: String): Any =
    context match
      case context: Map[_, _] =>
        val result = delegate.resolve(context.asJava, name)
        convertScalaValues(result)
      case _ =>
        ValueResolver.UNRESOLVED

  override def resolve(context: Any): Any =
    context match
      case context: Map[_, _] =>
        val result = delegate.resolve(context.asJava)
        convertScalaValues(result)
      case _ =>
        ValueResolver.UNRESOLVED

  override def propertySet(context: Any): java.util.Set[java.util.Map.Entry[String, AnyRef]] =
    delegate.propertySet(context)
