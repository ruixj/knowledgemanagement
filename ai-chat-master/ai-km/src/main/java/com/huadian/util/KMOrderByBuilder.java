package com.huadian.util;

import java.util.*;

// 排序工具类
public class KMOrderByBuilder {

    private Map<String,String> fieldDirection= new HashMap();
    private List<String> fieldList = List.of("name",
                                             "created_at" ,
                                             "updated_at" );
    private static Set<String> allowedFields = Set.of("id", "name", "created_at", "updated_at" );
    private static Set<String> directions = Set.of("desc","asc");
    public KMOrderByBuilder add(String field, String direction) {
        if (isValidField(field) && isValidDirection(direction)) {
            //fields.add(field);
            fieldDirection.put(field,direction);
        }
        return this;
    }

    private boolean isValidDirection(String direction) {
        return directions.contains(direction.toLowerCase());
    }

    public String build() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fieldList.size(); i++){
            String field = fieldList.get(i);
            if (fieldDirection.containsKey(field)){
                sb.append(field);
                sb.append(" ");
                sb.append(fieldDirection.get(field));
            }
        }
        return sb.toString() ;
    }

    private boolean isValidField(String field) {
        return allowedFields.contains(field.toLowerCase());
    }


}