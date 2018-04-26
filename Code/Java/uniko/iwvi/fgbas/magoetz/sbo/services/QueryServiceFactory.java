package uniko.iwvi.fgbas.magoetz.sbo.services;


public class QueryServiceFactory {
	public static IQueryService getQueryServiceByDatasource(String datasource) {
		if (datasource.equals("OData")) {
			return new ODataQueryService();
		} else {
			return new QueryService();
		}
	}
}