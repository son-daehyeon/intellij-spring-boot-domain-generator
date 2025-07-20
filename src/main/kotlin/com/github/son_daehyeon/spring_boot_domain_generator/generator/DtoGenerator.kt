package com.github.son_daehyeon.spring_boot_domain_generator.generator

import com.github.son_daehyeon.spring_boot_domain_generator.model.DomainInfo
import com.github.son_daehyeon.spring_boot_domain_generator.model.EntityProperty
import com.intellij.psi.PsiDirectory
import org.atteo.evo.inflector.English

fun generateDtoFiles(
    directory: PsiDirectory, domainInfo: DomainInfo, basePackage: String
) {

    val className = domainInfo.domainName.replaceFirstChar { it.uppercase() }

    val requestDirectory = directory.createSubdirectory("request")
    val responseDirectory = directory.createSubdirectory("response")

    val upsertRequestContent = generateUpsertRequestContent(className, domainInfo.properties, basePackage)
    createJavaFile(requestDirectory, "Upsert${className}Request.java", upsertRequestContent)

    val defaultResponseContent = generateDefaultResponseContent(className, domainInfo.properties, basePackage)
    createJavaFile(responseDirectory, "${className}Response.java", defaultResponseContent)

    val simpleResponseContent = generateSimpleResponseContent(className, domainInfo.properties, basePackage)
    createJavaFile(responseDirectory, "Simple${className}Response.java", simpleResponseContent)

    val listResponseContent = generateListResponseContent(className, basePackage)
    createJavaFile(responseDirectory, "${className}ListResponse.java", listResponseContent)
}

fun generateDefaultResponseContent(className: String, properties: List<EntityProperty>, basePackage: String): String {

    return """
package ${basePackage}.${className.lowercase()}.dto.response;

import ${basePackage}.${className.lowercase()}.entity.${className};
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;
${
        properties.mapNotNull { prop ->
            when (prop.type) {
                "LocalDate" -> "import java.time.LocalDate;"
                "LocalDateTime" -> "import java.time.LocalDateTime;"
                else -> null
            }
        }.toSet().joinToString("\n")
    }
@Data
@AllArgsConstructor(staticName = "of")
public class ${className}Response {

    UUID id;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    ${
        properties.joinToString("\n") { prop ->

            "${prop.type} ${prop.name};"
        }
    }
    
    public static ${className}Response from(${className} ${className.lowercase()}) {

        return ${className}Response.of(
                ${className.lowercase()}.getId(),
                ${className.lowercase()}.getCreatedAt(),
                ${className.lowercase()}.getUpdatedAt(),
                ${
        properties.joinToString(",\n") {
            "${className.lowercase()}.get${it.name.replaceFirstChar { it -> it.uppercase() }}()"
        }
    }
        );
    }
}
""".trimIndent()
}

fun generateSimpleResponseContent(className: String, properties: List<EntityProperty>, basePackage: String): String {

    return """
package ${basePackage}.${className.lowercase()}.dto.response;

import ${basePackage}.${className.lowercase()}.entity.${className};
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;
${
        properties.mapNotNull { prop ->
            when (prop.type) {
                "LocalDate" -> "import java.time.LocalDate;"
                "LocalDateTime" -> "import java.time.LocalDateTime;"
                else -> null
            }
        }.toSet().joinToString("\n")
    }
@Data
@AllArgsConstructor(staticName = "of")
public class Simple${className}Response {

    UUID id;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    ${
        properties.filter { it -> it.simple }.joinToString("\n") { prop ->

            "${prop.type} ${prop.name};"
        }
    }
    
    public static Simple${className}Response from(${className} ${className.lowercase()}) {

        return Simple${className}Response.of(
                ${className.lowercase()}.getId(),
                ${className.lowercase()}.getCreatedAt(),
                ${className.lowercase()}.getUpdatedAt(),
                ${
        properties.filter { it -> it.simple }.joinToString(",\n") {
            "${className.lowercase()}.get${it.name.replaceFirstChar { it -> it.uppercase() }}()"
        }
    }
        );
    }
}
""".trimIndent()
}


fun generateListResponseContent(className: String, basePackage: String): String {

    return """
package ${basePackage}.${className.lowercase()}.dto.response;

import ${basePackage}.${className.lowercase()}.entity.${className};
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor(staticName = "of")
public class ${className}ListResponse {

    List<Simple${className}Response> ${English.plural(className.lowercase())};
    
    public static ${className}ListResponse from(List<${className}> ${English.plural(className.lowercase())}) {

        return ${className}ListResponse.of(${English.plural(className.lowercase())}.stream().map(Simple${className}Response::from).toList());
    }
}
""".trimIndent()
}

fun generateUpsertRequestContent(className: String, properties: List<EntityProperty>, basePackage: String): String {

    return """
package ${basePackage}.${className.lowercase()}.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
${
        properties.mapNotNull { prop ->
            when (prop.type) {
                "LocalDate" -> "import java.time.LocalDate;"
                "LocalDateTime" -> "import java.time.LocalDateTime;"
                else -> null
            }
        }.toSet().joinToString("\n")
    }
@Data
public class Upsert${className}Request {
    
${
        properties.joinToString("\n\n") { prop ->

            val validation = if (!prop.nullable) "@NotNull" else ""
            """
                    $validation
                    ${prop.type} ${prop.name};
                """.trimIndent()
        }
    }
}
""".trimIndent()
}