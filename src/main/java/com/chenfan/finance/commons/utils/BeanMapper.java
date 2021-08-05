package com.chenfan.finance.commons.utils;

import org.springframework.beans.BeanUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by jmb on 2017/9/8.
 *
 * @author j
 */
public class BeanMapper {
    /**
     * 把list转换成一个map，指定字段为key，list中的元素为value
     * @param field 指定字段名称
     * @param payApplyList 需要转换的集合
     * @param <T>
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     *//*
    public static<E,T> Map<E,T> listToMap(String field,Class<? extends E> classs,List<T> payApplyList) throws NoSuchFieldException, IllegalAccessException {

        Map<E,T>map =new HashMap<>();
        for (T t : payApplyList) {
            Field declaredField = t.getClass().getDeclaredField(field);
            declaredField.setAccessible(true);
            map.put((E)declaredField.get(t),t);
        }
        return map;
    }
*/

    /**
     * 值拷贝
     *
     * @param source 输入对象
     * @param target 输出对象
     * @param <E>    输入对象
     * @param <T>    输出对象
     * @return 封装后的对象
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static <E, T> T map(E source, T target) {
        if (null == source || null == target) {
            return null;
        }
        BeanUtils.copyProperties(source, target);
        return target;
    }

    public static <E, T> void loopSetValues1(E source, T target, Class<?> targetClass, Field[] declaredFields) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            if (declaredField.get(source) == null) {
                continue;
            }
            String meghod = "set" + declaredField.getName().substring(0, 1).toUpperCase() + declaredField.getName().substring(1);
            Method declaredMethod = targetClass.getDeclaredMethod(meghod, declaredField.getType());
            if (declaredMethod != null) {
                Object o = declaredField.get(source);
                if (null != o) {
                    declaredMethod.invoke(target, o);
                }
            }
        }
    }

    /**
     * @param source      输入对象
     * @param targetClass 输出对象的class类型
     * @param <E>         输入对象
     * @param <T>         输出对象
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    /**@Deprecated
    public static <E, T> T map(E source, Class<? extends T> targetClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (null == source || null == targetClass) {
            return null;
        }
        Class<?> sourceClass = source.getClass();
        T target = targetClass.newInstance();
        Field[] declaredFields = sourceClass.getDeclaredFields();
        loopSetValues(source, targetClass, target, declaredFields);
        return target;
    }*/
    public static <E, T> T map(E source, Class<? extends T> targetClass) throws IllegalAccessException, InstantiationException {
        if (null == source || null == targetClass) {
            return null;
        }
        T target = targetClass.newInstance();
        BeanUtils.copyProperties(source, target);
        return target;
    }

    public static <E, T> void loopSetValues(E source, Class<? extends T> targetClass, T target, Field[] declaredFields) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        loopSetValues1((E) source, (T) target, targetClass, declaredFields);
    }

    /**
     * 根据指定的字段，从传入集合泛型类型中抽出该字段值，并重新组合以该字段类型为泛型的集合（允许重复的值）
     *
     * @param list      传入的集合
     * @param field     需要抽取的字段
     * @param fieldType 该字段类型
     * @param <E>       字段泛型
     * @param <T>       传入集合泛型
     * @return 结果集合
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static <E, T> List<E> toFieldList(String field, Class<E> fieldType, List<T> list) throws NoSuchFieldException, IllegalAccessException {
        if (null == field || null == fieldType || null == list || list.size() <= 0) {
            return null;
        }
        Field declaredField = null;
        List targetList = new ArrayList<>();
        for (T t : list) {
            if (declaredField == null) {
                declaredField = t.getClass().getDeclaredField(field);
                declaredField.setAccessible(true);
            }
            Object o = declaredField.get(t);
            if (o == null) {
                continue;
            }
            targetList.add((E) o);
        }
        return targetList;
    }

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
       /* ReportFromModel p=new ReportFromModel();
        p.setMoney(2.0);
        ReportFromModel p1=new ReportFromModel();
        p1.setMoney(3.0);
        List<ReportFromModel> payApplyList=new ArrayList<>();
        payApplyList.add(p);
        payApplyList.add(p1);
        Map<String, ReportFromModel> money = listToMap(payApplyList, "money", String.class);
        for (Object o : money.keySet()) {
            System.out.println(money.get(o));
        }*/
    }


    public static <F, T> Map<F, T> listToMap(Iterable<T> list, String field, Class<F> fieldType) {
        if (list == null || field == null || fieldType == null) {
            return null;
        }
        Field classField = null;
        Map<F, T> map = new LinkedHashMap<>();
        try {
            for (T t : list) {
                if (classField == null) {
                    classField = checkField(t.getClass(), field);
                }
                Object ko = classField.get(t);
                if (ko == null) {
                    continue;
                }
                map.put((F) ko, t);
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    /**
     * 根据指定的字段，从传入集合泛型类型中抽出该字段值，并重新组合以该字段类型为泛型的集合（允许重复的值）
     *
     * @param list      传入的集合
     * @param field     需要抽取的字段
     * @param fieldType 该字段类型
     * @param <E>       字段泛型
     * @param <T>       传入集合泛型
     * @return 结果集合
     */
    public static <E, T> List<E> getFieldList(Iterable<T> list, String field, Class<E> fieldType) {
        return (List<E>) getFieldCollection(list, field, fieldType, new ArrayList<E>());
    }

    /**
     * 根据指定的字段，从传入集合泛型类型中抽出该字段值，并重新组合以该字段类型为泛型的集合
     *
     * @param list      传入的集合
     * @param field     需要抽取的字段
     * @param fieldType 该字段类型
     * @param <E>       字段泛型
     * @param <T>       传入集合泛型
     * @return 结果集合
     */
    public static <E, T> Set<E> getFieldSet(Iterable<T> list, String field, Class<E> fieldType) {
        return (Set<E>) getFieldCollection(list, field, fieldType, new HashSet<E>());
    }

    @SuppressWarnings("unchecked")
    public static <E, T> Collection<E> getFieldCollection(Iterable<T> list, String field, Class<E> fieldType, Collection<E> collection) {
        if (list == null || field == null || fieldType == null) {
            return null;
        }
        Field classField = null;
        try {
            for (T t : list) {
                if (classField == null) {
                    classField = checkField(t.getClass(), field);
                }
                Object o = classField.get(t);
                if (o == null) {
                    continue;
                }
                collection.add((E) o);
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        return collection;
    }

    /**
     * 根据传入集合，提取指定字段值做为返回Map的Key，此方法与 list2Map 不同之处在于 后者的key应当是集合中的主键概念，即集合中所有
     * 对象的该字段值都不同，所以后者的应用受到该约束，如果有两个或以上的该字段相同的key，那么后来者会替换掉之前的value。
     * <p>
     * 本方法使用场景是提取集合中的某字段，同时集合中可能有多个值在此字段一致，调用此方法后，该字段做返回 Map 的Key，Map的value为
     * 原list在此字段聚合的结果集
     *
     * @param list      需要聚合的数据
     * @param field     指定聚合字段
     * @param fieldType 该字段类型
     * @param <E>       K泛型
     * @param <T>       元数据范型
     * @return 聚合结果，结构为 Map<E, List<T>>
     */
    @SuppressWarnings("all")
    public static <E, T> Map<E, List<T>> groupBy(Iterable<T> list, String field, Class<E> fieldType) {
        if (list == null || field == null || fieldType == null) {
            return null;
        }
        Field classField = null;
        Set<E> fieldSet = getFieldSet(list, field, fieldType);
        Map<E, List<T>> group = new HashMap<>();
        for (E e : fieldSet) {
            group.put(e, new ArrayList<T>());
        }
        try {
            for (T t : list) {
                if (classField == null) {
                    classField = checkField(t.getClass(), field);
                }
                group.get((classField.get(t))).add(t);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return group;
    }

    private static Field checkField(Class<?> tClass, String field) throws NoSuchFieldException {
        Field classField = null;
        while (classField == null) {
            if (tClass == Object.class) {
                throw new NoSuchFieldException(field);
            }
            try {
                classField = tClass.getDeclaredField(field);
                classField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                tClass = tClass.getSuperclass();
            }
        }
        return classField;
    }

    /**
     * addAll方法做非空判断
     *
     * @param collection1
     * @param collection2
     * @param <T>
     */
    public static <T> void addAll(Collection collection1, Collection<? extends T> collection2) {
        if (collection2 != null) {
            collection1.addAll(collection2);
        }
    }


    /**
     * ------------------------------------------------------------以下是对集合对象操作的扩展方法-----------------------------------------------------------------
     */


    /**
     * @param target 目标类
     * @param map    原类
     */
    public static void copyMapProperties(Object target, Map<String, Object> map) {
        try {
            Class clazz2 = target.getClass();
            Field[] fields2 = clazz2.getDeclaredFields();
            for (String key : map.keySet()) {
                String name1 = key;
                Object value = (Object) map.get(key);
                for (Field field2 : fields2) {
                    String name2 = field2.getName();
                    if (name1.toUpperCase().equals(name2.toUpperCase())) {
                        field2.setAccessible(true);
                        if (value != null) {
                            field2.set(target, value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 如果某个参数为固定值，则可以加以下对象objects，如：
     * paramMap.put("userId", "aaaaaaaaaaa");
     * paramMap.put("num", 777777777);固定值
     *
     * @param list    源list
     * @param target  目标类CLASS
     * @param objects 对象集合
     * @return list
     */
    public static <T> List<T> copyPropertiesList(List<?> list, Class<T> target, Object... objects) {
        List<T> result = new ArrayList();
        if (list != null) {
            for (Object o : list) {
                try {
                    T t = target.newInstance();
                    BeanUtils.copyProperties(o, t);
                    result.add(t);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /***
     * 此方法为jdbc中调用指生成结果的方法,其他类型可以继续添加....
     * *@author  chenhs
     * update wmk
     *
     * @param clazz 类
     * @param rs    结果集合
     * @return object
     * @date 2015/11/19
     *//*
    public static Object getBeanForRs(Class clazz, ResultSet rs) {
        try {
            Object target = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                String name = field.getName();
                field.setAccessible(true);
                Class type = field.getType();
                try {
                    if (type.equals(String.class)) {
                        field.set(target, rs.getString(name));
                    } else if (type.equals(Integer.class)) {
                        field.set(target, rs.getInt(name));
                    } else if (type.equals(Long.class)) {
                        field.set(target, rs.getLong(name));
                    } else if (type.equals(Float.class)) {
                        field.set(target, rs.getFloat(name));
                    } else if (type.equals(BigDecimal.class)) {
                        field.set(target, rs.getBigDecimal(name));
                    } else if(type.equals(UUID.class)){
                        field.set(target, UUID.fromString(rs.getString(name)));
                    } else if(type.equals(Date.class)){
                        field.set(target,rs.getDate(name));
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            return target;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/
    /***
     * 利用Introspector和PropertyDescriptor 将Bean --> Map
     * @author  guoyan
     * @param obj 类
     * @return Map
     * @date 2019/01/21
     */
    public static Map<String, Object> transBean2Map(Object obj) {

        if(obj == null){
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>(0);
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                key = firstToLowerCase(key);
                // 过滤class属性
                if (!"class".equals(key)) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);
                    if (null != value){
                        map.put(key, value);
                    }
                }

            }
        } catch (Exception e) {
            System.out.println("transBean2Map Error " + e);
        }

        return map;

    }

    /**
     * 首字母转大写
     *
     * @param s 字符串
     * @return stirng
     * update wmk
     */
    private static String firstToUpcase(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1, s.length());
    }

    /**
     * 首字母转小写
     *
     * @param s 字符串
     * @return stirng
     * update wmk
     */
    private static String firstToLowerCase(String s) {
        return s.substring(0, 1).toLowerCase() + s.substring(1, s.length());
    }
}
