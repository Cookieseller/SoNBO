package uniko.iwvi.fgbas.magoetz.sbo.services;

import uniko.iwvi.fgbas.magoetz.sbo.database.OData;
import uniko.iwvi.fgbas.magoetz.sbo.database.QueryService;
import uniko.iwvi.fgbas.magoetz.sbo.database.IQueryService;


public class QueryServiceFactory {
	public static IQueryService getQueryServiceByDatasource(String datasource) {
		if (datasource.equals("OData")) {
			return new OData();
		} else {
			return new QueryService();
		}
	}
}