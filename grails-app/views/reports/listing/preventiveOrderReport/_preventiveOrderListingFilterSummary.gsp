<%@ page import="org.chai.memms.util.Utils.ReportSubType" %>
<%@ page import="org.chai.memms.preventive.maintenance.PreventiveOrder.PreventiveOrderStatus" %>
<%@ page import="org.chai.memms.preventive.maintenance.PreventiveOrder.PreventionResponsible" %>
<div class="v-tabs-criteria">

  <g:if test="${customizedReportName != null && !customizedReportName.empty}">
    <h1>${customizedReportName}</h1>
  </g:if>
  <g:else>
    <h1>${reportName}</h1>
  </g:else>

  <ul>
    <li>
      <span>Report Type:</span>
      <a href="#">${message(code:'reports.type.'+reportType?.reportType)}</a>
    </li>

    <li>
      <span>Report Subtype:</span>
      <a href="#">${message(code:'reports.subType.'+reportSubType?.reportSubType)}</a>
    </li>
%{--     <li>
      <span>Report Total:</span>
      <a href="#">TODO Preventions = ${entities?.size()}</a>
    </li> --}%
  </ul>
  
  <ul>
    <g:if test="${customizedReportName != null && !customizedReportName.empty}">
      <li>
        <span>Custom Report Filter Summary:</span>
        <a href="#"><g:message code="reports.dataLocation"/> = 
          ${customPreventiveOrderParams?.dataLocations?.size()}</a>,
        <a href="#"><g:message code="reports.department"/> = 
          ${customPreventiveOrderParams?.departments?.size()}</a>,
        <a href="#"><g:message code="reports.equipmentType"/> = 
          ${customPreventiveOrderParams?.equipmentTypes?.size()}</a>,
        <a href="#"><g:message code="reports.cost"/> = 
          ${customPreventiveOrderParams?.fromCost?:message(code:'reports.filters.none')} - 
          ${customPreventiveOrderParams?.toCost?:message(code:'reports.filters.none')}</a>,
        <a href="#"><g:message code="reports.currency"/> = 
          ${customPreventiveOrderParams?.costCurrency?:message(code:'reports.filters.none')}</a>,

        <g:if test="${reportSubType == ReportSubType.WORKORDERS}">
          <a href="#"><g:message code="reports.preventive.workOrders.whoIsResponsible"/> =   
            <g:if test="${customPreventiveOrderParams?.whoIsResponsible == null || customPreventiveOrderParams?.whoIsResponsible.empty}">
              ${message(code:'reports.filters.none')},
            </g:if>
            <g:else>
              <g:each in="${PreventionResponsible.values() - PreventionResponsible.NONE}" var="statusEnum">
                <g:if test="${customPreventiveOrderParams?.whoIsResponsible?.contains(statusEnum)}">
                  ${message(code: statusEnum?.messageCode+'.'+statusEnum?.name)},
                </g:if>
              </g:each>
            </g:else>
          </a>
          <a href="#"><g:message code="reports.workOrderStatus"/> =   
            <g:if test="${customPreventiveOrderParams?.workOrderStatus == null || customPreventiveOrderParams?.workOrderStatus.empty}">
              ${message(code:'reports.filters.none')},
            </g:if>
            <g:else>
              <g:each in="${PreventiveOrderStatus.values()}" var="statusEnum">
                <g:if test="${customPreventiveOrderParams?.workOrderStatus?.contains(statusEnum)}">
                  ${message(code: statusEnum?.messageCode+'.'+statusEnum?.name)},
                </g:if>
              </g:each>
            </g:else>
          </a>
          <a href="#"><g:message code="reports.workOrderPeriod"/> =   
            ${customPreventiveOrderParams?.fromWorkOrderPeriod?:message(code:'reports.filters.none')} - 
            ${customPreventiveOrderParams?.toWorkOrderPeriod?:message(code:'reports.filters.none')}</a>,        
        </g:if>
      </li>
    </g:if>
%{-- TODO
    <g:else>
      <li>
        <span>Report Filter Summary:</span>
        <a href="#">TODO</a>
      </li>
    </g:else> --}%
  </ul>
</div>