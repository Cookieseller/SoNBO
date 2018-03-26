package uniko.iwvi.fgbas.magoetz.sbo.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.sql.*;

import uniko.iwvi.fgbas.magoetz.sbo.objects.Node;

public class DBMock implements Serializable {
    private static final long serialVersionUID = 1L;

    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private HashMap<String, Node> map = new HashMap<String, Node>();
    private HashMap<String, ArrayList<Node>> adjNodes = new HashMap<String, ArrayList<Node>>();

    public DBMock() {
        // Lieferanten Belege
        final Node ordNode1 = createNode("1001", "Bestellung 1",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Lieferanten-Belege", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Bestellung (Lieferant)");
        final Node ordNode2 = createNode("1002", "Bestellung 2",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Lieferanten-Belege", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Bestellung (Lieferant)");
        final Node ordNode3 = createNode("1003", "Bestellung 3",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Lieferanten-Belege", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Bestellung (Lieferant)");
        final Node ordNode4 = createNode("1004", "Bestellung 4",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Lieferanten-Belege", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Bestellung (Lieferant)");

        final Node delNoteNode1 = createNode("1101", "Lieferschein 1",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Lieferanten-Belege", new String[]{"Lieferanten-Belege"},
                "Lieferschein (Lieferant)");
        final Node delNoteNode2 = createNode("1102", "Lieferschein 2",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Lieferanten-Belege", new String[]{"Lieferanten-Belege"},
                "Lieferschein (Lieferant)");
        final Node delNoteNode3 = createNode("1103", "Lieferschein 3",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Lieferanten-Belege", new String[]{"Lieferanten-Belege"},
                "Lieferschein (Lieferant)");
        final Node delNoteNode4 = createNode("1104", "Lieferschein 4",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Lieferanten-Belege", new String[]{"Lieferanten-Belege"},
                "Lieferschein (Lieferant)");

        final Node billNode1 = createNode("1201", "Rechnung 1",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Lieferanten-Belege", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Rechnung (Lieferant)");
        final Node billNode2 = createNode("1202", "Rechnung 2",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Lieferanten-Belege", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Rechnung (Lieferant)");
        final Node billNode3 = createNode("1203", "Rechnung 3",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Lieferanten-Belege", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Rechnung (Lieferant)");
        final Node billNode4 = createNode("1204", "Rechnung 4",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Lieferanten-Belege", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Rechnung (Lieferant)");

        // Kunden Belege
        final Node offerNode1 = createNode("1301", "Angebot 1",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Kunden-Belege", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Angebot (Kunde)");
        final Node offerNode2 = createNode("1302", "Angebot 2",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Kunden-Belege", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Angebot (Kunde)");
        final Node offerNode3 = createNode("1303", "Angebot 3",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Kunden-Belege", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Angebot (Kunde)");
        final Node offerNode4 = createNode("1304", "Angebot 4",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Kunden-Belege", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Angebot (Kunde)");

        final Node orderNode1 = createNode("1401", "Auftrag 1",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Kunden-Belege", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Auftrag (Kunde)");
        final Node orderNode2 = createNode("1402", "Auftrag 2",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Kunden-Belege", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Auftrag (Kunde)");
        final Node orderNode3 = createNode("1403", "Auftrag 3",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Kunden-Belege", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Auftrag (Kunde)");
        final Node orderNode4 = createNode("1404", "Auftrag 4",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Kunden-Belege", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Auftrag (Kunde)");

        final Node custDelNoteNode1 = createNode("1501", "Lieferschein 1",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Kunden-Belege", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Lieferschein (Kunde)");
        final Node custDelNoteNode2 = createNode("1502", "Lieferschein 2",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Kunden-Belege", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Lieferschein (Kunde)");
        final Node custDelNoteNode3 = createNode("1503", "Lieferschein 3",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Kunden-Belege", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Lieferschein (Kunde)");
        final Node custDelNoteNode4 = createNode("1504", "Lieferschein 4",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Kunden-Belege", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Lieferschein (Kunde)");

        final Node custBillNode1 = createNode("1601", "Rechnung 1",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Kunden-Belege", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Rechnung (Kunde)");
        final Node custBillNode2 = createNode("1602", "Rechnung 2",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Kunden-Belege", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Rechnung (Kunde)");
        final Node custBillNode3 = createNode("1603", "Rechnung 3",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Kunden-Belege", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Rechnung (Kunde)");
        final Node custBillNode4 = createNode("1604", "Rechnung 4",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Kunden-Belege", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Rechnung (Kunde)");

        // Personen
        final Node empNode1 = createNode("1701", "Mathias Riedle",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Personen", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Mitarbeiter");
        final Node empNode2 = createNode("1702", "Berit Gebel-Sauer",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Personen", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Mitarbeiter");
        final Node empNode3 = createNode("1703", "Flemming G�tz",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Personen", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Mitarbeiter");
        final Node empNode4 = createNode("1704", "Petra Schubert",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Personen", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Mitarbeiter");

        final Node custNode1 = createNode("1801", "Kunde 1",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Personen", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Kunde");
        final Node custNode2 = createNode("1802", "Kunde 2",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Personen", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Kunde");
        final Node custNode3 = createNode("1803", "Kunde 3",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Personen", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Kunde");

        final Node supNode1 = createNode("K00000000000001", "China Import Ltd.",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Personen", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Lieferant");
        final Node supNode2 = createNode("K00000000000004", "#1299#_Brat & Schmeck",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Personen", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Lieferant");
        final Node supNode3 = createNode("K00000000000005", "#1299#_General Elektro Deutschland AG",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Personen", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Lieferant");

        // Lager
        final Node stockNode1 = createNode("2001", "Lager 1",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Lager", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Lager");
        final Node stockNode2 = createNode("2002", "Lager 2",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Lager", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Lager");

        // Produkte
        final Node productNode1 = createNode("2101", "Produkt 1",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Produkte", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Artikel/St�ckliste");
        final Node productNode2 = createNode("2102", "Produkt 2",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Produkte", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Artikel/St�ckliste");
        final Node productNode3 = createNode("2103", "Produkt 3",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Produkte", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Artikel/St�ckliste");
        final Node productNode4 = createNode("2104", "Produkt 4",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Produkte", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Artikel/St�ckliste");
        final Node productNode5 = createNode("2105", "Produkt 5",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Produkte", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Artikel/St�ckliste");
        final Node productNode6 = createNode("2106", "Produkt 6",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Produkte", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Artikel/St�ckliste");
        final Node productNode7 = createNode("2107", "Produkt 7",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Produkte", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Artikel/St�ckliste");
        final Node productNode8 = createNode("2108", "Produkt 8",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Produkte", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Artikel/St�ckliste");
        final Node productNode9 = createNode("2109", "Produkt 9",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Produkte", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Artikel/St�ckliste");

        // Dienstleistung
        final Node serviceNode1 = createNode("2201", "Aufbau",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Produkte", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Dienstleistung");
        final Node serviceNode2 = createNode("2202", "Produktion",
                new ArrayList<String>() {
                    {
                        add("Name");
                    }
                }, "Produkte", new String[]{"Lieferanten-Belege",
                        "Kunden-Belege", "Personen", "Lager", "Produkte"},
                "Dienstleistung");

        // Lieferanten Belege
        map.put("1001", ordNode1);
        map.put("1002", ordNode2);
        map.put("1003", ordNode3);
        map.put("1004", ordNode4);
        map.put("1101", delNoteNode1);
        map.put("1102", delNoteNode2);
        map.put("1103", delNoteNode3);
        map.put("1104", delNoteNode4);
        map.put("1201", billNode1);
        map.put("1202", billNode2);
        map.put("1203", billNode3);
        map.put("1204", billNode4);

        // Kunden Belege
        map.put("1301", offerNode1);
        map.put("1302", offerNode2);
        map.put("1303", offerNode3);
        map.put("1304", offerNode4);
        map.put("1401", orderNode1);
        map.put("1402", orderNode2);
        map.put("1403", orderNode3);
        map.put("1404", orderNode4);
        map.put("1501", custDelNoteNode1);
        map.put("1502", custDelNoteNode2);
        map.put("1503", custDelNoteNode3);
        map.put("1504", custDelNoteNode4);
        map.put("1601", custBillNode1);
        map.put("1602", custBillNode2);
        map.put("1603", custBillNode3);
        map.put("1604", custBillNode4);

        // Personen
        map.put("1701", empNode1);
        map.put("1702", empNode2);
        map.put("1703", empNode3);
        map.put("1704", empNode4);
        map.put("1801", custNode1);
        map.put("1802", custNode2);
        map.put("1803", custNode3);
        map.put("K00000000000001", supNode1);
        map.put("K00000000000004", supNode2);
        map.put("K00000000000005", supNode3);

        // Lager
        map.put("2001", stockNode1);
        map.put("2002", stockNode2);

        // Produkte
        map.put("2101", productNode1);
        map.put("2102", productNode2);
        map.put("2103", productNode3);
        map.put("2104", productNode4);
        map.put("2105", productNode5);
        map.put("2106", productNode6);
        map.put("2107", productNode7);
        map.put("2108", productNode8);
        map.put("2109", productNode9);

        // Dienstleistung
        map.put("2201", serviceNode1);
        map.put("2202", serviceNode2);

        adjNodes.put("1001", new ArrayList<Node>() {
            {
                add(delNoteNode1);
                add(billNode1);
                add(offerNode1);
                add(orderNode1);
                add(empNode1);
                add(supNode1);
                add(productNode1);
                add(productNode2);
            }
        });
        adjNodes.put("1002", new ArrayList<Node>() {
            {
                add(delNoteNode2);
                add(billNode2);
                add(offerNode2);
                add(orderNode2);
                add(empNode2);
                add(supNode2);
                add(productNode1);
                add(productNode2);
            }
        });
        adjNodes.put("1003", new ArrayList<Node>() {
            {
                add(delNoteNode3);
                add(billNode3);
                add(offerNode3);
                add(orderNode3);
                add(empNode3);
                add(supNode2);
                add(productNode1);
                add(productNode3);
            }
        });
        adjNodes.put("1004", new ArrayList<Node>() {
            {
                add(delNoteNode4);
                add(billNode4);
                add(offerNode4);
                add(orderNode4);
                add(empNode4);
                add(supNode3);
                add(productNode3);
                add(productNode4);
            }
        });

        adjNodes.put("1101", new ArrayList<Node>() {
            {
                add(ordNode1);
                add(billNode1);
                add(empNode1);
                add(supNode1);
                add(productNode1);
                add(productNode2);
            }
        });
        adjNodes.put("1102", new ArrayList<Node>() {
            {
                add(ordNode2);
                add(billNode2);
                add(empNode2);
                add(supNode2);
                add(productNode1);
                add(productNode2);
            }
        });
        adjNodes.put("1103", new ArrayList<Node>() {
            {
                add(ordNode3);
                add(billNode3);
                add(empNode3);
                add(supNode2);
                add(productNode1);
                add(productNode3);
            }
        });
        adjNodes.put("1104", new ArrayList<Node>() {
            {
                add(ordNode4);
                add(billNode4);
                add(empNode4);
                add(supNode3);
                add(productNode3);
                add(productNode4);
            }
        });

        adjNodes.put("1201", new ArrayList<Node>() {
            {
                add(ordNode1);
                add(delNoteNode1);
                add(empNode1);
                add(supNode1);
                add(productNode1);
                add(productNode2);
            }
        });
        adjNodes.put("1202", new ArrayList<Node>() {
            {
                add(ordNode2);
                add(delNoteNode2);
                add(empNode2);
                add(supNode2);
                add(productNode1);
                add(productNode2);
            }
        });
        adjNodes.put("1203", new ArrayList<Node>() {
            {
                add(ordNode3);
                add(delNoteNode3);
                add(empNode3);
                add(supNode2);
                add(productNode1);
                add(productNode3);
            }
        });
        adjNodes.put("1204", new ArrayList<Node>() {
            {
                add(ordNode4);
                add(delNoteNode4);
                add(empNode4);
                add(supNode3);
                add(productNode3);
                add(productNode4);
            }
        });

        // Angebot
        adjNodes.put("1301", new ArrayList<Node>() {
            {
                add(orderNode1);
                add(custDelNoteNode1);
                add(custBillNode1);
                add(empNode1);
                add(custNode1);
                add(productNode1);
                add(productNode2);
            }
        });
        adjNodes.put("1302", new ArrayList<Node>() {
            {
                add(orderNode2);
                add(custDelNoteNode2);
                add(custBillNode2);
                add(empNode2);
                add(custNode1);
                add(productNode1);
                add(productNode2);
            }
        });
        adjNodes.put("1303", new ArrayList<Node>() {
            {
                add(orderNode3);
                add(custDelNoteNode3);
                add(custBillNode3);
                add(empNode3);
                add(custNode2);
                add(productNode1);
                add(productNode3);
            }
        });
        adjNodes.put("1304", new ArrayList<Node>() {
            {
                add(orderNode4);
                add(custDelNoteNode4);
                add(custBillNode4);
                add(empNode4);
                add(custNode3);
                add(productNode3);
                add(productNode4);
            }
        });

        // Auftrag
        adjNodes.put("1401", new ArrayList<Node>() {
            {
                add(offerNode1);
                add(custDelNoteNode1);
                add(custBillNode1);
                add(empNode1);
                add(custNode1);
                add(productNode1);
                add(productNode2);
            }
        });
        adjNodes.put("1402", new ArrayList<Node>() {
            {
                add(offerNode2);
                add(custDelNoteNode2);
                add(custBillNode2);
                add(empNode2);
                add(custNode1);
                add(productNode1);
                add(productNode2);
            }
        });
        adjNodes.put("1403", new ArrayList<Node>() {
            {
                add(offerNode3);
                add(custDelNoteNode3);
                add(custBillNode3);
                add(empNode3);
                add(custNode2);
                add(productNode1);
                add(productNode3);
            }
        });
        adjNodes.put("1404", new ArrayList<Node>() {
            {
                add(offerNode4);
                add(custDelNoteNode4);
                add(custBillNode4);
                add(empNode4);
                add(custNode3);
                add(productNode3);
                add(productNode4);
            }
        });

        // Lieferschein
        adjNodes.put("1501", new ArrayList<Node>() {
            {
                add(offerNode1);
                add(orderNode1);
                add(empNode1);
                add(custNode1);
                add(productNode1);
                add(productNode2);
            }
        });
        adjNodes.put("1502", new ArrayList<Node>() {
            {
                add(offerNode2);
                add(orderNode2);
                add(empNode2);
                add(custNode1);
                add(productNode1);
                add(productNode2);
            }
        });
        adjNodes.put("1503", new ArrayList<Node>() {
            {
                add(offerNode3);
                add(orderNode3);
                add(empNode3);
                add(custNode2);
                add(productNode1);
                add(productNode3);
            }
        });
        adjNodes.put("1504", new ArrayList<Node>() {
            {
                add(offerNode4);
                add(orderNode4);
                add(empNode4);
                add(custNode3);
                add(productNode3);
                add(productNode4);
            }
        });

        // Rechnung
        adjNodes.put("1601", new ArrayList<Node>() {
            {
                add(offerNode1);
                add(orderNode1);
                add(empNode1);
                add(custNode1);
                add(productNode1);
                add(productNode2);
            }
        });
        adjNodes.put("1602", new ArrayList<Node>() {
            {
                add(offerNode2);
                add(orderNode2);
                add(empNode2);
                add(custNode1);
                add(productNode1);
                add(productNode2);
            }
        });
        adjNodes.put("1603", new ArrayList<Node>() {
            {
                add(offerNode3);
                add(orderNode3);
                add(empNode3);
                add(custNode2);
                add(productNode1);
                add(productNode3);
            }
        });
        adjNodes.put("1604", new ArrayList<Node>() {
            {
                add(offerNode4);
                add(orderNode4);
                add(empNode4);
                add(custNode3);
                add(productNode3);
                add(productNode4);
            }
        });

        adjNodes.put("1701", new ArrayList<Node>() {
            {
                add(ordNode1);
                add(delNoteNode1);
                add(billNode1);
                add(offerNode1);
                add(orderNode1);
                add(custDelNoteNode1);
                add(custBillNode1);
                add(supNode1);
                add(custNode1);
            }
        });
        adjNodes.put("1702", new ArrayList<Node>() {
            {
                add(ordNode2);
                add(delNoteNode2);
                add(billNode2);
                add(offerNode2);
                add(orderNode2);
                add(custDelNoteNode2);
                add(custBillNode2);
                add(supNode2);
                add(custNode1);
            }
        });
        adjNodes.put("1703", new ArrayList<Node>() {
            {
                add(ordNode3);
                add(delNoteNode3);
                add(billNode3);
                add(offerNode3);
                add(orderNode3);
                add(custDelNoteNode3);
                add(custBillNode3);
                add(supNode2);
                add(custNode2);
            }
        });
        adjNodes.put("1704", new ArrayList<Node>() {
            {
                add(ordNode4);
                add(delNoteNode4);
                add(billNode4);
                add(offerNode4);
                add(orderNode4);
                add(custDelNoteNode4);
                add(custBillNode4);
                add(supNode3);
                add(custNode3);
            }
        });

        adjNodes.put("1801", new ArrayList<Node>() {
            {
                add(offerNode1);
                add(orderNode1);
                add(custBillNode1);
                add(custDelNoteNode1);
                add(offerNode2);
                add(orderNode2);
                add(custBillNode2);
                add(custDelNoteNode2);
                add(empNode1);
                add(empNode2);
            }
        });
        adjNodes.put("1802", new ArrayList<Node>() {
            {
                add(offerNode3);
                add(orderNode3);
                add(custBillNode3);
                add(custDelNoteNode3);
                add(empNode3);
            }
        });
        adjNodes.put("1803", new ArrayList<Node>() {
            {
                add(offerNode4);
                add(orderNode4);
                add(custBillNode4);
                add(custDelNoteNode4);
                add(empNode4);
            }
        });

        adjNodes.put("K00000000000001", new ArrayList<Node>() {
            {
                add(ordNode1);
                add(billNode1);
                add(delNoteNode1);
                add(empNode1);
            }
        });
        adjNodes.put("K00000000000004", new ArrayList<Node>() {
            {
                add(ordNode2);
                add(billNode2);
                add(delNoteNode2);
                add(ordNode3);
                add(billNode3);
                add(delNoteNode3);
                add(empNode2);
                add(empNode3);
            }
        });
        adjNodes.put("K00000000000005", new ArrayList<Node>() {
            {
                add(ordNode4);
                add(billNode4);
                add(delNoteNode4);
                add(empNode4);
            }
        });

        adjNodes.put("2001", new ArrayList<Node>() {
            {
                add(productNode1);
                add(productNode2);
                add(productNode3);
                add(productNode4);
                add(productNode5);
            }
        });
        adjNodes.put("2002", new ArrayList<Node>() {
            {
                add(productNode6);
                add(productNode7);
                add(productNode8);
                add(productNode9);
            }
        });

        adjNodes.put("2101", new ArrayList<Node>() {
            {
                add(stockNode1);
                add(ordNode1);
                add(ordNode2);
                add(ordNode3);
                add(offerNode1);
            }
        });
        adjNodes.put("2102", new ArrayList<Node>() {
            {
                add(stockNode1);
                add(ordNode1);
                add(ordNode2);
                add(offerNode1);
            }
        });
        adjNodes.put("2103", new ArrayList<Node>() {
            {
                add(stockNode1);
                add(ordNode3);
                add(ordNode4);
                add(delNoteNode4);
            }
        });
        adjNodes.put("2104", new ArrayList<Node>() {
            {
                add(stockNode1);
                add(ordNode4);
                add(delNoteNode4);
            }
        });
        adjNodes.put("2105", new ArrayList<Node>() {
            {
                add(stockNode1);
            }
        });
        adjNodes.put("2106", new ArrayList<Node>() {
            {
                add(stockNode2);
            }
        });
        adjNodes.put("2107", new ArrayList<Node>() {
            {
                add(stockNode2);
            }
        });
        adjNodes.put("2108", new ArrayList<Node>() {
            {
                add(stockNode2);
            }
        });
        adjNodes.put("2109", new ArrayList<Node>() {
            {
                add(stockNode2);
            }
        });
    }

    private Node createNode(final String id, final String title,
                            final ArrayList<String> attributes, final String category,
                            final String[] categories, final String nodeType) {
        return new Node() {
            {
                setId(id);
                setNodeTitle(title);
                setNodeTypeCategory(category);
                setNodeTypeCategories(Arrays.asList(categories));
                setNodeType(nodeType);
            }
        };
    }

    public Node getNode(String id) {
        int intId = Integer.parseInt(id);
        String selectSql = "SELECT TOP (1000) [uid] ,[mail] FROM [NAV2015].[dbo].[_fp_users] WHERE uid="
                + intId;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(selectSql);

            while (resultSet.next()) {
                System.out.println(resultSet.getString(1) + " "
                        + resultSet.getString(2));
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (resultSet != null)
                try {
                    resultSet.close();
                } catch (Exception e) {
                }
            if (statement != null)
                try {
                    statement.close();
                } catch (Exception e) {
                }
            if (connection != null)
                try {
                    connection.close();
                } catch (Exception e) {
                }
        }

        return null;
    }

    public Node getNodeById(String id) {
        return map.get(id);
    }

    public ArrayList<Node> getAdjNodeList(Node node) {
        return adjNodes.get(node.getId());
    }
}
