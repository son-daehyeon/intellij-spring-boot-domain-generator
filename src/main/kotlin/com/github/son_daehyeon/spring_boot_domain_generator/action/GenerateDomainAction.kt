package com.github.son_daehyeon.spring_boot_domain_generator.action

import com.github.son_daehyeon.spring_boot_domain_generator.dialog.DomainGeneratorDialog
import com.github.son_daehyeon.spring_boot_domain_generator.generator.generateDomainStructure
import com.github.son_daehyeon.spring_boot_domain_generator.model.DomainInfo
import com.github.son_daehyeon.spring_boot_domain_generator.model.EntityProperty
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiDirectory

class GenerateDomainAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {

        val project = e.project ?: return
        val directory = e.getData(CommonDataKeys.PSI_ELEMENT) as? PsiDirectory ?: return

        val packageName = getPackageName(directory)
        if (!packageName.contains("domain")) {
            Messages.showErrorDialog(project, "올바른 패키지가 선택되지 않았습니다.", "오류")
            return
        }

        val dialog = DomainGeneratorDialog(project)
        if (dialog.showAndGet()) {

            val domainInfo = dialog.getDomainInfo()

            val domainNamePattern = Regex("^[a-zA-Z][a-zA-Z0-9]*$")
            val fieldNamePattern = Regex("^[a-zA-Z][a-zA-Z0-9_]*$")
            val typePattern =
                Regex("^(String|Integer|int|Long|long|Double|double|Float|float|Boolean|boolean|BigDecimal|LocalDate|LocalDateTime|UUID|List<\\w+>|Set<\\w+>|[A-Z][a-zA-Z0-9]*<?[a-zA-Z0-9<>,\\s]*>?)$")

            if (domainInfo.domainName.isBlank()) {
                Messages.showErrorDialog(project, "도메인명을 입력해주세요.", "오류")
                return
            }

            if (!domainNamePattern.matches(domainInfo.domainName)) {
                Messages.showErrorDialog(
                    project, "도메인명은 영어로 시작하고 영어와 숫자만 포함할 수 있습니다.\n예: User, OrderItem, ProductCategory", "오류"
                )
                return
            }

            if (domainInfo.properties.isEmpty()) {
                Messages.showErrorDialog(project, "속성을 하나 이상 입력해주세요.", "오류")
                return
            }

            for ((index, property) in domainInfo.properties.withIndex()) {
                val rowNumber = index + 1

                if (property.name.isBlank()) {
                    Messages.showErrorDialog(project, "${rowNumber}번째 행: 속성 이름을 입력해주세요.", "오류")
                    return
                }

                if (property.type.isBlank()) {
                    Messages.showErrorDialog(project, "${rowNumber}번째 행: 속성 타입을 입력해주세요.", "오류")
                    return
                }

                if (!fieldNamePattern.matches(property.name)) {
                    Messages.showErrorDialog(
                        project,
                        "${rowNumber}번째 행: 속성 이름이 올바르지 않습니다.\n" + "속성 이름은 영어로 시작하고 영어, 숫자, 언더스코어(_)만 포함할 수 있습니다.\n" + "예: name, firstName, user_id, createdAt",
                        "오류"
                    )
                    return
                }

                if (!typePattern.matches(property.type)) {
                    Messages.showErrorDialog(
                        project,
                        "${rowNumber}번째 행: 지원하지 않는 타입입니다.\n" + "지원 타입: String, Integer, Long, Double, Float, Boolean, BigDecimal, LocalDate, LocalDateTime, UUID, List<Type>, Set<Type>\n" + "입력된 타입: ${property.type}",
                        "오류"
                    )
                    return
                }
            }

            val duplicateNames = domainInfo.properties.groupBy { it.name.lowercase() }.filter { it.value.size > 1 }.keys

            if (duplicateNames.isNotEmpty()) {
                Messages.showErrorDialog(
                    project, "중복된 속성명이 있습니다: ${duplicateNames.joinToString(", ")}", "오류"
                )
                return
            }

            val normalizedDomainInfo = DomainInfo(
                domainName = domainInfo.domainName.lowercase(), properties = domainInfo.properties.map { property ->
                    EntityProperty(
                        name = property.name.lowercase(),
                        type = property.type,
                        nullable = property.nullable,
                        simple = property.simple
                    )
                })

            WriteCommandAction.runWriteCommandAction(project) {
                val subDirectory = directory.createSubdirectory(domainInfo.domainName.lowercase())
                generateDomainStructure(subDirectory, normalizedDomainInfo, packageName)
            }
        }
    }

    override fun update(e: AnActionEvent) {

        val psiElement = e.getData(CommonDataKeys.PSI_ELEMENT)
        e.presentation.isEnabledAndVisible = psiElement is PsiDirectory
    }

    override fun getActionUpdateThread(): ActionUpdateThread {

        return ActionUpdateThread.BGT
    }

    private fun getPackageName(directory: PsiDirectory): String {

        val path = directory.virtualFile.path
        return path.substringAfter("/src/main/java/").replace("/", ".")
    }
}
