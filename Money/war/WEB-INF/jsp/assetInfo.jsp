<%@ include file="/WEB-INF/jsp/decl.jsp" %>

<tag:page>
  <ul class="menu">
    <li><a href="<c:url value="/accounts"/>">Accounts</a></li>
    <li><a href="<c:url value="/assetInfos"/>">Asset info</a></li>
    <li><a href="<c:url value="/transaction"/>">Add transactions</a></li>
    <li><a href="<c:url value="/dividends"/>">Dividend projections</a></li>
  </ul>

  <c:choose>
    <c:when test="${!empty symbol}"><h1>${symbol}</h1></c:when>
    <c:otherwise><h1>(new symbol)</h1></c:otherwise>
  </c:choose>
  
  <form method="post" enctype="application/x-www-form-urlencoded">
    <table class="props">
      <tr>
        <th>Symbol</th>
        <td>
          <c:choose>
            <c:when test="${!empty symbol}">${symbol}</c:when>
            <c:otherwise><input type="text" name="symbol" value="${symbol}"/></c:otherwise>
          </c:choose>
        </td>
      </tr>
      <tr>
        <th>Name</th>
        <td><input type="text" name="name" value="${name}" class="long"/></td>
      </tr>
      <tr>
        <th>Market symbol</th>
        <td><input type="text" name="marketSymbol" value="${marketSymbol}"/></td>
      </tr>
      <tr>
        <th>Dividend amount</th>
        <td><input type="text" name="divAmount" value="${divAmount}" class="short"/></td>
      </tr>
      <tr>
        <th>Dividend day</th>
        <td><input type="text" name="divDay" value="${divDay}" class="short"/></td>
      </tr>
      <tr>
        <th>Dividend month</th>
        <td><input type="text" name="divMonth" value="${divMonth}" class="short"/></td>
      </tr>
      <tr>
        <th>Dividends per year</th>
        <td><input type="text" name="divPerYear" value="${divPerYear}" class="short"/></td>
      </tr>
      <tr>
        <th>Dividend transaction type</th>
        <td>
          <sst:select name="divXaType" value="${divXaType}">
            <sst:option value="CASHDIV">Cash dividend</sst:option>
            <sst:option value="REINVDIV">Reinvested dividend</sst:option>
            <sst:option value="STOCKDIV">Stock dividend</sst:option>
          </sst:select>
        </td>
      </tr>
      <tr>
        <th>Dividend country</th>
        <td>
          <sst:select name="divCountry" value="${divCountry}">
            <sst:option>Canada</sst:option>
            <sst:option>US</sst:option>
          </sst:select>
        </td>
      </tr>
      <tr>
        <th>Dividends symbol ID</th>
        <td><input type="text" name="divSymbolId" value="${divSymbolId}" class="short"/></td>
      </tr>
      <tr>
        <th>Notes</th>
        <td><textarea rows="60" cols="10" name="notes">${notes}</textarea></td>
      </tr>
    </table>
    
    <input type="submit" value="Save"/>
  </form>
</tag:page>