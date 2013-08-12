/*
 * Copyright (c) 2013, IT Services, Stockholm University
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of Stockholm University nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

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
      ret += '(' + filter.filters?.collect { filterToString(it) }.join("") + ')'
    }
    else if (filter instanceof ISearchFilterFunctionCall) {
      ret += filter.functionName
      ret += filter.parameters.join("")
    }
    else if (filter instanceof ISearchFilterRelationExpression) {
      ret += filter.propertyName
      ret += filter.operator
      ret += filter.constraint
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
