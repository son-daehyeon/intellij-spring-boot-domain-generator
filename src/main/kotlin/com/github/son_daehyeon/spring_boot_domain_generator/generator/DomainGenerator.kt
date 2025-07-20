package com.github.son_daehyeon.spring_boot_domain_generator.generator

import com.github.son_daehyeon.spring_boot_domain_generator.model.DomainInfo
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleManager

fun generateDomainStructure(
    baseDirectory: PsiDirectory, domainInfo: DomainInfo, basePackageName: String
) {

    WriteAction.run<Throwable> {

        val packages = mapOf(
            "controller" to createPackage(baseDirectory, "controller"),
            "dto" to createPackage(baseDirectory, "dto"),
            "service" to createPackage(baseDirectory, "service"),
            "entity" to createPackage(baseDirectory, "entity"),
            "repository" to createPackage(baseDirectory, "repository"),
            "exception" to createPackage(baseDirectory, "exception")
        )

        generateEntityFile(packages["entity"]!!, domainInfo, basePackageName)
        generateDtoFiles(packages["dto"]!!, domainInfo, basePackageName)
        generateControllerFile(packages["controller"]!!, domainInfo, basePackageName)
        generateServiceFile(packages["service"]!!, domainInfo, basePackageName)
        generateRepositoryFile(packages["repository"]!!, domainInfo, basePackageName)
        generateExceptionFile(packages["exception"]!!, domainInfo, basePackageName)
    }
}

private fun createPackage(baseDirectory: PsiDirectory, packageName: String): PsiDirectory {

    return baseDirectory.findSubdirectory(packageName) ?: baseDirectory.createSubdirectory(packageName)
}

fun createJavaFile(directory: PsiDirectory, fileName: String, content: String) {

    WriteCommandAction.runWriteCommandAction(directory.project) {

        try {

            val psiFile: PsiFile = directory.createFile(fileName)
            val viewProvider = psiFile.viewProvider
            val document = viewProvider.document

            document?.setText(content)

            PsiDocumentManager.getInstance(directory.project).commitDocument(document!!)

            val codeStyleManager = CodeStyleManager.getInstance(directory.project)
            codeStyleManager.reformat(psiFile)
        } catch (e: Exception) {

            println("Failed to create and format file $fileName: ${e.message}")
            throw e
        }
    }
}
