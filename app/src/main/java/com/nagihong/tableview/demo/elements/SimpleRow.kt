package com.nagihong.tableview.demo.elements

import com.nagihong.tableview.element.Column
import com.nagihong.tableview.element.Row

class SimpleRow(val source: RowData, columns: List<Column>) : Row<Column>(columns) {

    override fun type(): Int {
        return 1
    }

    override fun id(): Long {
        return source.id.hashCode().toLong()
    }

    override fun visible(): Boolean {
        return true
    }

}