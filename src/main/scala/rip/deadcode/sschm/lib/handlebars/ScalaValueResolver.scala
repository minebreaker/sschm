package rip.deadcode.sschm.lib.handlebars

import com.github.jknack.handlebars.ValueResolver
import com.github.jknack.handlebars.context.MethodValueResolver

// Basically this delegates operations to MethodValueResolver,
// since case classes have Bean-like methods,
// But treats special conversion when Scala-specific types are returned.
// Handlebar should provide the specific means to convert values...
class ScalaValueResolver extends ValueResolver:

  private val delegate = MethodValueResolver.INSTANCE

  import scala.jdk.CollectionConverters.*

  override def resolve(context: Any, name: String): Any =
    val result = delegate.resolve(context, name)

    result match
      case Some(value)  => value
      case None         => null
      case xs: Seq[_]   => xs.asJava
      case m: Map[_, _] => m.asJava
      case _            => result

  override def resolve(context: Any): Any =
    delegate.resolve(context)

  override def propertySet(context: Any): java.util.Set[java.util.Map.Entry[String, AnyRef]] =
    delegate.propertySet(context)
