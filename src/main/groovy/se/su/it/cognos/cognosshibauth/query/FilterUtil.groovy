package se.su.it.cognos.cognosshibauth.query

import com.cognos.CAM_AAA.authentication.ISearchFilter
import com.cognos.CAM_AAA.authentication.ISearchFilterConditionalExpression
import com.cognos.CAM_AAA.authentication.ISearchFilterFunctionCall
import com.cognos.CAM_AAA.authentication.ISearchFilterRelationExpression

class FilterUtil {

  static String filterToString(ISearchFilter filter) {
    String ret = ""

    if (filter instanceof ISearchFilterConditionalExpression) {
      ret += filter.operator
      ret += filter.filters?.collect { filterToString(it) }.join("")
    }
    else if (filter instanceof ISearchFilterFunctionCall) {
      ret += filter.functionName
      ret += filter.parameters.join("")
    }
    else if (filter instanceof ISearchFilterRelationExpression) {
      ret += filter.constraint
      ret += filter.operator
      ret += filter.propertyName
    }

    ret
  }

  static List<String> findFilterObjectTypes(ISearchFilter searchFilter) {
    List<String> ret = null

    if (searchFilter instanceof ISearchFilterConditionalExpression) {
      ret = searchFilter.filters.collect { findFilterObjectTypes(it) }
      ret = ret.flatten()
    }
    else
      ret = [ getFilterObjectType(searchFilter) ]

    ret.removeAll {it == null}

    if (ret.empty && searchFilter instanceof ISearchFilterConditionalExpression)
      ret = ['account', 'group', 'role', 'folder', 'namespace']

    ret?.unique()
  }

  static String getFilterObjectType(ISearchFilter searchFilter) {
    String attribute = "", value = ""

    if (searchFilter.searchFilterType == ISearchFilter.RelationalExpression) {
      def filter = searchFilter as ISearchFilterRelationExpression
      attribute = filter.propertyName
      value = filter.constraint
    }
    else if (searchFilter.searchFilterType == ISearchFilter.FunctionCall) {
      def filter = searchFilter as ISearchFilterFunctionCall
      attribute = filter.parameters.first()
      value = filter.parameters[1]
    }

    if (attribute == "@objectClass" && value == null || attribute != "@objectClass")
      value = null

    value
  }
}
