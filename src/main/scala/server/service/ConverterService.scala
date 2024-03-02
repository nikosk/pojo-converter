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

case class PojoField(name: String, dataType: String)
case class PojoMeta(className: String, fields: Seq[PojoField])
case class ConverterMeta(pojo: PojoMeta)

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
      .toSeq
      .head
  }

  def convert(codeFrom: String, codeTo: String): String = {
    val from = process(codeFrom)
    val to = process(codeTo)
    val buffer = new StringWriter()
    val writer = new JavaWriter(buffer)
    val paramName = s"${from.className.head.toString().toLowerCase()}${from.className.tail}"
    writer
      .emitPackage("com.example")
      .beginType(s"${from.className}Converter", "class", EnumSet.of(PUBLIC))
      .beginMethod(
        to.className,
        s"mapTo${to.className}",
        EnumSet.of(PUBLIC, STATIC),
        from.className, paramName
      )
      .emitStatement(buildStatement(from, to, paramName))
      .endMethod()
      .endType();
    buffer.getBuffer().toString()
  }

  private def buildStatement(from: PojoMeta, to: PojoMeta, paramName:String): String = {
    val statements = for {
      fromField <- from.fields
      toField <- to.fields
      if fromField.name == toField.name && fromField.dataType == toField.dataType
    } yield {
      s"\n.${toField.name}(${paramName}.get${fromField.name.capitalize}())"
    }
    s"${to.className}.builder().${statements.mkString}\n.build()"
  }
}
