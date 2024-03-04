package server.service

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.symbolsolver.JavaSymbolSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import scala.collection.JavaConverters._
import scala.collection.mutable
import com.squareup.javawriter.JavaWriter
import java.io.StringWriter
import java.util.EnumSet
import javax.lang.model.element.Modifier.*

case class PojoField(name: String, dataType: String) {
  def rawType = {
    dataType.split("<") match
      case Array(hkt, _)  => hkt
      case Array(rawType) => rawType
  }

  def genericType = {
    dataType.split("<") match {
      case Array(_, genericType) => Some(genericType.replace(">", ""))
      case Array(_)              => None
    }
  }

  def isCollection = Constants.javaCollections.contains(rawType)

}
case class PojoMeta(className: String, fields: Seq[PojoField])
case class ConverterMeta(pojo: PojoMeta)
case class FieldType(typeStr: String)

object ConverterService {

  val combinedTypeSolver = new CombinedTypeSolver()
  val symbolSolver = new JavaSymbolSolver(combinedTypeSolver)

  def process(code: String): PojoMeta = {
    val cu = StaticJavaParser.parse(code)
    cu.findAll(classOf[ClassOrInterfaceDeclaration])
      .asScala
      .map { classDecl =>
        val className = classDecl.getNameAsString
        val fields = classDecl.getFields.asScala
          .flatMap(field =>
            field.getVariables.asScala.map { variable =>
              val dataType = variable.getTypeAsString
              val fieldName = variable.getNameAsString
              PojoField(fieldName, dataType)
            }
          )
          .toSeq
        PojoMeta(className, fields)
      }
      .head
  }

  def convert(codeFrom: String, codeTo: String): String = {
    val from = process(codeFrom)
    val to = process(codeTo)
    val buffer = new StringWriter()
    val writer = new JavaWriter(buffer)
    val paramName =
      s"${from.className.head.toString().toLowerCase()}${from.className.tail}"
    writer
      .emitPackage("com.example")
      .beginType(s"${from.className}Converter", "class", EnumSet.of(PUBLIC))
      .beginMethod(
        to.className,
        s"mapTo${to.className}",
        EnumSet.of(PUBLIC, STATIC),
        from.className,
        paramName
      )
      .emitStatement(buildStatement(from, to, paramName))
      .endMethod()
      .endType();
    buffer.getBuffer().toString()
  }

  private def buildStatement(
      from: PojoMeta,
      to: PojoMeta,
      paramName: String
  ): String = {
    val matchedFields = for {
      fromField <- from.fields
      toField <- to.fields
      if fromField.name == toField.name
    } yield (fromField, toField)
    val lines = matchedFields.flatMap { fs =>
      val from = fs._1
      val to = fs._2
      if (from.dataType == to.dataType) {
        Some(s"\n.${to.name}(${paramName}.get${from.name.capitalize}())")
      } else if (from.isCollection && to.isCollection) {
        val genericFrom = from.genericType
        val genericTo = to.genericType
        if (genericFrom.isDefined && genericTo.isDefined) {
          val fromType = genericFrom.get
          val toType = genericTo.get
          Some("\n" + s"""
                |.${to.name}(
                |    ${paramName}.get${from.name.capitalize}()
                |    .stream()
                |    .map(${fromType}Converter::mapTo${toType})
                |    .toList()
                |)
                """.stripMargin.trim())
        } else None
      } else if (!from.isCollection && !to.isCollection) {
        Some(s"\n.${to.name}(${from.dataType}Converter.mapTo${to.dataType}(${from.name}))")
      } else {
        None
      }
    }.toSeq
    s"return ${to.className}.builder()${lines.mkString}\n.build()"
  }

}
