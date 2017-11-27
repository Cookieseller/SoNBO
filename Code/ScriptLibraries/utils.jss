function getSortedStringList(unsortedStringList) {
	var vectorList:java.util.List = new java.util.ArrayList();
	for(string in unsortedStringList) {
		var vItem:java.util.Vector = new java.util.Vector();
		vItem.add(string);
		if(context.getLocale().getLanguage().equals("de")) {
			var lookupResult = @DbLookup("", "translations", string, "translationGerman");
			result = (typeof lookupResult == "string") ? lookupResult : string;
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
			var lookupResult = @DbLookup("", "translations", allString, "translationGerman");
			allString = (typeof lookupResult == "string") ? lookupResult : allString;
			vItemAll.add(allString);
	}else {
		vItemAll.add(allString);
	}
	vectorList.unshift(vItemAll); 
	var sortedList = soNBOManager.sortVectorList(vectorList, 1);
	return sortedList;
}