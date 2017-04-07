<%@ include file="/WEB-INF/jsp/decl.jsp" %>

<tag:page>
<ul class="menu">
    <li><a href="<c:url value="/accounts"/>">Accounts</a></li>
    <c:forEach items="${accounts}" var="account">
      <li><a href="<c:url value="/accounts/${account.name}"/>">${account.name}</a></li>
    </c:forEach>
  </ul>

  <h1>${account.name}</h1>
  
  <div>
    <table class="table" style="width: auto;">
      <tr>
        <th>Cash balance</th>
        <td class="number">$<fmt:formatNumber pattern="0.00" value="${account.cashBalance}"/></td>
      </tr>
      <tr>
        <th>Total invested</th>
        <td class="number">$<fmt:formatNumber pattern="0.00" value="${totalInvested}"/></td>
      </tr>
      <tr>
        <th>Total gain/loss</th>
        <td class="number">$<fmt:formatNumber pattern="0.00" value="${totalGain}"/></td>
      </tr>
      <tr>
        <th>AROR</th>
        <td class="number"><fmt:formatNumber pattern="0.000" value="${(account.rateOfReturn - 1) * 100}"/>%</td>
      </tr>
      <tr>
        <th>Market</th>
        <td class="number">$<fmt:formatNumber pattern="0.00" value="${market}"/></td>
      </tr>
    </table>  
  </div>
  
  <c:if test="${!empty account.notes}">
    <i>${account.notes}</i>
  </c:if>
  
  <h3>Assets</h3>
  <table class="table">
    <tr>
      <th>Symbol</th>
      <th>Quantity</th>
      <th>Book</th>
<!--       <th>Gain</th> -->
      <th>AROR</th>
      <th>Market</th>
      <th>Net</th>
      <td></td>
    </tr>
    <c:forEach items="${account.assets}" var="asset">
      <c:if test="${!asset.pastCutoff && asset.gicPurchase==null}">
        <tr>
          <td>
            ${asset.symbol}<br/>
            <span class="assetName">${asset.assetInfo.name}</span>
          </td>
          <td class="number"><fmt:formatNumber pattern="0.#" value="${asset.quantity}"/></td>
          <td class="number">$<fmt:formatNumber pattern="0.00" value="${asset.bookValue}"/></td>
<%--           <td class="number">$<fmt:formatNumber pattern="0.00" value="${asset.return}"/></td> --%>
          <td class="number"><fmt:formatNumber pattern="0.###" value="${(asset.rateOfReturn - 1) * 100}"/>%</td>
          <td class="number">
            <c:if test="${!empty asset.marketValue}">
              $<fmt:formatNumber pattern="0.####" value="${asset.marketValue}"/>
              <c:if test="${asset.quantity > 0}">
                <!-- Note: the 1.1s in the following expression ensure that float point arithmetic is used. -->
                (<fmt:formatNumber pattern="0.#" value="${(1.1 * asset.marketValue * asset.quantity / asset.bookValue / 1.1 - 1) * 100}"/>%)
              </c:if>
            </c:if>
          </td>
          <td class="number">
            $<fmt:formatNumber pattern="0.00" value="${asset.cashReturn + (asset.marketValue * asset.quantity)}"/><br/>
          </td>
          <td>
            <tag:btn id="txBtn-${asset.symbol}" onclick="getTransactions('${asset.symbol}')" value="tx"/>
            <c:if test="${!empty asset.assetInfo.divXaType}">
              <button onclick='addDividend("${asset.assetInfo.divXaType}", "${asset.symbol}")'>dv</button>
            </c:if>
          </td>
        </tr>
      </c:if>
    </c:forEach>
  </table>
  
  <h3>GICs</h3>
  <table class="table">
    <tr>
      <th>Symbol</th>
      <th>Date</th>
      <th>Amount</th>
      <th>Rate</th>
      <th>Term</th>
      <th>Market</th>
      <th>Net</th>
    </tr>
    <c:forEach items="${gics}" var="gic">
      <tr>
        <td>
          ${gic.symbol}<br/>
          <span class="assetName">${gic.symbol2}</span>
        </td>
        <td class="number">${gic.prettyTransactionDate}</td>
        <td class="number">$<fmt:formatNumber pattern="0.00" value="${gic.price}"/></td>
        <td class="number"><fmt:formatNumber pattern="0.0000" value="${gic.foreignExchange}"/></td>
        <td class="number"><fmt:formatNumber pattern="0.#" value="${gic.fee}"/></td>
        <td class="number">$<fmt:formatNumber pattern="0.00" value="${gic.marketValue}"/></td>
        <td class="number">$<fmt:formatNumber pattern="0.00" value="${gic.marketValue - gic.price}"/></td>
      </tr>
    </c:forEach>
  </table>
  
  <div id="transactions" class="hide">
    <h3>Transactions - <span id="txDesc"></span></h3>
    
    <table>
      <tr>
        <th>Type</th>
        <th>Date</th>
        <th>Details</th>
        <th>Cash amount</th>
      </tr>
      <tbody id="txlist">
      </tbody>
    </table>
  </div>
</tag:page>

<script type="text/javascript">
function getTransactions(symbol) {
	$hide("#transactions");
	btnDisable("txBtn-"+ symbol);
	MoneyDwr.getTransactions(${account.id}, symbol, function(txs) {
		$set("txDesc", symbol);
		
		dwr.util.removeAllRows("txlist");
		dwr.util.addRows("txlist", txs, [
				function(t) { return t.prettyTransactionType; },
				function(t) { return t.prettyTransactionDate; },
				function(t) { return money.txDesc(symbol, t); },
				function(t) { return t.prettyCashAmount; }
        ],{
            cellCreator: function(options) {
                var td = document.createElement("td");
                if (options.cellNum == 3)
                    td.className = "number";
                return td;
            }
		});
		
		$show("#transactions");
		btnEnable("txBtn-"+ symbol);
	});
}

function addDividend(divXaType, symbol) {
	var query = "?accountId=${account.id}&xaType="+ divXaType +"&"+ divXaType +"Symbol="+ symbol +"&ret=/accounts/${account.name}";
	window.location = "<c:url value="/transaction"/>"+ query;
}
</script>