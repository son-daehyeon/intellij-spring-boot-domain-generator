package com.github.son_daehyeon.spring_boot_domain_generator.generator

import com.github.son_daehyeon.spring_boot_domain_generator.model.DomainInfo
import com.intellij.psi.PsiDirectory

fun generateExceptionFile(directory: PsiDirectory, domainInfo: DomainInfo, basePackage: String) {

    val className = domainInfo.domainName.replaceFirstChar { it.uppercase() }
    val content = generateContent(className, basePackage)

    createJavaFile(directory, "${className}Exceptions.java", content)
}

private fun generateContent(className: String, basePackage: String): String {

    return """
package ${basePackage}.${className.lowercase()}.exception;

import ${getBaseExceptionClass(basePackage)};
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ${className}Exceptions implements ApiExceptionCode {
    
    ${className.uppercase()}_NOT_FOUND("해당 ${className}을(를) 찾을 수 없습니다."),
    ;

    private final String message;
}
""".trimIndent()
}

private fun getBaseExceptionClass(`package`: String): String {

    val parts = `package`.split(".")
    val domainIndex = parts.indexOfFirst { it.contains("domain") }
    val baseParts = parts.take(domainIndex) + listOf("global", "exception")
    return baseParts.joinToString(".") + ".ApiExceptionCode"
}