<%@ include file="/WEB-INF/jsp/decl.jsp" %>

<tag:page>
  <ul class="menu">
    <li><a href="<c:url value="/accounts"/>">Accounts</a></li>
    <li><a href="<c:url value="/assetInfos"/>">Asset info</a></li>
    <li><a href="<c:url value="/transaction"/>">Add transactions</a></li>
    <li><a href="<c:url value="/dividends"/>">Dividend projections</a></li>
  </ul>

  <h1>Add transactions</h1>
  
  <form method="post" enctype="application/x-www-form-urlencoded" action="<c:url value="/transaction"/>">
    <input type="hidden" name="ret" value="${ret}"/>
    <input type="hidden" name="id" value="${id}"/>
    <input type="hidden" name="delDiv" value="${delDiv}"/>
    
    <table class="props">
      <tr>
        <th>Account</th>
        <td>
          <sst:select name="accountId" value="${accountId}" styleClass="ui-corner-all">
            <c:forEach items="${accounts}" var="account">
              <sst:option value="${account.id}">${account.name}</sst:option>
            </c:forEach>
          </sst:select>
        </td>
      </tr>
      <tr>
        <th>Date (yyyy/mm/dd)</th>
        <td><input type="text" id="xaDate" name="xaDate" value="${xaDate}" class="short ui-corner-all"/></td>
      </tr>
      <tr>
        <th>Transaction type</th>
        <td>
          <sst:select name="xaType" value="${xaType}" onchange="xaTypeChange()" styleClass="ui-corner-all">
            <c:forEach items="${xaTypes}" var="type">
              <sst:option value="${type.key}">${type.value}</sst:option>
            </c:forEach>
          </sst:select>
        </td>
      </tr>
      
      <tbody id="div-BUY" class="xaDiv hide">
        <tr>
          <th>Symbol</th>
          <td><input type="text" id="BUYSymbol" name="BUYSymbol" value="${BUYSymbol}" class="ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Shares</th>
          <td><input type="text" name="BUYShares" value="${BUYShares}" class="short number ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Price</th>
          <td><input type="text" name="BUYPrice" value="${BUYPrice}" class="short number ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Foreign exchange (from the PDF)</th>
          <td><input type="text" name="BUYFx" value="${BUYFx}" class="short number ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Fee</th>
          <td><input type="text" name="BUYFee" value="${BUYFee}" class="short number ui-corner-all"/></td>
        </tr>
      </tbody>
      
      <tbody id="div-BUYGIC" class="xaDiv hide">
        <tr>
          <th>Symbol</th>
          <td><input type="text" id="BUYGICSymbol" name="BUYGICSymbol" value="${BUYGICSymbol}" class="ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Description</th>
          <td><input type="text" id="BUYGICDescription" name="BUYGICDescription" value="${BUYGICDescription}" class="ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Amount</th>
          <td><input type="text" name="BUYGICAmount" value="${BUYGICAmount}" class="short number ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Rate (%)</th>
          <td><input type="text" name="BUYGICRate" value="${BUYGICRate}" class="short number ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Term (years)</th>
          <td><input type="text" name="BUYGICTerm" value="${BUYGICTerm}" class="short number ui-corner-all"/></td>
        </tr>
      </tbody>
      
      <tbody id="div-CASHADJ" class="xaDiv hide">
        <tr>
          <th>Amount</th>
          <td><input type="text" name="CASHADJAmount" value="${CASHADJAmount}" class="short number ui-corner-all"/></td>
        </tr>
      </tbody>
      
      <tbody id="div-CASHDIV" class="xaDiv hide">
        <tr>
          <th>Symbol</th>
          <td><input type="text" id="CASHDIVSymbol" name="CASHDIVSymbol" value="${CASHDIVSymbol}" class="ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Amount</th>
          <td><input type="text" name="CASHDIVAmount" value="${CASHDIVAmount}" class="short number ui-corner-all"/></td>
        </tr>
      </tbody>
      
      <tbody id="div-CONTRIBUTION" class="xaDiv hide">
        <tr>
          <th>Amount</th>
          <td><input type="text" name="CONTRIBUTIONAmount" value="${CONTRIBUTIONAmount}" class="short number ui-corner-all"/></td>
        </tr>
      </tbody>
      
      <tbody id="div-DEPOSIT" class="xaDiv hide">
        <tr>
          <th>Beneficiary</th>
          <td><input type="text" name="DEPOSITBene" value="${DEPOSITBene}" class="short ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Amount</th>
          <td><input type="text" name="DEPOSITAmount" value="${DEPOSITAmount}" class="short number ui-corner-all"/></td>
        </tr>
      </tbody>
      
      <tbody id="div-EXCHADJ" class="xaDiv hide">
        <tr>
          <th>Symbol</th>
          <td><input type="text" id="EXCHADJSymbol" name="EXCHADJSymbol" value="${EXCHADJSymbol}" class="ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Shares</th>
          <td><input type="text" name="EXCHADJShares" value="${EXCHADJShares}" class="short number ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Price</th>
          <td><input type="text" name="EXCHADJPrice" value="${EXCHADJPrice}" class="short number ui-corner-all"/></td>
        </tr>
      </tbody>
      
      <tbody id="div-FEE" class="xaDiv hide">
        <tr>
          <th>Amount</th>
          <td><input type="text" name="FEEAmount" value="${FEEAmount}" class="short number ui-corner-all"/></td>
        </tr>
      </tbody>
      
      <tbody id="div-FEE_REBATE" class="xaDiv hide">
        <tr>
          <th>Symbol</th>
          <td><input type="text" id="FEE_REBATESymbol" name="FEE_REBATESymbol" value="${FEE_REBATESymbol}" class="ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Amount</th>
          <td><input type="text" name="FEE_REBATEAmount" value="${FEE_REBATEAmount}" class="short number ui-corner-all"/></td>
        </tr>
      </tbody>
      
      <tbody id="div-GIC_REDEEMED" class="xaDiv hide">
        <tr>
          <th>Symbol</th>
          <td><input type="text" id="GIC_REDEEMEDSymbol" name="GIC_REDEEMEDSymbol" value="${GIC_REDEEMEDSymbol}" class="ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Amount</th>
          <td><input type="text" name="GIC_REDEEMEDAmount" value="${GIC_REDEEMEDAmount}" class="short number ui-corner-all"/></td>
        </tr>
      </tbody>
      
      <tbody id="div-GRANT" class="xaDiv hide">
        <tr>
          <th>Beneficiary</th>
          <td><input type="text" name="GRANTBene" value="${GRANTBene}" class="short ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Amount</th>
          <td><input type="text" name="GRANTAmount" value="${GRANTAmount}" class="short number ui-corner-all"/></td>
        </tr>
      </tbody>
      
      <tbody id="div-INTEREST" class="xaDiv hide">
        <tr>
          <th>Amount</th>
          <td><input type="text" name="INTERESTAmount" value="${INTERESTAmount}" class="short number ui-corner-all"/></td>
        </tr>
      </tbody>
      
      <tbody id="div-MANAGEMENT_FEE" class="xaDiv hide">
        <tr>
          <th>Symbol</th>
          <td><input type="text" id="MANAGEMENT_FEESymbol" name="MANAGEMENT_FEESymbol" value="${MANAGEMENT_FEESymbol}" class="ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Shares</th>
          <td><input type="text" name="MANAGEMENT_FEEShares" value="${MANAGEMENT_FEEShares}" class="short number ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Price</th>
          <td><input type="text" name="MANAGEMENT_FEEPrice" value="${MANAGEMENT_FEEPrice}" class="short number ui-corner-all"/></td>
        </tr>
      </tbody>
      
      <tbody id="div-MERGER" class="xaDiv hide">
        <tr>
          <th>From symbol</th>
          <td><input type="text" id="MERGERFromSymbol" name="MERGERFromSymbol" value="${MERGERFromSymbol}" class="ui-corner-all"/></td>
        </tr>
        <tr>
          <th>From shares</th>
          <td><input type="text" name="MERGERFromShares" value="${MERGERFromShares}" class="short number ui-corner-all"/></td>
        </tr>
        <tr>
          <th>To symbol</th>
          <td><input type="text" id="MERGERToSymbol" name="MERGERToSymbol" value="${MERGERToSymbol}" class="ui-corner-all"/></td>
        </tr>
        <tr>
          <th>To shares</th>
          <td><input type="text" name="MERGERToShares" value="${MERGERToShares}" class="short number ui-corner-all"/></td>
        </tr>
      </tbody>
      
      <tbody id="div-REINVDIV" class="xaDiv hide">
        <tr>
          <th>Symbol</th>
          <td><input type="text" id="REINVDIVSymbol" name="REINVDIVSymbol" value="${REINVDIVSymbol}" class="ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Shares</th>
          <td><input type="text" name="REINVDIVShares" value="${REINVDIVShares}" class="short number ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Price</th>
          <td><input type="text" name="REINVDIVPrice" value="${REINVDIVPrice}" class="short number ui-corner-all"/></td>
        </tr>
      </tbody>
      
      <tbody id="div-SELL" class="xaDiv hide">
        <tr>
          <th>Symbol</th>
          <td><input type="text" id="SELLSymbol" name="SELLSymbol" value="${SELLSymbol}" class="ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Shares</th>
          <td><input type="text" name="SELLShares" value="${SELLShares}" class="short number ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Price</th>
          <td><input type="text" name="SELLPrice" value="${SELLPrice}" class="short number ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Foreign exchange (from the PDF)</th>
          <td><input type="text" name="SELLFx" value="${SELLFx}" class="short number ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Fee</th>
          <td><input type="text" name="SELLFee" value="${SELLFee}" class="short number ui-corner-all"/></td>
        </tr>
      </tbody>
      
      <tbody id="div-SPLIT" class="xaDiv hide">
        <tr>
          <th>Symbol</th>
          <td><input type="text" id="SPLITSymbol" name="SPLITSymbol" value="${SPLITSymbol}" class="ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Shares</th>
          <td><input type="text" name="SPLITShares" value="${SPLITShares}" class="short number ui-corner-all"/></td>
        </tr>
      </tbody>
      
      <tbody id="div-SPLIT_CASH" class="xaDiv hide">
        <tr>
          <th>Symbol</th>
          <td><input type="text" id="SPLIT_CASHSymbol" name="SPLIT_CASHSymbol" value="${SPLIT_CASHSymbol}" class="ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Amount</th>
          <td><input type="text" name="SPLIT_CASHAmount" value="${SPLIT_CASHAmount}" class="short number ui-corner-all"/></td>
        </tr>
      </tbody>
      
      <tbody id="div-STOCKDIV" class="xaDiv hide">
        <tr>
          <th>Symbol</th>
          <td><input type="text" id="STOCKDIVSymbol" name="STOCKDIVSymbol" value="${STOCKDIVSymbol}" class="ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Amount</th>
          <td><input type="text" name="STOCKDIVAmount" value="${STOCKDIVAmount}" class="short number ui-corner-all"/></td>
        </tr>
      </tbody>
      
      <tbody id="div-TAX" class="xaDiv hide">
        <tr>
          <th>Symbol</th>
          <td><input type="text" id="TAXSymbol" name="TAXSymbol" value="${TAXSymbol}" class="ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Amount</th>
          <td><input type="text" name="TAXAmount" value="${TAXAmount}" class="short number ui-corner-all"/></td>
        </tr>
      </tbody>
      
      <tbody id="div-TRANSFER_IN" class="xaDiv hide">
        <tr>
          <th>In kind</th>
          <td><sst:checkbox id="TRANSFER_INInKind" name="TRANSFER_INInKind" onclick="transferInInKindClick()" value="${TRANSFER_INInKind}"/></td>
        </tr>
        
        <tr>
          <th>Cash amount</th>
          <td><input type="text" name="TRANSFER_INAmount" value="${TRANSFER_INAmount}" class="short number tiCash ui-corner-all"/></td>
        </tr>
        
        <tr>
          <th>Symbol</th>
          <td><input type="text" id="TRANSFER_INSymbol" name="TRANSFER_INSymbol" value="${TRANSFER_INSymbol}" class="tiKind ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Shares</th>
          <td><input type="text" name="TRANSFER_INShares" value="${TRANSFER_INShares}" class="short number tiKind ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Price</th>
          <td><input type="text" name="TRANSFER_INPrice" value="${TRANSFER_INPrice}" class="short number tiKind ui-corner-all"/></td>
        </tr>
        <tr>
          <th>Book value</th>
          <td><input type="text" name="TRANSFER_INBook" value="${TRANSFER_INbook}" class="short number tiKind ui-corner-all"/></td>
        </tr>
      </tbody>
      
      <tbody id="div-WITHDRAWAL" class="xaDiv hide">
        <tr>
          <th>Amount</th>
          <td><input type="text" name="WITHDRAWALAmount" value="${WITHDRAWALAmount}" class="short number ui-corner-all"/></td>
        </tr>
      </tbody>
    </table>
    
    <input type="submit" value="Save"/>
  </form>
  
  <c:if test="${!empty dateError}"><div class="error">${dateError}</div></c:if>
  <c:if test="${!empty xaException}"><div class="error">${xaException}</div></c:if>
</tag:page>

<script type="text/javascript">
$(function() {
	symbols = [
        <c:forEach items="${symbols}" var="symbol">"${symbol}",</c:forEach>
    ];
		
	$("#xaDate").datepicker({ dateFormat: "yy/mm/dd" });
	
    $("#BUYSymbol").autocomplete({ source: symbols });
    $("#CASHDIVSymbol").autocomplete({ source: symbols });
    $("#EXCHADJSymbol").autocomplete({ source: symbols });
    $("#FEE_REBATESymbol").autocomplete({ source: symbols });
    $("#MANAGEMENT_FEESymbol").autocomplete({ source: symbols });
    $("#MERGERFromSymbol").autocomplete({ source: symbols });
    $("#MERGERToSymbol").autocomplete({ source: symbols });
    $("#REINVDIVSymbol").autocomplete({ source: symbols });
    $("#SELLSymbol").autocomplete({ source: symbols });
    $("#SPLITSymbol").autocomplete({ source: symbols });
    $("#SPLIT_CASHSymbol").autocomplete({ source: symbols });
    $("#STOCKDIVSymbol").autocomplete({ source: symbols });
    $("#TAXSymbol").autocomplete({ source: symbols });
    $("#TRANSFER_INSymbol").autocomplete({ source: symbols });
});

function xaTypeChange() {
	$(".xaDiv").addClass("hide");
	var type = $get("xaType");
	$("#div-"+ type).removeClass("hide");
}

function transferInInKindClick() {
	if ($get("TRANSFER_INInKind")) {
		$(".tiCash").attr('disabled', 'disabled');
		$(".tiKind").removeAttr('disabled');
	}
	else {
		$(".tiCash").removeAttr('disabled');
		$(".tiKind").attr('disabled', 'disabled');
	}
}

xaTypeChange();
transferInInKindClick();
</script>