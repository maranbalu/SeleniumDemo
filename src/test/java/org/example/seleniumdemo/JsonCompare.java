package org.example.seleniumdemo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

public class JsonCompare {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        File jsonFile1 = new File("path/to/your/first.json"); // replace with your first JSON file path
        File jsonFile2 = new File("path/to/your/second.json"); // replace with your second JSON file path

        JsonNode tree1 = mapper.readTree(jsonFile1);
        JsonNode tree2 = mapper.readTree(jsonFile2);

        if (!tree1.equals(tree2)) {
            System.out.println("The JSON objects are not equal. Differences:");
            compareJsonNodes(tree1, tree2, "");
        }
    }

    private static void compareJsonNodes(JsonNode node1, JsonNode node2, String path) {
        if (node1.equals(node2)) {
            return;
        }

        if (node1.isObject() && node2.isObject()) {
            ObjectNode objectNode1 = (ObjectNode) node1;
            ObjectNode objectNode2 = (ObjectNode) node2;

            Iterator<Map.Entry<String, JsonNode>> iterator = objectNode1.fields();
            while (iterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = iterator.next();
                String key = entry.getKey();
                if (objectNode2.has(key)) {
                    compareJsonNodes(entry.getValue(), objectNode2.get(key), path + "." + key);
                } else {
                    System.out.println("Key " + path + "." + key + " is missing in the second JSON object");
                }
            }
        } else {
            System.out.println("Different values at path " + path + ": " + node1 + " vs " + node2);
        }
    }
}

