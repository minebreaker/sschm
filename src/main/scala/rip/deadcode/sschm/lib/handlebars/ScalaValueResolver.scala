package rip.deadcode.sschm.lib.handlebars

import com.github.jknack.handlebars.ValueResolver
import com.github.jknack.handlebars.context.MethodValueResolver

// Basically this delegates operations to MethodValueResolver,
// since case classes have Bean-like methods,
// But treats special conversion when Scala-specific types are returned.
// Handlebar should provide the specific means to convert values...
class ScalaValueResolver extends ValueResolver:

  private val delegate = MethodValueResolver.INSTANCE

  override def resolve(context: Any, name: String): Any =
    val result = delegate.resolve(context, name)
    convertScalaValues(result)

  override def resolve(context: Any): Any =
    val result = delegate.resolve(context)
    convertScalaValues(result)

  override def propertySet(context: Any): java.util.Set[java.util.Map.Entry[String, AnyRef]] =
    delegate.propertySet(context)
