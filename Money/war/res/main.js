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
	if (t.transactionType == "BUY") {
		s += t.shares +" @ "+ t.price;
	} else if (t.transactionType == "BUYGIC") {
        s += "Rate: "+ t.price +"%<br/>";
        s += "Term: "+ t.foreignExchange +" years<br/>";
    } else if (t.transactionType == "CASHADJ") {
		;
    } else if (t.transactionType == "CASHDIV") {
		;
	} else if (t.transactionType == "CONTRIBUTION") {
		;
	} else if (t.transactionType == "DEPOSIT") {
		if (t.symbol2)
			s += t.symbol2;
	} else if (t.transactionType == "EXCHADJ") {
		s += t.shares +" @ "+ t.price;
  } else if (t.transactionType == "FEE") {
    ;
	} else if (t.transactionType == "FEE_REBATE") {
		;
	} else if (t.transactionType == "GRANT") {
		s += t.symbol2;
	} else if (t.transactionType == "INTEREST") {
		;
	} else if (t.transactionType == "MERGER") {
		if (symbol == null)
			s += t.shares +" becomes "+ t.price +" in "+ t.symbol2;
		else if (symbol == t.symbol)
			s += t.shares +" to "+ t.symbol2;
		else
			s += t.price +" from "+ t.symbol;
	} else if (t.transactionType == "MANAGEMENT_FEE") {
		s += "Shares: "+ t.shares +"<br/>";
		s += "Price: "+ t.price +"<br/>";
		s += "FX: "+ t.foreignExchange +"<br/>";
		s += "Fee: "+ t.fee +"<br/>";
		s += "Book: "+ t.bookValue +"<br/>";
		return s;
	} else if (t.transactionType == "REINVDIV") {
		s += t.shares +" @ "+ t.price;
	} else if (t.transactionType == "SELL") {
		s += t.shares +" @ "+ t.price;
	} else if (t.transactionType == "SPLIT") {
		s += t.shares;
	} else if (t.transactionType == "SPLIT_CASH") {
		;
	} else if (t.transactionType == "STOCKDIV") {
		;
	} else if (t.transactionType == "TAX") {
		;
	} else if (t.transactionType == "TRANSFER_IN") {
		if (t.symbol)
			s += t.shares +" @ "+ t.price;
	} else if (t.transactionType == "WITHDRAWAL") {
		;
	} else {
		s += "Unknown: "+ t.transactionType;
	}
	return s;
};
