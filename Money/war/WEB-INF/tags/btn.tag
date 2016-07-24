<%@include file="/WEB-INF/tags/decl.tagf"%><%--
--%><%@attribute name="id" %><%--
--%><%@attribute name="onclick" %><%--
--%><%@attribute name="value" %><%--
--%><%@tag body-content="empty"%><%--
--%><button id="${id}" onclick="${onclick}">${value} <img src="<c:url value="/res/img/throbber.gif"/>" class="hide"/></button>