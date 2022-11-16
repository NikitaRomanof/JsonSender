package org.example;

import java.util.List;

public class HandlerClass {
    private final String operationType;
    private final String filePath;

    public HandlerClass(String operationType, String filePath) {
        this.operationType = operationType;
        this.filePath = filePath;
    }

    public List<String> handler() throws Exception {
        ParserJson parser = new ParserJson(filePath);
        List<String> result = null;
        if (!operationType.equals("search") && !operationType.equals("stat")) throw new Exception("Incorrect request");
        DataBaseRequester bd = new DataBaseRequester();

        if (operationType.equals("search")) {
            List<CriteriaJson> resultJson = parser.parserJsonSearch();
            if (parser.getErrorString() != null) throw new Exception(parser.getErrorString());
            result = bd.requestSearch(resultJson);
        } else {
            List<String> dateList = parser.parserJsonStat();
            result = bd.requestStat(dateList);
        }

        if (bd.getError() != null) throw new Exception(bd.getError());
        return result;
    }

}
