<%@ include file="/WEB-INF/jsp/decl.jsp" %>

<tag:page>
  <ul class="menu">
    <li><a href="<c:url value="/accounts"/>">Accounts</a></li>
    <li><a href="<c:url value="/assetInfos"/>">Asset info</a></li>
    <li><a href="<c:url value="/transaction"/>">Add transactions</a></li>
    <li><a href="<c:url value="/dividends"/>">Dividend projections</a></li>
  </ul>

  <h1>Asset information</h1>
  
  <tag:btn id="marketPriceBtn" onclick="updateAllPrice()" value="update all prices"/>
  
  <table class="table">
    <tr>
      <th>Symbol</th>
      <th>Currency</th>
      <th>Dividend amount</th>
      <th>Dividend schedule</th>
      <th>Market price</th>
      <th></th>
    </tr>
    <c:forEach items="${assets}" var="asset">
      <tr>
        <td>
          <a href="assetInfos/${asset.symbol}">${asset.symbol}</a><br/>
          <span class="assetName">${asset.name}</span>
        </td>
        <td>${asset.divCountry}</td>
        <td>$${asset.divAmount}</td>
        <td>
          <c:choose>
            <c:when test="${asset.divPerYear == 0}">None</c:when>
            <c:otherwise>
              <c:choose>
                <c:when test="${asset.divPerYear == 12}">Monthly</c:when>
                <c:when test="${asset.divPerYear == 4}">Quarterly</c:when>
                <c:when test="${asset.divPerYear == 2}">Semi-annually</c:when>
                <c:when test="${asset.divPerYear == 1}">Annually</c:when>
                <c:otherwise>asset.divPerYear</c:otherwise>
              </c:choose>
              ${asset.divMonth}/${asset.divDay}
            </c:otherwise>
          </c:choose>
        </td>
        <td class="nowrap">
          <tag:btn id="marketPriceBtn-${asset.symbol}" onclick="updatePrice('${asset.symbol}')" value="update"/>
          <span id="marketPrice-${asset.symbol}">
            <c:if test="${!empty asset.marketPrice && asset.marketPrice > 0}">
              ${asset.marketPrice} @ ${asset.prettyMarketTime}
            </c:if>
          </span>
        </td>
        <td>
          <button onclick='deleteAsset("${asset.symbol}")'>delete</button>
        </td>
      </tr>
    </c:forEach>
  </table>
  
  <a href="<c:url value="/assetInfos/(NEW)"/>">New</a>
</tag:page>

<script type="text/javascript">
function updatePrice(symbol) {
	btnDisable("marketPriceBtn-"+ symbol);
	MoneyDwr.getMarketPrice(symbol, function(result) {
		if (result.hasMessages)
			alert(result.messages[0].genericMessage);
		else if (result.data.price != 0)
			$set("marketPrice-"+ symbol, result.data.price +" @ "+ result.data.time);
		btnEnable("marketPriceBtn-"+ symbol);
	});
}

function updateAllPrice() {
	btnDisable("marketPriceBtn");
	MoneyDwr.getAllMarketPrice(function(result) {
		if (result.hasMessages)
			alert(result.messages[0].genericMessage);
		else {
			for (var symbol in result.data) {
				$set("marketPrice-"+ symbol, result.data[symbol].price +" @ "+ result.data[symbol].time);
			}
		}
		btnEnable("marketPriceBtn");
	});
}

function deleteAsset(symbol) {
	if (confirm("Really delete "+ symbol +"?"))
		window.location = window.location + "?delete="+ symbol;
}
</script>