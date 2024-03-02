package server.service
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import server.service.{ConverterService, PojoField, PojoMeta}

class ConverterServiceTest extends AnyFunSpec with Matchers {

  val javaClassSource1 = """
    public class User {
        private Long id;
        private String name;
        private String email;
    }
    """

  val javaClassSource2 = """
      public class UserDTO {
          private Long id;
          private String name;
          private String contactEmail;
      }
      """

  def normalize(s: String): String = s.replaceAll("\\s+", " ").trim

  describe("ConverterService") {
    describe("process method") {
      it("should parse a Java class source and create a PojoMeta") {
        val expectedResult = PojoMeta(
          className = "User",
          fields = Seq(
            PojoField("id", "Long"),
            PojoField("name", "String"),
            PojoField("email", "String")
          )
        )

        val pojoMeta = ConverterService.process(javaClassSource1)
        pojoMeta.className shouldEqual expectedResult.className
        pojoMeta.fields should contain theSameElementsAs expectedResult.fields
      }
    }

    describe("convert method") {
      it("should create a converter class source code string") {
        val expectedConverterCodeSnippet = "public class UserConverter"

        val converterCode =
          ConverterService.convert(javaClassSource1, javaClassSource2)
        converterCode should include(expectedConverterCodeSnippet)
      }

      it("should have a method that maps one class to another class") {
        val expectedMethodName = "mapToUserDTO"
        val expectedStatementParts = Seq(
          "UserDTO.builder()",
          ".id(user.getId())",
          ".name(user.getName())",
          ".build()"
        )

        val converterCode =
          ConverterService.convert(javaClassSource1, javaClassSource2)
        val normalizedConverterCode = normalize(converterCode)

        converterCode should include(expectedMethodName)
        expectedStatementParts.foreach { part =>
          normalizedConverterCode should include(normalize(part))
        }
      }
    }
  }
}
