package uniko.iwvi.fgbas.magoetz.sbo;


import com.microsoft.sqlserver.jdbc.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.ibm.gsk.ikeyman.util.Pair;
import com.ibm.xsp.extlib.relational.jdbc.dbhelper.DatabaseHelper;
import com.ibm.xsp.extlib.relational.util.JdbcUtil;
import uniko.iwvi.fgbas.magoetz.sbo.services.ConnectionsService;

public class SoNBOSession {

    private String objectId;

    private ConnectionsService connectionsService;

    private List<Vector<String>> chronicList = new ArrayList<Vector<String>>();
    ;

    public SoNBOSession() {
        //this.connectionsService = new ConnectionsService("connectionsSSO");
        //this.objectId = connectionsService.getUserEmail();
    }

    public String getObjectId() {
        return objectId;
    }

    public void clearChronic() {
        this.chronicList.clear();
    }

    public void addChronicEntry(String nodeTitle, String nodeId) {
        /*
		Vector<String> newChronicEntry = new Vector<String>();
		newChronicEntry.add(nodeTitle);
		newChronicEntry.add(nodeId);
		this.chronicList.add(newChronicEntry);*/
    }

    public List<Vector<AbstractMap.SimpleEntry<Integer, String>>> getActivityStreamEntries(int maxEntries, String nodeType, String nodeID) throws Exception {
        if ("Mitarbeiter".equals(nodeType)) {
            ArrayList<Vector<AbstractMap.SimpleEntry<Integer, String>>> entries = new ArrayList<Vector<AbstractMap.SimpleEntry<Integer, String>>>();
            Vector<AbstractMap.SimpleEntry<Integer, String>> entry = new Vector<AbstractMap.SimpleEntry<Integer, String>>();

            AbstractMap.SimpleEntry<Integer, String> newEntry = new AbstractMap.SimpleEntry<Integer, String>(0, "17.04.2017");
            entry.add(newEntry);
            newEntry = new AbstractMap.SimpleEntry<Integer, String>(0, "Schmidt pays the outgoing invoice 1725");
            entry.add(newEntry);
            entries.add(entry);

            Vector<AbstractMap.SimpleEntry<Integer, String>> entry2 = new Vector<AbstractMap.SimpleEntry<Integer, String>>();
            newEntry = new AbstractMap.SimpleEntry<Integer, String>(0, "27.03.2017");
            entry2.add(newEntry);
            newEntry = new AbstractMap.SimpleEntry<Integer, String>(0, "Outgoing invoice 1725 is created");
            entry2.add(newEntry);
            entries.add(entry2);

            Vector<AbstractMap.SimpleEntry<Integer, String>> entry3 = new Vector<AbstractMap.SimpleEntry<Integer, String>>();
            newEntry = new AbstractMap.SimpleEntry<Integer, String>(0, "10.03.2017");
            entry3.add(newEntry);
            newEntry = new AbstractMap.SimpleEntry<Integer, String>(0, "Report 1734 is created");
            entry3.add(newEntry);
            entries.add(entry3);

            Vector<AbstractMap.SimpleEntry<Integer, String>> entry4 = new Vector<AbstractMap.SimpleEntry<Integer, String>>();
            newEntry = new AbstractMap.SimpleEntry<Integer, String>(0, "10.10.2016");
            entry4.add(newEntry);
            newEntry = new AbstractMap.SimpleEntry<Integer, String>(0, "Offer 1610 is created");
            entry4.add(newEntry);
            entries.add(entry4);

            return entries;
        } else if ("Lieferant".equals(nodeType)) {
            return getOrdersFromVendor(maxEntries, nodeID);
        }

        return new ArrayList<Vector<AbstractMap.SimpleEntry<Integer, String>>>();
    }

    public List<Vector<String>> getChronicEntries(int maxEntries) {
        List<Vector<String>> chronicEntries = new ArrayList<Vector<String>>();
        maxEntries = maxEntries == 0 ? chronicList.size() : maxEntries;
        if (chronicList.size() > 1) {
            for (int i = chronicList.size(); i > 1; i--) {
                chronicEntries.add(chronicList.get(i - 1));
                // max number to return
                if (i <= chronicList.size() - maxEntries) {
                    break;
                }
            }
        }
        return chronicEntries;
    }

    private List<Vector<AbstractMap.SimpleEntry<Integer, String>>> getOrdersFromVendor(int maxEntries, String nodeID) throws Exception {
        String connectionString =
                "jdbc:sqlserver://nav.erp-challenge.de;"
                        + "database=NAV2015;"
                        + "user=mariedle;"
                        + "password=Uhuvegire422;"
                        + "loginTimeout=10;";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        ArrayList<Vector<AbstractMap.SimpleEntry<Integer, String>>> resultList = new ArrayList<Vector<AbstractMap.SimpleEntry<Integer, String>>>();
        connection = DriverManager.getConnection(connectionString);

        String selectSql = "SELECT TOP (1000) " +
                "[Order Date] " +
                ",CAST(ROUND([Quantity],2,0) as decimal(18,0)) as Quantity " +
                ",[Description] " +
                ",[Name] " +
                "FROM [NAV2015].[dbo].[K�chenland GmbH$Purchase Line] AS PL " +
                "JOIN [NAV2015].[dbo].[K�chenland GmbH$Vendor] as V ON (PL.[Buy-from Vendor No_] = V.[No_]) " +
                "WHERE [Buy-from Vendor No_] = '" + nodeID + "' " +
                "ORDER BY [Order Date], PL.[timestamp] DESC";

        statement = connection.createStatement();
        resultSet = statement.executeQuery(selectSql);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        SimpleDateFormat formatter2 = new SimpleDateFormat("dd.MM.yyyy");

        while (resultSet.next()) {
            Vector<AbstractMap.SimpleEntry<Integer, String>> entry = new Vector<AbstractMap.SimpleEntry<Integer, String>>();
            Date date = formatter.parse(resultSet.getString(1));
            AbstractMap.SimpleEntry<Integer, String> newEntry = new AbstractMap.SimpleEntry<Integer, String>(0, formatter2.format(date));
            entry.add(newEntry);
            newEntry = new AbstractMap.SimpleEntry<Integer, String>(0, resultSet.getString(2));
            entry.add(newEntry);
            newEntry = new AbstractMap.SimpleEntry<Integer, String>(0, " ");
            entry.add(newEntry);
            newEntry = new AbstractMap.SimpleEntry<Integer, String>(0, resultSet.getString(3));
            entry.add(newEntry);
            newEntry = new AbstractMap.SimpleEntry<Integer, String>(0, " von ");
            entry.add(newEntry);
            newEntry = new AbstractMap.SimpleEntry<Integer, String>(1, resultSet.getString(4));
            entry.add(newEntry);
            newEntry = new AbstractMap.SimpleEntry<Integer, String>(0, " bestellt.");
            entry.add(newEntry);

            resultList.add(entry);
        }
        return resultList;
    }
}
