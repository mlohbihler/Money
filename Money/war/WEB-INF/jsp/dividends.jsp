<%@ include file="/WEB-INF/jsp/decl.jsp" %>

<tag:page>
  <ul class="menu">
    <li><a href="<c:url value="/accounts"/>">Accounts</a></li>
    <li><a href="<c:url value="/assetInfos"/>">Asset info</a></li>
    <li><a href="<c:url value="/transaction"/>">Add transactions</a></li>
    <li><a href="<c:url value="/dividends"/>">Dividend projections</a></li>
  </ul>

  <h1>Dividend projections</h1>
  
  <form method="post" enctype="application/x-www-form-urlencoded" class="shim">
    <input type="submit" name="generate" value="Generate dividends"/>
  </form>
  
  <sst:map var="cbs">
    <c:forEach items="${accounts}" var="account">
      <sst:mapEntry key="${account.name}" value="${account.cashBalance}"/>
    </c:forEach>
  </sst:map>
  <sst:map var="colours">
    <c:forEach items="${accounts}" var="account">
      <sst:mapEntry key="${account.name}" value="${account.colour}"/>
    </c:forEach>
  </sst:map>

  <table class="table">
    <tr>
      <th>Date</th>
      <th>ExDiv</th>
      <th>Account</th>
      <th>Symbol</th>
      <th>Shares</th>
      <th>Amount</th>
      <th>Type</th>
      <th>Balance</th>
      <td></td>
    </tr>
    <c:forEach items="${dividends}" var="dividend">
      <tr id="diviRow-${dividend.id}">
        <td>${dividend.xaDate}</td>
        <td>${dividend.exDivDate}</td>
        <td>${dividend.accountName}</td>
        <td>
          ${dividend.symbol}<br/>
          <span class="assetName">${dividend.assetName}</span>
        </td>
        <td style="white-space: nowrap;">${dividend.shares} @ $<fmt:formatNumber value="${dividend.divAmount}" pattern="0.00####"/></td>
        <td class="number">$<fmt:formatNumber value="${dividend.amount}" pattern="0.00"/></td>
        <td>${dividend.xaType}</td>
        <td class="number" style="background-color: #${colours[dividend.accountName]}">
          <sst:mapEntry mapVar="cbs" key="${dividend.accountName}" value="${cbs[dividend.accountName] + dividend.amount}"/>
          $<fmt:formatNumber value="${cbs[dividend.accountName]}" pattern="0.00"/>
        </td>
        <td style="white-space: nowrap;">
          <tag:btn value="mod" onclick="modify(${dividend.id})"/>
          <tag:btn value="post" onclick="post(${dividend.id})"/>
          <form method="post" enctype="application/x-www-form-urlencoded" style="display: inline-block;">
            <input type="submit" name="delete" value="del"/>
            <input type="hidden" name="id" value="${dividend.id}"/>
          </form>
        </td>
      </tr>
    </c:forEach>
  </table>
  
  <div id="dialog-form" title="Edit dividend">
    <form>
      <fieldset>
        <input type="hidden" id="divId"/>
        <label for="name">Payable date</label>
        <input type="text" name="xaDate" id="xaDate" class="text ui-widget-content ui-corner-all">
        <label for="email">Ex-div date</label>
        <input type="text" name="exDiv" id="exDiv" class="text ui-widget-content ui-corner-all">
        <label for="email">Shares</label>
        <input type="text" name="shares" id="shares" value="" class="number text ui-widget-content ui-corner-all">
        <label for="email">Div amount</label>
        <input type="text" name="divAmount" id="divAmount" value="" class="number text ui-widget-content ui-corner-all">
        <label for="email">Amount</label>
        <input type="text" name="amount" id="amount" value="" class="number text ui-widget-content ui-corner-all">
        <label for="email">Type</label>
        <select name="xaType" id="xaType" class="ui-widget-content ui-corner-all">
          <c:forEach items="${xaTypes}" var="xaType">
            <option value="${xaType.key}">${xaType.value}</option>
          </c:forEach>
        </select>
      </fieldset>
    </form>
  </div>
</tag:page>

<script type="text/javascript">
$(function() {
    function updateAmount() {
    	var shares = parseFloat($get("shares"));
    	var divAmount = parseFloat($get("divAmount"));
    	if (!isNaN(shares) && !isNaN(divAmount))
    		$set("amount", parseInt(shares * divAmount * 100) / 100);
    };
	$( "#dialog-form" ).dialog({
        autoOpen: false,
        height: 510,
        width: 350,
        modal: true,
        buttons: {
            "Save": function() {
            	MoneyDwr.updateDividend($get("divId"), $get("xaDate"), $get("exDiv"), $get("shares"), $get("divAmount"), 
            			$get("amount"), $get("xaType"), function(err) {
            		if (err)
            			alert(err);
            		else
	    	            //$(this).dialog("close");
	    	            window.location.reload();
            	});
            },
            "Cancel": function() {
	            $(this).dialog("close");
            }
        }
	});
	
	$("#xaDate").datepicker({ dateFormat: "yy/mm/dd" });
	$("#exDiv").datepicker({ dateFormat: "yy/mm/dd" });
	$("#shares").blur(updateAmount);
	$("#divAmount").blur(updateAmount);
});

function modify(id) {
	MoneyDwr.getDividend(id, function(d) {
		var dialog = $("#dialog-form");
		dialog.dialog( "option", "position", { my: "top", at: "bottom", of: "#diviRow-"+id, collision: "flip" });
		dialog.dialog("open");
		$set("divId", d.id);
		$set("xaDate", d.prettyXaDate);
		$set("exDiv", d.prettyExDivDate);
		$set("shares", d.shares);
		$set("divAmount", d.divAmount);
		$set("amount", d.prettyAmount);
		$set("xaType", d.xaType);
	});
};

function post(id) {
	MoneyDwr.getDividend(id, function(d) {
		var query = "?accountId="+ d.accountId;
		query += "&xaDate="+ d.prettyXaDate;
		query += "&xaType="+ d.xaType;
		query += "&"+ d.xaType +"Symbol="+ d.symbol;
		query += "&"+ d.xaType +"Amount="+ d.amount;
		query += "&delDiv="+ d.id;
		query += "&ret=/dividends";
		window.location = "<c:url value="/transaction"/>"+ query;
	});
}
</script>