package se.su.it.cognos.cognosshibauth

import spock.lang.Specification
import org.junit.runner.RunWith
import org.spockframework.runtime.Sputnik
import se.su.it.cognos.cognosshibauth.query.FilterUtil
import spock.lang.Unroll
import com.cognos.CAM_AAA.authentication.ISearchFilterFunctionCall
import com.cognos.CAM_AAA.authentication.ISearchFilterRelationExpression
import com.cognos.CAM_AAA.authentication.ISearchFilterConditionalExpression
import se.su.it.cognos.cognosshibauth.ldap.Account

@RunWith(Sputnik)
class FilterUtilSpock extends Specification{

  @Unroll
  def "Test that filterToString on ( #functionName, #attribute, #value ) returns #expected"() {
    setup:
    def filter = null

    ISearchFilterFunctionCall functionCall = Mock()
    functionCall.functionName >> functionName
    functionCall.parameters >> [attribute, value]

    ISearchFilterRelationExpression relationExpression = Mock()
    relationExpression.operator >> functionName
    relationExpression.constraint >> value
    relationExpression.propertyName >> attribute

    ISearchFilterConditionalExpression conditionalExpression = Mock()
    conditionalExpression.operator >> functionName
    conditionalExpression.filters >> [relationExpression, functionCall]

    if(filterType == 'relationExpression') {
      filter = relationExpression
    }
    else if(filterType == 'functionCall') {
      filter = functionCall
    }
    else if(filterType == 'conditionalExpression') {
      filter = conditionalExpression
    }

    when:
    def filterString = FilterUtil.filterToString(filter)

    then:
    filterString == expected

    where:
    filterType           | functionName                                         | attribute   | value  | expected
    'functionCall'       | null                                                 | null        | null   | 'nullnullnull'
    'functionCall'       | null                                                 | '@userName' | null   | 'null@userNamenull'
    'functionCall'       | null                                                 | '@userName' | 'jolu' | 'null@userNamejolu'
    'functionCall'       | ISearchFilterFunctionCall.Contains                   | '@userName' | 'jolu' | 'contains@userNamejolu'
    'functionCall'       | ISearchFilterFunctionCall.Contains                   | null        | null   | 'containsnullnull'
    'functionCall'       | ISearchFilterFunctionCall.Contains                   | '@userName' | null   | 'contains@userNamenull'
    'functionCall'       | null                                                 | null        | null   | 'nullnullnull'
    'functionCall'       | null                                                 | '@userName' | null   | 'null@userNamenull'
    'functionCall'       | null                                                 | '@userName' | 'jolu' | 'null@userNamejolu'
    'functionCall'       | ISearchFilterFunctionCall.StartsWith                 | '@userName' | 'jolu' | 'starts-with@userNamejolu'
    'functionCall'       | ISearchFilterFunctionCall.StartsWith                 | null        | null   | 'starts-withnullnull'
    'functionCall'       | ISearchFilterFunctionCall.StartsWith                 | '@userName' | null   | 'starts-with@userNamenull'
    'functionCall'       | null                                                 | null        | null   | 'nullnullnull'
    'functionCall'       | null                                                 | '@userName' | null   | 'null@userNamenull'
    'functionCall'       | null                                                 | '@userName' | 'jolu' | 'null@userNamejolu'
    'functionCall'       | ISearchFilterFunctionCall.EndsWith                   | '@userName' | 'jolu' | 'ends-with@userNamejolu'
    'functionCall'       | ISearchFilterFunctionCall.EndsWith                   | null        | null   | 'ends-withnullnull'
    'functionCall'       | ISearchFilterFunctionCall.EndsWith                   | '@userName' | null   | 'ends-with@userNamenull'

    'relationExpression' | null                                                 | null        | null   | 'nullnullnull'
    'relationExpression' | null                                                 | '@userName' | null   | '@userNamenullnull'
    'relationExpression' | null                                                 | '@userName' | 'jolu' | '@userNamenulljolu'
    'relationExpression' | ISearchFilterRelationExpression.EqualTo              | '@userName' | 'jolu' | '@userName=jolu'
    'relationExpression' | ISearchFilterRelationExpression.EqualTo              | null        | null   | 'null=null'
    'relationExpression' | ISearchFilterRelationExpression.EqualTo              | '@userName' | null   | '@userName=null'
    'relationExpression' | ISearchFilterRelationExpression.GreaterThan          | '@userName' | 'jolu' | '@userName>jolu'
    'relationExpression' | ISearchFilterRelationExpression.GreaterThan          | null        | null   | 'null>null'
    'relationExpression' | ISearchFilterRelationExpression.GreaterThan          | '@userName' | null   | '@userName>null'
    'relationExpression' | ISearchFilterRelationExpression.GreaterThanOrEqual   | '@userName' | 'jolu' | '@userName>=jolu'
    'relationExpression' | ISearchFilterRelationExpression.GreaterThanOrEqual   | null        | null   | 'null>=null'
    'relationExpression' | ISearchFilterRelationExpression.GreaterThanOrEqual   | '@userName' | null   | '@userName>=null'
    'relationExpression' | ISearchFilterRelationExpression.LessThan             | '@userName' | 'jolu' | '@userName<jolu'
    'relationExpression' | ISearchFilterRelationExpression.LessThan             | null        | null   | 'null<null'
    'relationExpression' | ISearchFilterRelationExpression.LessThan             | '@userName' | null   | '@userName<null'
    'relationExpression' | ISearchFilterRelationExpression.LessThanOrEqual      | '@userName' | 'jolu' | '@userName<=jolu'
    'relationExpression' | ISearchFilterRelationExpression.LessThanOrEqual      | null        | null   | 'null<=null'
    'relationExpression' | ISearchFilterRelationExpression.LessThanOrEqual      | '@userName' | null   | '@userName<=null'
    'relationExpression' | ISearchFilterRelationExpression.NotEqual             | '@userName' | 'jolu' | '@userName!=jolu'
    'relationExpression' | ISearchFilterRelationExpression.NotEqual             | null        | null   | 'null!=null'
    'relationExpression' | ISearchFilterRelationExpression.NotEqual             | '@userName' | null   | '@userName!=null'

    'conditionalExpression' | null                                              | null        | null   | 'null(nullnullnullnullnullnull)'
    'conditionalExpression' | null                                              | '@userName' | null   | 'null(@userNamenullnullnull@userNamenull)'
    'conditionalExpression' | null                                              | '@userName' | 'jolu' | 'null(@userNamenulljolunull@userNamejolu)'
    'conditionalExpression' | ISearchFilterConditionalExpression.ConditionalAnd | null        | null   | 'and(nullandnullandnullnull)'
    'conditionalExpression' | ISearchFilterConditionalExpression.ConditionalAnd | '@userName' | null   | 'and(@userNameandnulland@userNamenull)'
    'conditionalExpression' | ISearchFilterConditionalExpression.ConditionalAnd | '@userName' | 'jolu' | 'and(@userNameandjoluand@userNamejolu)'
    'conditionalExpression' | ISearchFilterConditionalExpression.ConditionalOr  | null        | null   | 'or(nullornullornullnull)'
    'conditionalExpression' | ISearchFilterConditionalExpression.ConditionalOr  | '@userName' | null   | 'or(@userNameornullor@userNamenull)'
    'conditionalExpression' | ISearchFilterConditionalExpression.ConditionalOr  | '@userName' | 'jolu' | 'or(@userNameorjoluor@userNamejolu)'
  }
}
