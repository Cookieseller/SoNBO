function getSortedStringList(unsortedStringList) {
	var vectorList:java.util.List = new java.util.ArrayList();
	for(string in unsortedStringList) {
		var vItem:java.util.Vector = new java.util.Vector();
		vItem.add(string);
		if(context.getLocale().getLanguage().equals("de")) {
			var lookupResult = @DbLookup("", "translations", string, 2);
			var result = (typeof lookupResult == "string") ? lookupResult : string;
			result = result == "" ? string : result;
			vItem.add(result);
		}else {
			vItem.add(string);
		}
		vectorList.add(vItem);
	} 
	var allString = "all";
	var vItemAll:java.util.Vector = new java.util.Vector();
	vItemAll.add(allString);
	if(context.getLocale().getLanguage().equals("de")) {
			var lookupResult = @DbLookup("", "translations", allString, 2);
			var result = (typeof lookupResult == "string") ? lookupResult : allString;
			allString = result == "" ? allString : lookupResult;
			vItemAll.add(allString);
	}else {
		vItemAll.add(allString);
	}
	var sortedList = soNBOManager.sortVectorList(vectorList, 1);
	sortedList.unshift(vItemAll); 
	return sortedList;
}