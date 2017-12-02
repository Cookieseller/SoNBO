function getSortedStringListForView(unsortedStringList, viewName) {
	var vectorList:java.util.List = new java.util.ArrayList();
	var locale = context.getLocale().getLanguage();
	if(locale.equals("de")) {
		var returnField = "translationGerman";
	}else if(locale.contains("en")) {
		var returnField = "translationEnglish";
	}
	for(string in unsortedStringList) {
		var vItem = getTranslatedVector(string);
		// lookup entered order and append to vector
		lookupResult = @DbLookup("", viewName, string, 2);
		var result = (typeof lookupResult == "number" && lookupResult != "") ? lookupResult : vItem.get(1);
		vItem.add(result.toString());
		vectorList.add(vItem);
	}
	var sortedList = soNBOManager.sortVectorList(vectorList, 2);
	var vItemAll = getTranslatedVector("all");
	sortedList.unshift(vItemAll); 
	return sortedList;
}

function getTranslatedVector(string) {
	var vItem:java.util.Vector = new java.util.Vector();
	vItem.add(string);
	var lookupResult = "";
	if(locale.equals("de") || locale.contains("en")) {
		lookupResult = @DbLookup("", "translations", string, returnField);
	}
	var result = (typeof lookupResult == "string" && lookupResult != "") ? lookupResult : string;
	vItem.add(result)
	return vItem;
}