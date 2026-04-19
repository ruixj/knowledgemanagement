package com.huadian.util;

import org.springframework.ui.ModelMap;

public class ErrorUtil {

    public static ModelMap retErrMsg(String errId){
        ModelMap m = new ModelMap();

        String msg = MessageUtils.message(errId);
        String msg_code = MessageUtils.message(errId + ".code");
        int iCode = Integer.parseInt(msg_code.trim());
        m.put("code", iCode);
        m.put("message", msg);

        return m;


    }

    public static ModelMap retErrMsg(String errId,Object... errParameters){
        ModelMap m = new ModelMap();

        String msg = MessageUtils.message(errId,errParameters);
        String msg_code = MessageUtils.message(errId + ".code");
        m.put("code", msg_code);
        m.put("message", msg);

        return m;

    }

    public static ModelMap retErrMsg(String errId,Object map,Object... errParameters){
        ModelMap m = new ModelMap();

        String msg = MessageUtils.message(errId,errParameters);
        String msg_code = MessageUtils.message(errId + ".code");

        m.put("code", msg_code);
        m.put("message", msg);

        m.put("data", map);
        return m;

    }


    public static ModelMap retErrMsg(String errId, Object map){
        ModelMap m = new ModelMap();

        String msg = MessageUtils.message(errId);
        String msg_code = MessageUtils.message(errId + ".code");

        m.put("code", Integer.valueOf(msg_code.trim()));
        m.put("message", msg);

        m.put("data", map);

        return m;

    }

}
