package uniko.iwvi.fgbas.magoetz.sbo.services;

import uniko.iwvi.fgbas.magoetz.sbo.objects.Datasource;
import uniko.iwvi.fgbas.magoetz.sbo.util.Utilities;

public class QueryServiceFactory {
	public static IQueryService getQueryServiceByDatasource(String datasource) {
		if (datasource.equals("OData")) {
			Utilities.remotePrint("Serive: " + "OData");
			return new ODataQueryService();
		} else {
			Utilities.remotePrint("Serive: " + "QueryService");
			return new QueryService();
		}
	}
}