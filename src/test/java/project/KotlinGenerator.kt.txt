import org.junit.jupiter.api.Test
import java.util.*

data class Field(
    val name: String,
    val type: String,
    val columnName: String,
    val isPrimaryKey: Boolean,
    val isForeignKey: Boolean,
    val withGetters: Boolean,
    val withSetters: Boolean
)

class KotlinGenerator {
    @Test
    fun testMethod() {
        val projectName = "TestProject"
        val className = "Person"
        val tableName = "person"

        val fields = listOf(
            Field("id", "Long", "id", true, false, true, true),
            Field("firstName", "String", "first_name", false, false, true, true),
            Field("lastName", "String", "last_name", false, false, true, true),
            Field("age", "Int", "age", false, false, true, true),
            Field("dateNaissance", "LocalDate", "date_naissance", false, false, true, true),
            Field("adresse", "Adresse", "adresse_id", false, true, true, true)
        )

        val template = """
            package com.${projectName.lowercase()}.models

            import jakarta.persistence.*

            @Entity
            @Table(name = "$tableName")
            class ${className.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}(
            ${
            fields.joinToString(",\n") { field ->
                val annotations = mutableListOf<String>()
                if (field.isPrimaryKey) annotations.add("@Id")
                if (field.isPrimaryKey) annotations.add("@GeneratedValue(strategy = GenerationType.IDENTITY)")
                if (field.isForeignKey) annotations.add("@ManyToOne")
                annotations.add(
                    "@${if (field.isForeignKey) "JoinColumn(name = \"${field.columnName}\")" else "Column(name = \"${field.columnName}\")"}"
                )
                annotations.joinToString("\n    ") + "\n    val ${field.name}: ${field.type}"
            }.prependIndent("    ")
        }
            ) {

            ${
            fields.filter { it.withGetters || it.withSetters }.joinToString("\n\n") { field ->
                val getter = if (field.withGetters) {
                    """
                        fun get${field.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}(): ${field.type} = ${field.name}
                        """.trimIndent()
                } else ""

                val setter = if (field.withSetters) {
                    """
                        fun set${field.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}(value: ${field.type}) {
                            this.${field.name} = value
                        }
                        """.trimIndent()
                } else ""

                listOf(getter, setter).filter { it.isNotBlank() }.joinToString("\n\n").prependIndent("    ")
            }
        }
            }
        """.trimIndent()

        println(template)
    }
}
