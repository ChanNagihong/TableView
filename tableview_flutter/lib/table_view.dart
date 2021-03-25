import 'dart:math';

import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:tableview_flutter/no_glow_behavior.dart';
import 'package:tableview_flutter/table_view_def.dart';

import 'header_row.dart';
import 'table_column_layout.dart';
import 'table_specs.dart';

class TableView extends StatefulWidget {
  final HeaderRow headerRow;
  final TableSpecs specs;
  final ColumnGestureDetectorCreator? columnGestureDetectorCreator;
  final VoidCallback? onScrollToEndListener;
  final VoidCallback? onVerticalScrolledListener;
  final VoidCallback? onHorizontalScrolledListener;

  TableView(this.headerRow, this.specs,
      {this.columnGestureDetectorCreator,
      this.onScrollToEndListener,
      this.onVerticalScrolledListener,
      this.onHorizontalScrolledListener});

  @override
  State<StatefulWidget> createState() => _TableViewState();
}

class _TableViewState extends State<TableView> {
  @override
  void initState() {
    super.initState();
    if (widget.specs.stickyColumnsCount > 0) {
      for (var i = 0; i < widget.specs.stickyColumnsCount; i++) {
        widget.specs.viewColumnsWidthListener[i] = () {
          setState(() {});
        };
      }
    }
  }

  @override
  void dispose() {
    super.dispose();
    if (widget.specs.stickyColumnsCount > 0) {
      for (var i = 0; i < widget.specs.stickyColumnsCount; i++) {
        widget.specs.viewColumnsWidthListener[i] = null;
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    if (widget.specs.stickyColumnsCount <= 0) {
      return ScrollConfiguration(
        behavior: NoGlowBehavior(),
        child: ListView.builder(
          scrollDirection: Axis.horizontal,
          itemCount: widget.headerRow.columns.length,
          itemBuilder: (context, index) {
            return TableColumnLayout(widget.specs, widget.headerRow, index,
                false, widget.columnGestureDetectorCreator);
          },
        ),
      );
    }

    double stickyListViewWidth = 0;
    widget.specs.viewColumnsWidth
        .take(widget.specs.stickyColumnsCount)
        .forEach((element) {
      stickyListViewWidth += element;
    });
    return Row(
      children: widget.specs.enableColumnsDivider
          ? _buildWithDivider(
              stickyListViewWidth, widget.specs, widget.headerRow)
          : _buildWithoutDivider(
              stickyListViewWidth, widget.specs, widget.headerRow),
    );
  }

  List<Widget> _buildWithDivider(
      double stickyListViewWidth, TableSpecs specs, HeaderRow headerRow) {
    return [
      Container(
        width: stickyListViewWidth,
        child: ScrollConfiguration(
          behavior: NoGlowBehavior(),
          child: ListView.separated(
            separatorBuilder: (context, index) => specs.getVerticalDivider()!,
            scrollDirection: Axis.horizontal,
            itemCount: specs.stickyColumnsCount,
            itemBuilder: (context, index) {
              return TableColumnLayout(specs, headerRow, index, true,
                  widget.columnGestureDetectorCreator);
            },
          ),
        ),
      ),
      specs.getVerticalDivider()!,
      Expanded(
        child: ScrollConfiguration(
          behavior: NoGlowBehavior(),
          child: ListView.separated(
            separatorBuilder: (context, index) => specs.getVerticalDivider()!,
            scrollDirection: Axis.horizontal,
            itemCount:
                max(headerRow.columns.length - specs.stickyColumnsCount, 0),
            itemBuilder: (context, index) {
              return TableColumnLayout(
                  specs,
                  headerRow,
                  index + specs.stickyColumnsCount,
                  false,
                  widget.columnGestureDetectorCreator);
            },
          ),
        ),
      )
    ];
  }

  List<Widget> _buildWithoutDivider(
      double stickyListViewWidth, TableSpecs specs, HeaderRow headerRow) {
    return [
      Container(
        width: stickyListViewWidth,
        child: ScrollConfiguration(
          behavior: NoGlowBehavior(),
          child: ListView.builder(
            scrollDirection: Axis.horizontal,
            itemCount: specs.stickyColumnsCount,
            itemBuilder: (context, index) {
              return TableColumnLayout(specs, headerRow, index, true,
                  widget.columnGestureDetectorCreator);
            },
          ),
        ),
      ),
      Expanded(
        child: ScrollConfiguration(
          behavior: NoGlowBehavior(),
          child: NotificationListener<ScrollNotification>(
            onNotification: (notification) => _onScrollNotified(notification),
            child: ListView.builder(
              scrollDirection: Axis.horizontal,
              itemCount: headerRow.columns.length - specs.stickyColumnsCount,
              itemBuilder: (context, index) {
                return TableColumnLayout(
                    specs,
                    headerRow,
                    index + specs.stickyColumnsCount,
                    false,
                    widget.columnGestureDetectorCreator);
              },
            ),
          ),
        ),
      )
    ];
  }

  bool _onScrollNotified(ScrollNotification notification) {
    if (notification is ScrollEndNotification) {
      widget.onHorizontalScrolledListener?.call();
    }
    return true;
  }
}
