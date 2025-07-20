package com.github.son_daehyeon.spring_boot_domain_generator.generator

import com.github.son_daehyeon.spring_boot_domain_generator.model.DomainInfo
import com.github.son_daehyeon.spring_boot_domain_generator.model.EntityProperty
import com.intellij.psi.PsiDirectory

fun generateServiceFile(directory: PsiDirectory, domainInfo: DomainInfo, basePackage: String) {

    val className = domainInfo.domainName.replaceFirstChar { it.uppercase() }
    val content = generateContent(className, domainInfo.properties, basePackage)

    createJavaFile(directory, "${className}Service.java", content)
}

private fun generateContent(className: String, properties: List<EntityProperty>, basePackage: String): String {

    val content = """
package ${basePackage}.${className.lowercase()}.service;

import ${basePackage}.${className.lowercase()}.dto.request.Upsert${className}Request;
import ${basePackage}.${className.lowercase()}.dto.response.${className}ListResponse;
import ${basePackage}.${className.lowercase()}.dto.response.${className}Response;
import ${basePackage}.${className.lowercase()}.entity.${className};
import ${basePackage}.${className.lowercase()}.exception.${className}Exceptions;
import ${basePackage}.${className.lowercase()}.repository.${className}Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ${className}Service {

    private final ${className}Repository ${className.lowercase()}Repository;

    @Transactional(readOnly = true)
    public ${className}ListResponse get${className}List() {

        return ${className}ListResponse.from(${className.lowercase()}Repository.findAll());
    }

    @Transactional(readOnly = true)
    public ${className}Response get${className}(UUID ${className.lowercase()}Id) {

        return ${className.lowercase()}Repository.findById(${className.lowercase()}Id)
                .map(${className}Response::from)
                .orElseThrow(${className}Exceptions.${className.uppercase()}_NOT_FOUND::toException);
    }

    @Transactional
    public ${className}Response create${className}(Upsert${className}Request request) {

        $className ${className.lowercase()} = ${className.lowercase()}Repository.save(
                ${className}.builder()
                        ${
        properties.joinToString("\n") { it ->
            ".${it.name}(request.get${it.name.replaceFirstChar { it.uppercase() }}())"
        }
    }
                        .build()
        );

        return ${className}Response.from(${className.lowercase()});
    }

    @Transactional
    public ${className}Response update${className}(UUID ${className.lowercase()}Id, Upsert${className}Request request) {

        $className ${className.lowercase()} = ${className.lowercase()}Repository.findById(${className.lowercase()}Id).orElseThrow(${className}Exceptions.${className.uppercase()}_NOT_FOUND::toException);

        $className new${className} = ${className.lowercase()}Repository.save(
                ${className.lowercase()}.toBuilder()
                        ${
        properties.joinToString("\n") { it ->
            ".${it.name}(request.get${it.name.replaceFirstChar { it.uppercase() }}())"
        }
    }
                        .build()
        );

        return ${className}Response.from(new${className});
    }

    @Transactional
    public void delete${className}(UUID ${className.lowercase()}Id) {

        $className ${className.lowercase()} = ${className.lowercase()}Repository.findById(${className.lowercase()}Id).orElseThrow(${className}Exceptions.${className.uppercase()}_NOT_FOUND::toException);

        ${className.lowercase()}Repository.delete(${className.lowercase()});
    }
}
""".trimIndent()

    return content
}