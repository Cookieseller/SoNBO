function getSortedStringList(unsortedStringList) {
	var vectorList:java.util.List = new java.util.ArrayList();
	var locale = context.getLocale().getLanguage();
	if(locale.equals("de")) {
		var returnField = "translationGerman";
	}else if(locale.contains("en")) {
		var returnField = "translationEnglish";
	}
	for(string in unsortedStringList) {
		var vItem = getTranslatedVector(string);
		vectorList.add(vItem);
	}
	var sortedList = soNBOManager.sortVectorList(vectorList, 1);
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