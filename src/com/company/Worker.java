package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.*;

public class Worker {

    private static String url;
    private static String username;
    private static String password;
    private static int N;
    private static Connection currentConnection;

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String url) {
        Worker.url = url;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        Worker.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        Worker.password = password;
    }

    public static int getN() {
        return N;
    }

    public static void setN(int n) {
        Worker.N = n;
    }

    //Подключение к БД
    public static Connection connectToDB() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
        currentConnection = DriverManager.getConnection(url, username, password);
        return currentConnection;
    }

    //Формирование файла 1.xml согласно заданию
    public static void createXML_1() throws SQLException, ParserConfigurationException, IOException, TransformerException {
        Statement statement = currentConnection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM TEST");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        Element root = document.createElement("entries");

        while (resultSet.next()) {
            Element entry = document.createElement("entry");
            Element field = document.createElement("field");
            field.setTextContent(String.valueOf(resultSet.getInt("FIELD")));
            entry.appendChild(field);
            root.appendChild(entry);
        }
        document.appendChild(root);

        StringWriter stringWriter = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
        transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
        FileWriter fileWriter = new FileWriter(new File("1.xml"));
        fileWriter.write(stringWriter.getBuffer().toString());
        resultSet.close();
        statement.close();
        fileWriter.close();
    }

    //Формирование файла 2.xml согласно заданию
    public static void createXML_2() throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Source xslt = new StreamSource(new File("resources/template.xsl"));
        Transformer transformer = factory.newTransformer(xslt);
        transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        Source xml = new StreamSource(new File("1.xml"));
        transformer.transform(xml, new StreamResult(new File("2.xml")));
    }

    //Подсчет и вывод арифметической суммы согласно заданию
    public static void calculateFieldValue() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File("2.xml"));
        NodeList entryElements = document.getDocumentElement().getElementsByTagName("entry");
        long sum = 0;
        for (int i = 0; i < entryElements.getLength(); i++) {
            sum += Long.parseLong(entryElements.item(i).getAttributes().getNamedItem("field").getNodeValue());
        }
        System.out.println("Сумма всех элементов FIELD = " + sum);
    }
}
