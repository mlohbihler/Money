var money = {};

dwr.util.setEscapeHtml(false);

//
// DWR utility shorthand
function $get(comp) {
    return dwr.util.getValue(comp);
};

function $set(comp, value) {
    return dwr.util.setValue(comp, value);
};

function $show(comp) {
	getNodeIfString(comp).removeClass("hide");
}

function $hide(comp) {
	getNodeIfString(comp).addClass("hide");
}

function getNodeIfString(node) {
    if (typeof(node) == "string")
        return $(node);
    return node;
}

function btnDisable(id) {
	var btn = $("#"+ id.replace(':', '\\:'));
	btn.prop("disabled", true);
    btn.children("img").show();
}

function btnEnable(id) {
	var btn = $("#"+ id.replace(':', '\\:'));
	btn.prop("disabled", false);
    btn.children("img").hide();
}

money.txDesc = function(symbol, t) {
	var s = "";
	if (t.transactionType == "BUY")
		s += t.shares +" @ "+ t.price;
	else if (t.transactionType == "CASHADJ")
		;
	else if (t.transactionType == "CASHDIV")
		;
	else if (t.transactionType == "CONTRIBUTION")
		;
	else if (t.transactionType == "DEPOSIT") {
		if (t.symbol2)
			s += t.symbol2;
	}
	else if (t.transactionType == "EXCHADJ")
		s += t.shares +" @ "+ t.price;
	else if (t.transactionType == "FEE_REBATE")
		;
	else if (t.transactionType == "GRANT")
		s += t.symbol2;
	else if (t.transactionType == "INTEREST") {
		var s = "Shares: "+ t.shares +"<br/>";
		s += "Price: "+ t.price +"<br/>";
		s += "FX: "+ t.foreignExchange +"<br/>";
		s += "Fee: "+ t.fee +"<br/>";
		s += "Book: "+ t.bookValue +"<br/>";
		return s;
	}
	else if (t.transactionType == "MERGER") {
		if (symbol == null)
			s += t.shares +" becomes "+ t.price +" in "+ t.symbol2;
		else if (symbol == t.symbol)
			s += t.shares +" to "+ t.symbol2;
		else
			s += t.price +" from "+ t.symbol;
	}
	else if (t.transactionType == "MANAGEMENT_FEE") {
		var s = "Shares: "+ t.shares +"<br/>";
		s += "Price: "+ t.price +"<br/>";
		s += "FX: "+ t.foreignExchange +"<br/>";
		s += "Fee: "+ t.fee +"<br/>";
		s += "Book: "+ t.bookValue +"<br/>";
		return s;
	}
	else if (t.transactionType == "REINVDIV")
		s += t.shares +" @ "+ t.price;
	else if (t.transactionType == "SELL")
		s += t.shares +" @ "+ t.price;
	else if (t.transactionType == "SPLIT")
		s += t.shares;
	else if (t.transactionType == "SPLIT_CASH")
		;
	else if (t.transactionType == "STOCKDIV")
		;
	else if (t.transactionType == "TAX")
		;
	else if (t.transactionType == "TRANSFER_IN") {
		if (t.symbol)
			s += t.shares +" @ "+ t.price;
	}
	else if (t.transactionType == "WITHDRAWAL")
		;
	else
		s += "Unknown: "+ t.transactionType;
	return s;
};

//m2m2lic.hideDwrMessages = function(fields) {
//	$("#genericMessages").hide();
//	for (var i=0; i<fields.length; i++)
//		$("#"+ fields[i] +"Message").hide();
//};
//
//m2m2lic.showDwrMessages = function(response) {
//	var genericMessages = [];
//	var msg, node;
//	for (var i=0; i<response.messages.length; i++) {
//		msg = response.messages[i];
//		if (msg.contextKey) {
//			node = $("#"+ msg.contextKey +"Message");
//			if (node.length > 0) {
//				node.html(msg.contextualMessage);
//				node.show();
//			}
//			else
//				// The message node wasn't found, so add the message to the generic messages.
//				genericMessages.push(msg.contextKey +": "+ msg.contextualMessage);
//		}
//		else
//			genericMessages.push(msg.genericMessage);
//	}
//	
//	if (genericMessages.length > 0) {
//		var genericContent = "<ul>";
//		for (var i=0; i<genericMessages.length; i++)
//			genericContent += "<li>"+ genericMessages[i] +"</li>";
//		genericContent += "</ul>";
//		node = $("#genericMessages");
//		node.html(genericContent);
//		node.show();
//	}
//};
//
//m2m2lic.startUpload = function() {
//    if (!$get("uploadFile"))
//        alert("Please choose a file to upload");
//    else {
//        AccountDwr.startUpload(function() {
//            document.getElementById("daform").submit();
//            m2m2lic.updateProgress(0);
//            $("#uploadStatus").show();
//            setTimeout(m2m2lic.monitorUpload, 1000);
//        });
//    }
//}
//
//m2m2lic.monitorUpload = function() {
//    AccountDwr.monitorUpload(function(monitor) {
//        if (monitor.percent == -1)
//            $set("uploadPercentage", "(unknown)");
//        else
//        	m2m2lic.updateProgress(monitor.percent);
//        
//        if (!monitor.done)
//            setTimeout(m2m2lic.monitorUpload, 1000);
//        else {
//            if (monitor.message)
//                alert(monitor.message);
//            else {
//                alert("Upload successful");
//                window.location = monitor.redirect;
//            }
//            $("#uploadStatus").hide();
//        }
//    });
//}
//
//m2m2lic.updateProgress = function(percent) {
//    var maxWidth = $("#uploadProgressBar").width() - 2;
//    var width = percent * maxWidth / 100;
//    $("#uploadIndicator").width(width);
//    $set("uploadPercentage", percent +"%");
//}
//
//m2m2lic.formatPennies = function(pennies) {
//	var d = "$"+ (pennies / 100);
//	var dot = d.indexOf(".");
//	if (dot == -1)
//		d += ".00";
//	while (d.length <= dot + 2)
//		d += "0";
//	return d;
//}
