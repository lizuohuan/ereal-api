package com.magic.ereal.api.config;

import org.apache.log4j.Logger;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Eric Xie on 2017/3/29 0029.
 */
public class DESConfig {



    private static final String PROPERTIES_PATH = "des.properties";

    private static Logger logger = Logger.getLogger(DESConfig.class);

    private static Map<String,String> result = new HashMap<String, String>();

    /**参数名*/
    public static final String PARAM_NAME = "param";

    /**结果获取参数的Key*/
    public static final String PARAM_OBJECT = "params";

    private DESConfig(){}

    static {
        init();
    }

    private static void init() {
        try {
            Properties properties  = new Properties();
            InputStream in = DESConfig.class.getClassLoader().getResourceAsStream(PROPERTIES_PATH);
            properties.load(in);
            Enumeration enumeration = properties.keys();
            while (enumeration.hasMoreElements()){
                String key = (String)enumeration.nextElement();
                result.put(key,properties.getProperty(key));
            }
        }catch (Exception e){
            logger.error("初始化配置文件失败",e);
        }
    }

    public static String getKey(){
        return result.get("key");
    }

    public static boolean getIsEnable(){
        String enable = result.get("isEnable");
        return "true".equals(enable);
    }


}
