package com.github.son_daehyeon.spring_boot_domain_generator.generator

import com.github.son_daehyeon.spring_boot_domain_generator.model.DomainInfo
import com.intellij.psi.PsiDirectory

fun generateRepositoryFile(directory: PsiDirectory, domainInfo: DomainInfo, basePackage: String) {

    val className = domainInfo.domainName.replaceFirstChar { it.uppercase() }
    val content = generateContent(className, basePackage)

    createJavaFile(directory, "${className}Repository.java", content)
}

private fun generateContent(
    className: String, basePackage: String
): String {

    return """
package ${basePackage}.${className.lowercase()}.repository;

import ${basePackage}.${className.lowercase()}.entity.${className};
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ${className}Repository extends JpaRepository<${className}, UUID> {
}
""".trimIndent()
}