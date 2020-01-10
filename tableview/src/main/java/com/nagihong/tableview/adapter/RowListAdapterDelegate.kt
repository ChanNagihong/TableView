package com.nagihong.tableview.adapter

import android.util.SparseArray
import android.view.ViewGroup
import androidx.core.util.contains
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.nagihong.tableview.RowListViewHolder
import com.nagihong.tableview.TableViewLog
import com.nagihong.tableview.element.Row
import com.nagihong.tableview.fallback.EmptyRow
import com.nagihong.tableview.fallback.RowListEmptyViewHolder
import com.nagihong.tableview.layoutmanager.ColumnsLayoutManager

abstract class RowListAdapterDelegate : IRowListDelegate {

    override var titleRow: Row<*>? = null
    override var stickyRows: MutableList<Row<*>?>? = null
    override var rows: MutableList<Row<*>?>? = null
    override var columnsLayoutManager: ColumnsLayoutManager? = null

    private val adapters = mutableListOf<RowListAdapter>()
    private val headerTypeRowMap = SparseArray<Row<*>>()
    private val typeRowMap = SparseArray<Row<*>>()

    abstract fun onBindViewHolder(
        holder: ViewHolder,
        row: Row<*>,
        layoutManager: ColumnsLayoutManager
    )

    override fun createViewHolder(
        parent: ViewGroup,
        viewType: Int,
        fromHeader: Boolean
    ): ViewHolder {
        if (viewType == IRowListDelegate.INVALID_VIEW_TYPE) {
            return RowListEmptyViewHolder(
                parent
            )
        }

        val type = (if (fromHeader) {
            getHeaderRowForType(viewType)
        } else {
            getRowForType(viewType)
        }) ?: return RowListEmptyViewHolder(
            parent
        )

        return RowListViewHolder(parent, type)
    }

    override fun bindViewHolder(
        holder: ViewHolder,
        position: Int,
        fromHeader: Boolean
    ) {
        if (holder is RowListEmptyViewHolder) {
            TableViewLog.e(
                this::class.java.name,
                "found an RowListEmptyViewHolder at $position${if (fromHeader) "(header)" else ""}"
            )
            return
        }

        val row = getRow(position, fromHeader)
        if (null == row) {
            TableViewLog.e(
                this::class.java.name,
                "row should not be null"
            )
            return
        }

        val layoutManager = columnsLayoutManager
        if (null == layoutManager) {
            TableViewLog.e(this::class.java.name, "layoutManager should not be null")
            return
        }

        onBindViewHolder(holder, row, layoutManager)
    }

    override fun getItemCount(fromHeader: Boolean): Int {
        return if (fromHeader) {
            (if (null == titleRow) 0 else 1) + (stickyRows?.size ?: 0)
        } else {
            rows?.size ?: 0
        }

    }

    override fun getItemViewType(
        position: Int,
        fromHeader: Boolean
    ): Int {
        return if (fromHeader) {
            if (position == 0) titleRow?.type() ?: 0
            else stickyRows?.get(position - 1)?.type() ?: 0
        } else {
            rows?.get(position)?.type() ?: 0
        }
    }

    override fun getItemId(
        position: Int,
        fromHeader: Boolean
    ): Long {
        return if (fromHeader) {
            if (position == 0) titleRow?.id() ?: 0
            else stickyRows?.get(position - 1)?.id() ?: 0
        } else {
            rows?.get(position)?.id() ?: 0
        }
    }

    override fun notifyDataSetChanged() {
        adapters.forEach { it.notifyDataSetChanged() }
    }

    override fun connect(adapter: RowListAdapter) {
        if (!adapters.contains(adapter)) {
            adapters.add(adapter)
        }
        adapter.delegate = this
    }

    private fun getRow(
        position: Int,
        fromHeader: Boolean
    ): Row<*>? {
        if (fromHeader) {
            if (null != titleRow && position == 0) return titleRow!!
            val relativePosition = if (null != titleRow) position - 1 else position
            return stickyRows?.get(relativePosition) ?: EmptyRow()
        } else {
            return rows?.get(position) ?: EmptyRow()
        }
    }

    private fun getRowForType(type: Int): Row<*>? {
        if (type == IRowListDelegate.INVALID_VIEW_TYPE) return EmptyRow()
        if (typeRowMap.contains(type)) return typeRowMap[type]
        rows
            ?.filterNotNull()
            ?.distinctBy { it.type() }
            ?.forEach { typeRowMap.put(it.type(), it) }
        return typeRowMap[type]
    }

    private fun getHeaderRowForType(type: Int): Row<*>? {
        if (type == IRowListDelegate.INVALID_VIEW_TYPE) return EmptyRow()
        if (headerTypeRowMap.contains(type)) return headerTypeRowMap[type]
        titleRow?.apply { headerTypeRowMap.put(type, this) }
        stickyRows
            ?.filterNotNull()
            ?.toList()
            ?.distinctBy { it.type() }
            ?.forEach { headerTypeRowMap.put(type, it) }
        return headerTypeRowMap[type]
    }

}