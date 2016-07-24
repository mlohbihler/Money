<%@ include file="/WEB-INF/jsp/decl.jsp" %>

<tag:page>
  <ul class="menu">
    <li><a href="<c:url value="/accounts"/>">Accounts</a></li>
    <li><a href="<c:url value="/assetInfos"/>">Asset info</a></li>
    <li><a href="<c:url value="/transaction"/>">Add transactions</a></li>
    <li><a href="<c:url value="/dividends"/>">Dividend projections</a></li>
  </ul>

  <h1>Accounts</h1>
  
  <table class="table" style="width: auto;">
    <c:forEach items="${accounts}" var="account">
      <tr>
        <td><a href="accounts/${account.name}">${account.name}</a></td>
        <td class="number">$<fmt:formatNumber pattern="0.00" value="${account.cashBalance}"/></td>
        <td><tag:btn id="txBtn-${account.id}" onclick="getTransactions(${account.id}, '${account.name}')" value="tx"/></td>
      </tr>
    </c:forEach>
  </table>
  
  <div id="transactions" class="hide">
    <h3>Transactions - <span id="txDesc"></span></h3>
    
    <table>
      <tr>
        <th>Type</th>
        <th>Date</th>
        <th>Symbol</th>
        <th>Details</th>
        <th>Cash amount</th>
        <th>Balance</th>
        <td></td>
      </tr>
      <tbody id="txlist">
      </tbody>
    </table>
  </div>
</tag:page>

<script type="text/javascript">
function getTransactions(accountId, name) {
    $hide("#transactions");
    btnDisable("txBtn-"+ accountId);
    MoneyDwr.getTransactions(accountId, null, function(txs) {
        $set("txDesc", name);
        
        dwr.util.removeAllRows("txlist");
        
        var balance = 0;
        dwr.util.addRows("txlist", txs, [
            function(t) { return t.prettyTransactionType; },
            function(t) { return t.prettyTransactionDate; },
    		function(t) { return t.symbol; },
            function(t) { return money.txDesc(null, t); },
            function(t) { return t.prettyCashAmount; },
            function(t) { return t.prettyLastCashBalance; },
            function(t) {
            	var s = "<button onclick='editTx("+ t.id +")'>edit</button> ";
            	s += "<button onclick='delTx("+ t.id +")'>del</button>";
            	return s;
            }
            ],{
        	    rowCreator: function(options) {
                    var tr = document.createElement("tr");
                    tr.id = "txRow-"+ options.rowData.id;
                    return tr;
        	    },
                cellCreator: function(options) {
                    var td = document.createElement("td");
                    if (options.cellNum == 4 || options.cellNum == 5)
                        td.className = "number";
                    return td;
                }
        });
        
        $show("#transactions");
        btnEnable("txBtn-"+ accountId);
    });
}

function editTx(id) {
	window.location = "<c:url value="/transaction"/>?editId="+ id;
}

function delTx(id) {
	MoneyDwr.deleteTransaction(id, function() {
        $("#txRow-"+ id).remove();
	});
}
</script>