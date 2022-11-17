package org.aikam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParserJson {
    private static final String TAG_CRITERION = "criterias";
    private static final String TAG_FIRST_CRITERION = "lastName";
    private static final String TAG_SECOND_CRITERION_1 = "productName";
    private static final String TAG_SECOND_CRITERION_2 = "minTimes";
    private static final String TAG_THIRD_CRITERION_1 = "minExpenses";
    private static final String TAG_THIRD_CRITERION_2 = "maxExpenses";
    private static final String TAG_FOURTH_CRITERION = "badCustomers";

    private final String filePath;

    private String errorString = null;

    public String getErrorString() {
        return errorString;
    }

    public ParserJson(String filePath) {
        this.filePath = filePath;
    }

    private CriteriaJson createdObjectCriteriaJson(String lastName, String productName,
                                                   Long minTimes, Long minExpenses,
                                                   Long maxExpenses, Long badCustomers) {
        CriteriaJson result = null;
        if (lastName != null) {
            result = new CriteriaJson(lastName);
        } else if (productName != null && minTimes != null) {
            result = new CriteriaJson(productName, minTimes);
        } else if (minExpenses != null && maxExpenses != null) {
            result = new CriteriaJson(minExpenses, maxExpenses);
        } else if (badCustomers != null) {
            result = new CriteriaJson(badCustomers);
        }
        return result;
    }

    public List<CriteriaJson> parserJsonSearch() {
        List<CriteriaJson> allCCriteriaInJson = new ArrayList<>();
        try (FileReader reader = new FileReader(filePath)) {
            JSONParser parser = new JSONParser();
            JSONObject headJsonObj = (JSONObject) parser.parse(reader);
            JSONArray criteriaArray = (JSONArray) headJsonObj.get(TAG_CRITERION);

            for (Object it : criteriaArray) {
                JSONObject criteriaOnLine = (JSONObject) it;
                CriteriaJson result = createdObjectCriteriaJson((String) criteriaOnLine.get(TAG_FIRST_CRITERION),
                        (String) criteriaOnLine.get(TAG_SECOND_CRITERION_1), (Long) criteriaOnLine.get(TAG_SECOND_CRITERION_2),
                        (Long) criteriaOnLine.get(TAG_THIRD_CRITERION_1), (Long) criteriaOnLine.get(TAG_THIRD_CRITERION_2),
                        (Long) criteriaOnLine.get(TAG_FOURTH_CRITERION));
                allCCriteriaInJson.add(result);
            }

        } catch (IOException e) {
            errorString = "File not found";
            e.printStackTrace();
        } catch (ParseException e) {
            errorString = "Parse Exception";
            e.printStackTrace();
        }

        return allCCriteriaInJson;
    }

    public List<String> parserJsonStat() {
        List<String> result = new ArrayList<>();

        try (FileReader reader = new FileReader(filePath)) {
            JSONParser parser = new JSONParser();
            JSONObject headJsonObj = (JSONObject) parser.parse(reader);
            String firstDate = (String) headJsonObj.get("startDate");
            String secondDate = (String) headJsonObj.get("endDate");
            result.add(firstDate);
            result.add(secondDate);
        } catch (IOException e) {
            errorString = "File not found";
            e.printStackTrace();
        } catch (ParseException e) {
            errorString = "Parse Exception";
            e.printStackTrace();
        }
        return result;
    }

    public void writerJson(String filePathIn, List<String> resultRequest) {
        String filePathOut = filePathIn.replaceAll(".json", "Out.json");
        try (FileWriter file = new FileWriter(filePathOut)) {
            for (String it : resultRequest) {
                file.write(it);
            }
            file.flush();
        } catch (IOException e) {
            errorString = "Error when trying to write to file";
            e.printStackTrace();
        }
    }

    public void writerError(String error) {
        String filePathOut = filePath.replaceAll(".json", "Out.json");
        try (FileWriter file = new FileWriter(filePathOut)) {
            file.write("{\n");
            file.write("  \"type\": \"error\",\n");
            file.write("  \"message\": \"" + error + "\"\n");
            file.write("}");
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


