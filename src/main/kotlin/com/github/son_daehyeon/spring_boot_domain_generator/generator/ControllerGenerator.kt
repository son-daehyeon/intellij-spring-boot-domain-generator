package com.github.son_daehyeon.spring_boot_domain_generator.generator

import com.github.son_daehyeon.spring_boot_domain_generator.model.DomainInfo
import com.intellij.psi.PsiDirectory

fun generateControllerFile(directory: PsiDirectory, domainInfo: DomainInfo, basePackage: String) {

    val className = domainInfo.domainName.replaceFirstChar { it.uppercase() }
    val content = generateContent(className, basePackage)

    createJavaFile(directory, "${className}Controller.java", content)
}

private fun generateContent(className: String, basePackage: String): String {

    val content = """
package ${basePackage}.${className.lowercase()}.controller;

import ${basePackage}.${className.lowercase()}.dto.request.Upsert${className}Request;
import ${basePackage}.${className.lowercase()}.dto.response.${className}ListResponse;
import ${basePackage}.${className.lowercase()}.dto.response.${className}Response;
import ${basePackage}.${className.lowercase()}.service.${className}Service;
import ${getApiResponseClass(basePackage)};
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/${className.lowercase()}")
@RequiredArgsConstructor
@Tag(name = "[${className}]")
public class ${className}Controller {

    private final ${className}Service ${className.lowercase()}Service;

    @GetMapping
    @Operation(summary = "$className 목록 조회")
    public ApiResponse<${className}ListResponse> get${className}List() {

        return ApiResponse.ok(${className.lowercase()}Service.get${className}List());
    }

    @GetMapping("/{${className.lowercase()}Id}")
    @Operation(summary = "$className 상세 조회")
    public ApiResponse<${className}Response> get${className}(@PathVariable UUID ${className.lowercase()}Id) {

        return ApiResponse.ok(${className.lowercase()}Service.get${className}(${className.lowercase()}Id));
    }

    @PostMapping
    @Operation(summary = "$className 생성")
    public ApiResponse<${className}Response> create${className}(@RequestBody @Valid Upsert${className}Request request) {

        return ApiResponse.ok(${className.lowercase()}Service.create${className}(request));
    }

    @PutMapping("/{${className.lowercase()}Id}")
    @Operation(summary = "$className 수정")
    public ApiResponse<${className}Response> update${className}(
            @PathVariable UUID ${className.lowercase()}Id,
            @RequestBody @Valid Upsert${className}Request request
    ) {

        return ApiResponse.ok(${className.lowercase()}Service.update${className}(${className.lowercase()}Id, request));
    }

    @DeleteMapping("/{${className.lowercase()}Id}")
    @Operation(summary = "$className 삭제")
    public ApiResponse<Void> delete${className}(@PathVariable UUID ${className.lowercase()}Id) {

        ${className.lowercase()}Service.delete${className}(${className.lowercase()}Id);

        return ApiResponse.ok();
    }
}
""".trimIndent()

    return content
}

private fun getApiResponseClass(`package`: String): String {

    val parts = `package`.split(".")
    val domainIndex = parts.indexOfFirst { it.contains("domain") }
    val baseParts = parts.take(domainIndex) + listOf("global", "response")
    return baseParts.joinToString(".") + ".ApiResponse"
}