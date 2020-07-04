package mksgroup.java.common;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * This class provides some common utilities about XML.
 * @author ThachLN
 *
 */
public class XMLUtil {
    /** For logging. */
    private final static Logger LOG = LoggerFactory.getLogger(XMLUtil.class);

    @Deprecated
    public static String toXML(Map mapObj) {
        StringBuffer resultXMLBuff = new StringBuffer();
        Map.Entry mapEntryItem;
        Object key;
        Object value;
        
        if (mapObj != null) {
            for (Object itemObj : mapObj.entrySet()) {
                mapEntryItem = (Map.Entry) itemObj;
                key = mapEntryItem.getKey();
                value = mapEntryItem.getValue();
                resultXMLBuff.append("<").append(key).append(">")
                             .append(value).append("</").append(key).append(">")
                             .append(Constant.LFCR);
            }
        }
        
        return resultXMLBuff.toString();
    }
    
    /**
     * Serialize the object into XML-based string.
     * @param obj
     * @return
     * @throws IntrospectionException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static String toXML(Object obj) {
        StringBuffer resultXMLBuff = new StringBuffer();
        try {

            if (obj instanceof List) {
                List listObj = (List) obj;
                for (Object objItem : listObj) {
                    resultXMLBuff.append(toXMLFromSimObj(objItem)).append(Constant.LFCR);
                }
            } else if (obj instanceof Object[]) {
                Object[] arrObj = (Object[]) obj;
                for (Object objItem : arrObj) {
                    resultXMLBuff.append(toXMLFromSimObj(objItem)).append(Constant.LFCR);
                }
            } else { // Simple object
                return toXMLFromSimObj(obj);
            }

        } catch (Exception ex) {
            LOG.error("Could not convert object to XML.", ex);
        } 

        return resultXMLBuff.toString();
    }
    
    /**
     * Convert the object into the String-bases XML.
     * @param obj
     * @return
     * @throws IntrospectionException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private static String toXMLFromSimObj(Object obj) throws IntrospectionException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        Method method;
        Object result;
        StringBuffer xmlBuff = new StringBuffer();
        String className = obj.getClass().getSimpleName();
        
        xmlBuff.append("<").append(className)
               .append(Constant.SPACE).append("class=")
                                      .append(Constant.QUOTE)
                                      .append(obj.getClass().getName())
                                      .append(Constant.QUOTE)
               .append(">");
        xmlBuff.append(Constant.LF);
        
        for (PropertyDescriptor propsDesc : beanInfo.getPropertyDescriptors()) {
            // skip the built-property "class"
            if (propsDesc.getName().equals("class")) {
                continue;
            }
            method = propsDesc.getReadMethod();
            result = method.invoke(obj, new Object[0]);
            
            
            
            xmlBuff.append("  <").append(propsDesc.getName()).append(">");
            
            if (CommonUtil.isPrimitive(result)) {
                if (result != null) {
                    xmlBuff.append(result);
                }
            } else { // result is not a primitive type
                xmlBuff.append(Constant.LFCR);
                xmlBuff.append(toXML(result));
            }
                   
            xmlBuff.append("</").append(propsDesc.getName()).append(">");
            xmlBuff.append(Constant.LF);
        }
        xmlBuff.append("</").append(className).append(">");
        
        return xmlBuff.toString();
    }
    
    /**
     * Parse the InputStream into the Document.
     * @param is
     * @return Document
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public static Document parse(InputStream is) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(is);
    }
}
