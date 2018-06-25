package mksgroup.java.common;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanUtil {
    final static Logger LOG = LoggerFactory.getLogger(BeanUtil.class);

    /**
     * @param entity
     * @return Map<property name, set method instance>
     * @throws IntrospectionException
     */
    public static Map<String, Method> getWriteMethodMap(Object entity) throws IntrospectionException {
        Map<String, Method> writeMethodMap = null;

        if (writeMethodMap != null) {
            return writeMethodMap;
        }
        // <property, WriteMethod object>
        writeMethodMap = new HashMap<String, Method>();

        // Analyze the bean to build the methodMap
        BeanInfo beanInfo = Introspector.getBeanInfo(entity.getClass());
        for (PropertyDescriptor propDes : beanInfo.getPropertyDescriptors()) {
            writeMethodMap.put(propDes.getName(), propDes.getWriteMethod());
        }

        return writeMethodMap;
    }

    /**
     * @param entity
     * @return Map<property name, set method instance>
     * @throws IntrospectionException
     */
    public static Map<String, Method> getReadMethodMap(Object entity) throws IntrospectionException {
        Map<String, Method> readMethodMap = null;
        if (readMethodMap != null) {
            return readMethodMap;
        }
        // <property, ReadMethod object>
        readMethodMap = new HashMap<String, Method>();

        // Analyze the bean to build the methodMap
        BeanInfo beanInfo = Introspector.getBeanInfo(entity.getClass());
        for (PropertyDescriptor propDes : beanInfo.getPropertyDescriptors()) {
            readMethodMap.put(propDes.getName(), propDes.getReadMethod());
        }
        return readMethodMap;
    }

    public static boolean updateProperty(Object obj, String property, String value) {
        try {
            String setMethodName = "set" + (property.substring(0, 1).toUpperCase()) + property.substring(1);
            Method setMethod = obj.getClass().getMethod(setMethodName, String.class);
            setMethod.invoke(obj, value);

        } catch (Exception ex) {
            LOG.warn("Dynamic invoke setter for '" + property, ex);
            return false;
        }

        return true;
    }

    public static boolean updateProperty(Object obj, String property, Object objValue) {
        return updateProperty(obj, property, objValue, String.class.getName());
    }

    /**
     * Update the value of Object's properties dynamically.
     * @param instanceObj
     * @param property
     * @param objValue
     * @param dataType
     * @return
     */
    public static boolean updateProperty(Object instanceObj, String property, Object objValue, String dataType) {
        Object actObj = null;
        Method setMethod;
        try {
            String setMethodName = "set" + (property.substring(0, 1).toUpperCase()) + property.substring(1);

            if (!CommonUtil.isNNandNB(dataType)) {
                dataType = String.class.getName();
            }

            if (String.class.getName().equals(dataType)) {
                actObj = (objValue != null ? objValue.toString() : null);
                setMethod = instanceObj.getClass().getMethod(setMethodName, String.class);
                setMethod.invoke(instanceObj, actObj);
            } else if (Date.class.getName().equals(dataType)) {
                if (objValue instanceof Double) {
                    actObj = new Date(new Double(objValue.toString()).longValue());
                } else {
                    actObj = (Date) objValue;
                }
                setMethod = instanceObj.getClass().getMethod(setMethodName, Date.class);
                setMethod.invoke(instanceObj, actObj);
            } else if (Integer.class.getName().equals(dataType)) {
                actObj = (Integer) objValue;
                setMethod = instanceObj.getClass().getMethod(setMethodName, Integer.class);
                setMethod.invoke(instanceObj, actObj);
            } else if (Double.class.getName().equals(dataType)) {
                if (CommonUtil.isNNandNB(objValue)) {
                    actObj = new Double(objValue.toString());
                }
                setMethod = instanceObj.getClass().getMethod(setMethodName, Double.class);
                setMethod.invoke(instanceObj, actObj);
            } else {
                throw new RuntimeException("Unsupport data type of value: " + objValue.getClass());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }
    
    public static Object readProperty(Object obj, String property) {
        try {
            String getMethodName = "get" + (property.substring(0, 1).toUpperCase()) + property.substring(1);
            Method getMethod = obj.getClass().getMethod(getMethodName);
            return getMethod.invoke(obj);

        } catch (Exception ex) {
            LOG.warn("Dynamic invoke setter for '" + property, ex);
        }

        return null;
    }
    
    public static List<String> getPropertyNames(Class classType) {
        List<String> lstPropertyName = new ArrayList<String>();

        // Analyze the bean to build the methodMap
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(classType);
            for (PropertyDescriptor propDes : beanInfo.getPropertyDescriptors()) {
                lstPropertyName.add(propDes.getName());
            }
            return lstPropertyName;
        } catch (IntrospectionException iEx) {
            LOG.error("Could not get all property names of the class", iEx);
        }

        return null;
    }
    
    /**
     * Transfer data from properties into entity.
     * @param props
     * @return
     */
    public static Object propsToEntity(Properties props, Class entityClass) {
        Method setMethod;
        Object resultObj = null;

        try {
            resultObj = entityClass.newInstance();

            // Scan properties
            String setMethodName;
            String strKey;
            for (Object key : props.keySet()) {
                strKey = (String) key;
                setMethodName = buildSetMethod(strKey);

                try {
                    setMethod = entityClass.getDeclaredMethod(setMethodName, String.class);
                    setMethod.invoke(resultObj, props.getProperty(strKey));
                } catch (SecurityException ex) {
                    LOG.warn("Have no permission for method.", ex);
                } catch (NoSuchMethodException ex) {
                    LOG.warn("Method not found.", ex);
                } catch (IllegalArgumentException ex) {
                    LOG.warn("Could not invoke method.", ex);
                } catch (IllegalAccessException ex) {
                    LOG.warn("Could not invoke method.", ex);
                } catch (InvocationTargetException ex) {
                    LOG.warn("Could not invoke method.", ex);
                }
            }
        } catch (InstantiationException ex) {
            LOG.error("Could not create instance", ex);
        } catch (IllegalAccessException ex) {
            LOG.error("Could not create instance", ex);
        }

        return resultObj;
    }

    /**
     * Convert 2-dimension array "data" into List of context object.
     * @param data List of List, or List of Array
     * @param headers array of property names
     * @param objectType class of entities will built.
     * @parm skipEmptyRow true avoid the empty lines.
     * @options options String of patter key=value
     * Ex: "createdbyUsername=SYSTEM", "created=${new Date()}"
     * @return List of entity
     * 
     */
    public static List<?> getDataList(List data, String[] headers, Class objectType, boolean skipEmptyRow, String...options) {
        List<Object> listData = null;
        
        if (data == null) {
            listData = null;
        } else {
            // Lookup member data of the object from the header
            int idxMemberData;
            
            // Parse options: "string1", "string2", "string3"
            // string1: property name will be set value from string2
            // string3: property name will be set the current time.
            List<String> listOptions = Arrays.asList(options);
            String authorProperty = null;
            String authorValue = null;
            String createdProperty = null;
            if (listOptions != null && listOptions.size() > 0) {
                authorProperty = listOptions.get(0);
                authorValue = (listOptions.size() > 1) ? listOptions.get(1) : null;
                createdProperty = (listOptions.size() > 2) ? listOptions.get(2) : null;
            }

            Map<String, Method> mapSetMethod;
            try {
                Object emptyObj = objectType.newInstance();
                mapSetMethod = BeanUtil.getWriteMethodMap(emptyObj);
                
                // Initialize memory of result list.
                listData = new ArrayList<Object>(data.size());
                Object rowOutputData;
                Method setMethod;
                Object value;
                int idxHeader;
                Object[] rowInputObj = null;
                for (Object rowInputObjData : data) {
                    if (rowInputObjData instanceof List) {
                        rowInputObj = ((List) rowInputObjData).toArray();
                    }
                    
                    if ((rowInputObj != null) && (rowInputObj.length > 0)) {
                        
                        if (skipEmptyRow) {
                            if (!CommonUtil.isNNandNB(rowInputObj)) {
                                break;
                            }
                        }

                        rowOutputData = objectType.newInstance();
                        
                        // Set data for rowData
                        idxHeader = 0;
                        for (String header : headers) {
                            // Lookup header from map table
                            if (mapSetMethod.containsKey(header)) {
                                // Call method setter to set data
                                // Way 1: cellValue = AppUtil.readProperty(obj, memberData);
                                
                                setMethod = mapSetMethod.get(header);
                               
                                value = rowInputObj[idxHeader];
                                
                                // Determine type of argument of the setter method
                                value = convertDataType(setMethod, value);
                                //

                                setMethod.invoke(rowOutputData, value);
                                
                                // Update more options
                                // 1. Invoke method author
                                if (authorProperty != null) {
                                    // Get method to set author
                                    setMethod = mapSetMethod.get(authorProperty);
                                    if (setMethod != null) {
                                        setMethod.invoke(rowOutputData, authorValue);
                                    }
                                }
                                
                                // 2. Invoke method set create time
                                if (createdProperty != null) {
                                    setMethod = mapSetMethod.get(createdProperty);
                                    if (setMethod != null) {
                                        setMethod.invoke(rowOutputData, new Date());
                                    }
                                }
                            } else {
                                LOG.warn("Header " + header + "was not found in member data of the object '" + objectType.getName() + "'.");
                            }
                            idxHeader++;
                        }
                        
                        listData.add(rowOutputData);
                    } else {
                        // listData.add(emptyObj);
                    }
                }
            } catch (Exception ex) {
                LOG.warn("Could not parse the data.", ex);
            }
            
        }

        return listData;
    }

    /**
     * Analysis type of parameter of the method.
     * @param setMethod method with only 1 parameter. Ex setId(int id).
     * @param value generic object
     * @return specified object matches with type of the parameter of the method.
     */
    private static Object convertDataType(Method setMethod, Object value) {
        String typeName = null;
        Class<?>[] arrParamTypes = null;

        if (value == null) {
            return null;
        }
        try {
//            arrParamTypes = setMethod.getGenericParameterTypes();
            arrParamTypes =setMethod.getParameterTypes();
            
            if ((arrParamTypes != null) && (arrParamTypes.length > 0))  {
                // Get the first parameter type
                typeName = arrParamTypes[0].getName();

                if (Integer.class.getName().equals(typeName)) {
                    return (value != null && !value.toString().isEmpty()) ? Integer.valueOf(value.toString()) : null;                    
                } else if (Long.class.getName().equals(typeName)) {
                    return (value != null && !value.toString().isEmpty()) ? Long.valueOf(value.toString()) : null;
                } else if (Double.class.getName().equals(typeName)) {
                    return (value != null && !value.toString().isEmpty()) ? Double.valueOf(value.toString()) : null;
                } else if (String.class.getName().equals(typeName)) {
                    return value.toString();
                } else {
                    return value;
                }
            }
        } catch(Throwable th) {
            LOG.error(String.format("Could not parse data(method name, param type, paramlist, class, value class, value): '%s', '%s', '%s', '%s', '%s'", setMethod.getName(), typeName, arrParamTypes, value.getClass(), value.getClass(), value.toString()), th);
            throw th;
        }

        return null;
    }

    /**
     * Build set method from property name.
     * <br/>
     * Rule: "set" + property name with upper first character.
     * @param property
     * @return set + property with first character is upper.
     */
    private static String buildSetMethod(String property) {
        return "set" + String.valueOf(property.charAt(0)).toUpperCase() + property.substring(1);
    }

}
