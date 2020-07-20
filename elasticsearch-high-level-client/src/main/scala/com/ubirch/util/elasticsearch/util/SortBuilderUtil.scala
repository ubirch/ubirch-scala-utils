package com.ubirch.util.elasticsearch.util

import org.elasticsearch.search.sort.{FieldSortBuilder, SortBuilder, SortBuilders, SortOrder}

object SortBuilderUtil {

  def sortBuilder(field: String, asc: Boolean = true): SortBuilder[FieldSortBuilder] = {

    val fieldSort: FieldSortBuilder = SortBuilders.fieldSort(field)

    if (asc) fieldSort.order(SortOrder.ASC)
    else fieldSort.order(SortOrder.DESC)

  }

}
