package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;

import javax.xml.crypto.Data;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class Main {
    public static List<Employee> parseCSV(String[] columnMapping, String fileInput) throws FileNotFoundException {
        List<Employee> result = new ArrayList<>();
        ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
        strategy.setColumnMapping(columnMapping); strategy.setType(Employee.class);

        try (CSVReader csvReader = new CSVReader(new FileReader(fileInput))){
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            result = csv.parse();
        } catch (IOException ex) {
            throw new FileNotFoundException();
        }
        return result;
    }

    public static boolean writeString(String s, String filename){
        try (FileWriter fw = new FileWriter(filename + ".json")) {
            fw.write(s);
        } catch (IOException e) {
            return false;
        }
        return true;
    }


    public static String readString(String inputFile) throws FileNotFoundException {
        StringBuilder result = new StringBuilder();
        try(BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException ex) {
            throw new FileNotFoundException(ex.getMessage());
        }
        return result.toString();
    }

    public static <String> String listToJson(List<Employee> list) {
        String result;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        result = (String) gson.toJson(list, new TypeToken<List<Employee>>(){}.getType());
        return result;
    }

    public static List<Employee> paseXML(String xmlFileName) throws Exception {
        List<Employee> result = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(xmlFileName));

        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element employee = (Element) node;
                // ???
                /*
                    long id = Long.parseLong(employee.getAttribute("id"));
                    String firstName = employee.getAttribute("firstName");
                    String lastName = employee.getAttribute("lastName");
                    String country = employee.getAttribute("country");
                    int age = Integer.parseInt(employee.getAttribute("age"));
                */
                // result.add(new Employee(id, firstName, lastName, country, age));
            }
        }
        return result;
    }

    private static List<Employee> jsonToList(String json) throws ParseException {
        List<Employee> result = new ArrayList<>();

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        JSONArray jsonArray = (JSONArray) new JSONParser().parse(json);
        for (Object jo : jsonArray.toArray()) {
            Employee employee = gson.fromJson(jo.toString(), Employee.class);
            result.add(employee);
        }
//        for (int i = 0; i < )

//        System.out.println(midtermJson);

//        result = gson.fromJson(midtermJson.toString(), new TypeToken<List<Employee>>(){}.getType());
        return result;
    }

    public static void main(String[] args) throws Exception {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String csvFileName = "./src/main/java/org/example/data.csv";
        String xmlFileName = "./src/main/java/org/example/data.xml";


        List<Employee> list = parseCSV(columnMapping, csvFileName);
        String json = listToJson(list);
        writeString(json, "data1");

        list = paseXML(xmlFileName);
        json = listToJson(list);
        writeString(json, "data2");

        json = readString("data1.json");
        list = jsonToList(json);
        list.forEach(System.out::println);

    }

}