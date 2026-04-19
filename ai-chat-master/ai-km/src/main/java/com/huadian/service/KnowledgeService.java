package com.huadian.service;

import com.huadian.dao.SysKMMapper;
import com.huadian.util.KMOrderByBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KnowledgeService {
    @Autowired
    SysKMMapper kmMapper;
   public void createKnowledgeBase(Map<String,Object> data){

        kmMapper.createKM(data);
    }

    public Map<String,Object> loadKnowledgeBaseByPage(Map map) {
         Map<String,Object>  orderBys = (Map) map.get("orderBys");

        if (orderBys != null){
            KMOrderByBuilder kmOrderByBuilder = new KMOrderByBuilder();
            orderBys.entrySet()
                    .stream()
                    .forEach(
                    (entry) ->{
                        kmOrderByBuilder.add(
                                entry.getKey(),
                                (String) entry.getValue()
                        );
                    }
            );
            String sortByStr = kmOrderByBuilder.build();
            map.put("orderBy",sortByStr);
        }
        else{
            KMOrderByBuilder kmOrderByBuilder = new KMOrderByBuilder();
            String sortByStr = kmOrderByBuilder.build();
            kmOrderByBuilder.add(
                   "id",
                    "desc"
            );
            map.put("orderBy",sortByStr);
        }

        List<Map<String, Object>> KMPageData = kmMapper.loadKMByPage(map);
        long total = kmMapper.loadKMByPageCount(map);
        Map<String,Object> kmPage = new HashMap<>();
        kmPage.put("total",total);
        kmPage.put("page",KMPageData);
        return kmPage;
    }
}
