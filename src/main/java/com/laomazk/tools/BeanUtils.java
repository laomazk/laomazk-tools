package com.laomazk.tools;

import net.sf.cglib.beans.BeanCopier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Supplier;

public class BeanUtils {
    protected static Logger logger = LoggerFactory.getLogger(BeanUtils.class);
    public static Map<String, BeanCopier> beanCopierMap = new HashMap<>();

    public BeanUtils() {
    }

    public static void beanCopy(Object source, Object target) {
        if (null != source && null != target) {
            String beanKey = generateKey(source.getClass(), target.getClass());
            BeanCopier copier = beanCopierMap.get(beanKey);
            if (null == copier) {
                copier = BeanCopier.create(source.getClass(), target.getClass(), false);
                beanCopierMap.put(beanKey, copier);
            }

            copier.copy(source, target, new DateConverterBeanCopier());
        }
    }

    public static <T> T copy(Object source, T target) {
        if (null != source && null != target) {
            beanCopy(source, target);
            return target;
        } else {
            return null;
        }
    }

    public static <T> T copy(Object source, Class<T> targetClass) {
        if (null != source && null != targetClass) {
            Object target = null;

            try {
                Constructor<T> constructor = targetClass.getConstructor();
                target = constructor.newInstance();
            } catch (InstantiationException var4) {
                logger.error("bean copy InstantiationException", var4);
            } catch (IllegalAccessException var5) {
                logger.error("bean copy IllegalAccessException", var5);
            } catch (InvocationTargetException var6) {
                logger.error("bean copy InvocationTargetException", var6);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null == target ? null : copy(source, (T) target);
        } else {
            return null;
        }
    }

    private static String generateKey(Class<?> class1, Class<?> class2) {
        return class1.toString() + class2.toString();
    }

    public static <T> List<T> copyList(List<?> source, Class<T> cls) {
        if (source != null && !source.isEmpty()) {
            List<T> resultList = new ArrayList<>();
            Iterator<?> var3 = source.iterator();

            while (var3.hasNext()) {
                Object o = var3.next();
                resultList.add(copy(o, cls));
            }

            return resultList;
        } else {
            return Collections.emptyList();
        }
    }

    public static <T> List<T> copyList(List<?> source, Supplier<T> supplier) {
        if (source != null && !source.isEmpty()) {
            List<T> resultList = new ArrayList<>();
            for (Object o : source) {
                resultList.add(copy(o, supplier));
            }
            return resultList;
        } else {
            return Collections.emptyList();
        }
    }

    public static <T> T copy(Object source, Supplier<T> supplier) {
        if (null != source && null != supplier) {
            try {
                T t = supplier.get();
                copy(source, t);
                return t;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }

    //
//    public static void copyRemoveString(Object from, Object to, boolean isPre, String ignore) {
//        copyRemoveString(from, to, isPre, ignore, null);
//    }
//
//    /**
//     * TODO 待性能优化
//     * 拷贝对象属性
//     * from对象的属性比to对象的属性多了ignore前缀或者后缀
//     *
//     * @param from
//     * @param to
//     * @param isPre
//     * @param ignore
//     * @param ignoreProperties 忽略哪些属性不拷贝
//     */
//    public static void copyRemoveString(Object from, Object to, boolean isPre, String ignore, List<String> ignoreProperties) {
//        List<String> igp = null == ignoreProperties ? Collections.emptyList() : ignoreProperties;
//        PropertyDescriptor[] descr = PropertyUtils.getPropertyDescriptors(from);
//        PropertyDescriptor[] toDescCr = PropertyUtils.getPropertyDescriptors(to);
//        for (int i = 0; i < descr.length; i++) {
//            PropertyDescriptor d = descr[i];
//            if (d.getReadMethod() == null)
//                continue;
//
//            try {
//                String name = "";
//                if (!isPre) {
//                    if (!d.getName().endsWith(ignore)) {
//                        continue;
//                    }
//                    name = StringUtils.removeEnd(d.getName(), ignore);
//                } else {
//                    if (!d.getName().startsWith(ignore)) {
//                        continue;
//                    }
//                    name = StringUtils.removeStart(d.getName(), ignore);
//                }
//                name = (name.substring(0, 1)).toLowerCase() + name.substring(1);
//                // 是否为要忽略的属性
//                if (igp.contains(name)) {
//                    continue;
//                }
//
//                for (PropertyDescriptor propertyDescriptor : toDescCr) {
//                    if (propertyDescriptor.getName().compareToIgnoreCase(name) != 0) {
//                        continue;
//                    }
//                    if (null == propertyDescriptor.getWriteMethod()) {
//                        continue;
//                    }
//                    Object value = PropertyUtils.getProperty(from, d.getName());
//                    PropertyUtils.setProperty(to, name, value);
//                }
//
//            } catch (Exception e) {
//                throw new RuntimeException("属性名：" + d.getName() + " 在实体间拷贝时出错", e);
//            }
//        }
//    }
    public static Map<String, Object> transBean2Map(Object obj) {
        if (obj == null) {
            return null;
        } else {
            HashMap<String, Object> map = new HashMap<>();

            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                Map<String, Object> proMap = null;
                List<Map<String, Object>> beanMapList = null;
                int var7 = propertyDescriptors.length;

                for (int var8 = 0; var8 < var7; ++var8) {
                    PropertyDescriptor property = propertyDescriptors[var8];
                    String key = property.getName();
                    if (!key.equals("class") && !key.equals("page") && !key.equals("html")) {
                        Method getter = property.getReadMethod();
                        if (getter != null) {
                            Object value = getter.invoke(obj);
                            if (value != null) {
                                if (value instanceof String) {
                                    if (!value.toString().trim().isEmpty()) {
                                        map.put(key, value);
                                    }
                                } else if (isBaseDataType(value.getClass())) {
                                    map.put(key, value);
                                } else if (!value.getClass().isArray()) {
                                    if (!(value instanceof Collection)) {
                                        proMap = transBean2Map(value);
                                        map.put(key, proMap);
                                    } else {
                                        beanMapList = new ArrayList<>();
                                        Iterator iter = ((Collection) value).iterator();

                                        while (iter.hasNext()) {
                                            beanMapList.add(transBean2Map(iter.next()));
                                        }

                                        map.put(key, beanMapList);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception var14) {
                logger.error("transBean2Map err.", var14);
            }

            return map;
        }
    }

    public static Map<String, String> transBean2StringMap(Object object) {
        HashMap<String, String> dataMap = new HashMap<>();

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                Class<?> propertyType = property.getPropertyType();
                if (!Class.class.equals(propertyType)) {
                    Method getter = property.getReadMethod();
                    if (getter != null) {
                        Object value = getter.invoke(object);
                        if (value != null) {
                            if (value instanceof String) {
                                dataMap.put(key, value.toString());
                            } else {
                                dataMap.put(key, JsonUtils.writeValueAsString(value));
                            }
                        }
                    }
                }
            }
        } catch (Exception var12) {
            logger.error("transBean2Map Error", var12);
        }

        return dataMap;
    }

    public static boolean isBaseDataType(Class<?> clazz) throws Exception {
        return clazz.equals(String.class) || clazz.equals(Integer.class) || clazz.equals(Byte.class) || clazz.equals(Long.class) || clazz.equals(Double.class) || clazz.equals(Float.class) || clazz.equals(Character.class) || clazz.equals(Short.class) || clazz.equals(BigDecimal.class) || clazz.equals(BigInteger.class) || clazz.equals(Boolean.class) || clazz.equals(Date.class) || clazz.isPrimitive();
    }

}
