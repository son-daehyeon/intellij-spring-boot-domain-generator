package com.github.son_daehyeon.spring_boot_domain_generator.generator

import com.github.son_daehyeon.spring_boot_domain_generator.model.DomainInfo
import com.github.son_daehyeon.spring_boot_domain_generator.model.EntityProperty
import com.intellij.psi.PsiDirectory

fun generateEntityFile(directory: PsiDirectory, domainInfo: DomainInfo, basePackage: String) {

    val className = domainInfo.domainName.replaceFirstChar { it.uppercase() }
    val content = generateContent(className, domainInfo.properties, basePackage)

    createJavaFile(directory, "${className}.java", content)
}

private fun generateContent(className: String, properties: List<EntityProperty>, basePackage: String): String {

    return """
package ${basePackage}.${className.lowercase()}.entity;

import ${getBaseSchemaClass(basePackage)};
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
${
        properties.mapNotNull { prop ->
            when (prop.type) {
                "LocalDate" -> "import java.time.LocalDate;"
                "LocalDateTime" -> "import java.time.LocalDateTime;"
                else -> null
            }
        }.toSet().joinToString("\n")
    }
@Entity
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
public class $className extends BaseSchema {
    
${
        properties.joinToString("\n\n") { prop ->

            val annotation = if (!prop.nullable) "@Column(nullable = false)" else "@Column"

            """
                $annotation
                ${prop.type} ${prop.name};
            """.trimIndent()
        }
    }
}
""".trimIndent()
}

private fun getBaseSchemaClass(`package`: String): String {

    val parts = `package`.split(".")
    val domainIndex = parts.indexOfFirst { it.contains("domain") }
    val baseParts = parts.take(domainIndex) + listOf("global", "infra", "mysql")
    return baseParts.joinToString(".") + ".BaseSchema"
}