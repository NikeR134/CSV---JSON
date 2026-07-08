import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Задача 1: CSV -> JSON
        System.out.println("=== Task 1: CSV to JSON ===");
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String csvFileName = "data.scv"; // или data.csv

        List<Employee> csvList = parseCSV(columnMapping, csvFileName);
        if (csvList != null && !csvList.isEmpty()) {
            String csvJson = listToJson(csvList);
            writeString(csvJson, "data.json");
        }

        // Задача 2: XML -> JSON
        System.out.println("\n=== Task 2: XML to JSON ===");
        String xmlFileName = "data.xml";
        List<Employee> xmlList = parseXML(xmlFileName);
        if (xmlList != null && !xmlList.isEmpty()) {
            String xmlJson = listToJson(xmlList);
            writeString(xmlJson, "data2.json");
        }
    }

    // Метод для парсинга CSV (из первой задачи)
    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (FileReader reader = new FileReader(fileName, StandardCharsets.UTF_8)) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();

            return csvToBean.parse();
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            return null;
        }
    }

    // Новый метод для парсинга XML
    public static List<Employee> parseXML(String fileName) {
        List<Employee> employees = new ArrayList<>();

        try {
            // Создаем фабрику и билдер для парсинга XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Парсим XML файл
            Document document = builder.parse(new File(fileName));

            // Получаем корневой элемент
            Element root = document.getDocumentElement();

            // Получаем список всех узлов employee
            NodeList nodeList = root.getChildNodes();

            // Проходим по всем узлам
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                // Проверяем, что узел является элементом и имеет имя "employee"
                if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("employee")) {
                    Element employeeElement = (Element) node;

                    // Извлекаем данные из каждого элемента
                    long id = Long.parseLong(getElementValue(employeeElement, "id"));
                    String firstName = getElementValue(employeeElement, "firstName");
                    String lastName = getElementValue(employeeElement, "lastName");
                    String country = getElementValue(employeeElement, "country");
                    int age = Integer.parseInt(getElementValue(employeeElement, "age"));

                    // Создаем объект Employee
                    Employee employee = new Employee(id, firstName, lastName, country, age);
                    employees.add(employee);
                }
            }

            System.out.println("Parsed " + employees.size() + " employees from XML");

        } catch (Exception e) {
            System.err.println("Error parsing XML file: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        return employees;
    }

    // Вспомогательный метод для получения значения элемента по тегу
    private static String getElementValue(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent();
        }
        return "";
    }

    // Метод для преобразования списка в JSON (из первой задачи)
    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        Type listType = new TypeToken<List<Employee>>() {}.getType();
        return gson.toJson(list, listType);
    }

    // Метод для записи строки в файл (из первой задачи)
    public static void writeString(String json, String fileName) {
        try (FileWriter writer = new FileWriter(fileName, StandardCharsets.UTF_8)) {
            writer.write(json);
            System.out.println("JSON successfully written to file: " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}