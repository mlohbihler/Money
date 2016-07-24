<%@include file="/WEB-INF/tags/decl.tagf"%>
<%@attribute name="styles" fragment="true" %>
<%@attribute name="js" %>

<!doctype html5>
<html>
<head>
  <title>My Money</title>
  
  <!-- Meta -->
  <meta http-equiv="content-type" content="application/xhtml+xml;charset=utf-8"/>
  <meta http-equiv="Content-Style-Type" content="text/css" />
  
  <!-- Style -->
<%--   <link rel="icon" href="<c:url value="/img/favicon.ico"/>"/> --%>
<%--   <link rel="shortcut icon" href="<c:url value="/img/favicon.ico"/>"/> --%>
  <link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
  <link type="text/css" rel="stylesheet" media="screen, projection" href="<c:url value="/res/css/blueprint/screen.css"/>"/>
  <link type="text/css" rel="stylesheet" media="print" href="<c:url value="/res/css/blueprint/print.css"/>"/> 
  <link type="text/css" rel="stylesheet" media="all" href="<c:url value="/res/css/main.css"/>"/>
  <!--[if lt IE 8]><link rel="stylesheet" href="<c:url value="/res/css/blueprint/ie.css"/>" type="text/css" media="screen, projection"><![endif]-->
  <!--[if lte IE 6]><style type="text/css" media="all">@import "<c:url value="/res/css/ie6.css"/>";</style><![endif]-->
  <!--[if IE 7]><style type="text/css" media="all">@import "<c:url value="/res/css/ie7.css"/>";</style><![endif]-->
  <jsp:invoke fragment="styles"/>
  
  <!-- Scripts -->
  <script type="text/javascript" src="<c:url value="/res/dwr/engine.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/res/dwr/util.js"/>"></script>
  <script type="text/javascript" src="https://code.jquery.com/jquery-2.1.0.min.js"></script>
  <script type="text/javascript" src="https://code.jquery.com/ui/1.10.4/jquery-ui.min.js"></script>
  <script type="text/javascript" src="<c:url value="/res/main.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/dwr/interface/MoneyDwr.js"/>"></script>
</head>

<body>
<!--   <div id="header" class="centered"> -->
<%--     <a href="<c:url value="http://www.InfiniteAutomation.com"/>"><img src="<c:url value="/img/logo.png"/>" alt="Mango Automation - Automation that Fits"/></a> --%>
<!--     <div class="nav"> -->
<!--       <ul> -->
<%--         <li><a href="<c:url value="/home"/>">Home</a></li> --%>
<%--         <li><a href="<c:url value="/core"/>">Download</a></li> --%>
<%--         <li><a href="<c:url value="/modules"/>">Modules</a></li> --%>
<%--         <li><a href="<c:url value="/documentation/home"/>">Documentation</a></li> --%>
<%--         <li><a href="<c:url value="/account/licenses"/>">Account</a></li> --%>
<%--         <li><a href="<c:url value="http://forum.infiniteautomation.com/forum/forums/list.page"/>">Forum</a></li> --%>
<!--       </ul> -->
<!--     </div> -->
<!--   </div> -->
  
  <div class="centered">
    <jsp:doBody/>
  </div>
  
  <div id="footer">&copy;2014 Serotonin Software Technologies Inc., all rights reserved</div>
</body>
<c:if test="${!empty onload}"><script type="text/javascript">$(document).ready(${onload});</script></c:if>
</html>