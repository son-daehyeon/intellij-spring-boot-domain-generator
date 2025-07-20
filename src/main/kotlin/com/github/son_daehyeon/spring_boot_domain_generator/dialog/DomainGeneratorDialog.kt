package com.github.son_daehyeon.spring_boot_domain_generator.dialog

import com.github.son_daehyeon.spring_boot_domain_generator.model.DomainInfo
import com.github.son_daehyeon.spring_boot_domain_generator.model.EntityProperty
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.JBTable
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*
import javax.swing.table.DefaultTableModel

class DomainGeneratorDialog(project: Project) : DialogWrapper(project) {

    private val tableModel = DefaultTableModel()
    private val table = JBTable(tableModel)
    private lateinit var domainNameField: JTextField

    init {

        title = "도메인 생성기"
        init()
        setupTable()
    }

    private fun setupTable() {

        tableModel.addColumn("속성명")
        tableModel.addColumn("타입")
        tableModel.addColumn("Nullable")
        tableModel.addColumn("상세 조회 시 표시")

        tableModel.addRow(arrayOf("", "String", false, true))

        table.setShowGrid(true)
        table.rowSelectionAllowed = true
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)

        table.columnModel.getColumn(0).preferredWidth = 150
        table.columnModel.getColumn(1).preferredWidth = 150
        table.columnModel.getColumn(2).preferredWidth = 80
        table.columnModel.getColumn(3).preferredWidth = 80
    }

    override fun createCenterPanel(): JComponent {

        val panel = JPanel(BorderLayout())

        val topPanel = JPanel()
        topPanel.add(JLabel("도메인명:"))
        domainNameField = JTextField(20)
        topPanel.add(domainNameField)

        val tablePanel = ToolbarDecorator.createDecorator(table)
            .setAddAction { addRow() }
            .setRemoveAction { removeRow() }
            .setMoveUpAction { moveRowUp() }
            .setMoveDownAction { moveRowDown() }
            .createPanel()

        tablePanel.preferredSize = Dimension(500, 300)

        panel.add(topPanel, BorderLayout.NORTH)
        panel.add(tablePanel, BorderLayout.CENTER)

        return panel
    }

    private fun addRow() {

        tableModel.addRow(arrayOf("", "String", false, true))
        val lastRow = tableModel.rowCount - 1
        table.setRowSelectionInterval(lastRow, lastRow)
        table.editCellAt(lastRow, 0)
    }

    private fun removeRow() {

        val selectedRow = table.selectedRow
        if (selectedRow >= 0) {
            tableModel.removeRow(selectedRow)
        }
    }

    private fun moveRowUp() {

        val selectedRow = table.selectedRow
        if (selectedRow > 0) {
            tableModel.moveRow(selectedRow, selectedRow, selectedRow - 1)
            table.setRowSelectionInterval(selectedRow - 1, selectedRow - 1)
        }
    }

    private fun moveRowDown() {

        val selectedRow = table.selectedRow
        if (selectedRow >= 0 && selectedRow < tableModel.rowCount - 1) {
            tableModel.moveRow(selectedRow, selectedRow, selectedRow + 1)
            table.setRowSelectionInterval(selectedRow + 1, selectedRow + 1)
        }
    }

    fun getDomainInfo(): DomainInfo {

        val properties = mutableListOf<EntityProperty>()

        for (i in 0 until tableModel.rowCount) {
            val name = tableModel.getValueAt(i, 0) as? String ?: ""
            val type = tableModel.getValueAt(i, 1) as? String ?: ""
            val nullable = tableModel.getValueAt(i, 2) as? Boolean ?: false
            val simple = tableModel.getValueAt(i, 3) as? Boolean ?: true

            if (name.isNotBlank() && type.isNotBlank()) {
                properties.add(EntityProperty(name, type, nullable, simple))
            }
        }

        return DomainInfo(
            domainName = domainNameField.text.trim(),
            properties = properties
        )
    }
}
