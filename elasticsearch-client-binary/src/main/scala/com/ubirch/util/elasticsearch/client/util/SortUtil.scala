package com.ubirch.util.elasticsearch.client.util

import org.elasticsearch.search.sort.{FieldSortBuilder, SortBuilder, SortBuilders, SortOrder}

/**
  * author: cvandrei
  * since: 2016-10-27
  */
object SortUtil {

  def sortBuilder(field: String, asc: Boolean = true): SortBuilder[FieldSortBuilder] = {

    val fieldSort: FieldSortBuilder = SortBuilders.fieldSort(field)

    asc match {
      case true => fieldSort.order(SortOrder.ASC)
      case false => fieldSort.order(SortOrder.DESC)
    }

  }

}
