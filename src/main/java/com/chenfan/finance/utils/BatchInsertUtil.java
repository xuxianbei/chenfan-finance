package com.chenfan.finance.utils;

import cn.hutool.core.util.ReflectUtil;
import com.chenfan.finance.producer.U8Produce;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * @author liran
 */
public class BatchInsertUtil {

    public static int batchInsert(List<?> tables, Class<?> mapperClass, String methodName) {
        if (tables == null || tables.size() == 0) {
            return 0;
        }
        int insert = 5000;
        if (tables.size() < insert) {
            insert = tables.size();
        }
        int loop = tables.size() / insert;
        Object mapper = U8Produce.applicationContext.getBean(mapperClass);
        int result = 0;
        for (int i = 0; i < loop; i++) {
            int start = i * insert;
            int end = (i + 1) * insert;
            if (loop - 1 == i) {
                end = tables.size();
            }
            Object invoke = ReflectUtil.invoke(mapper, methodName, tables.subList(start, end));
            int sum = Integer.parseInt(String.valueOf(invoke));
            result = sum + result;
        }
        return result;
    }

    public static int batchInsertTask(List<?> tables, Class<?> mapperClass, String methodName) {
        ForkJoinPool fjp = new ForkJoinPool(8);
        Object mapper = U8Produce.applicationContext.getBean(mapperClass);
        ForkJoinTask<Integer> task = new SaveTask(tables, 0, tables.size(), mapper, methodName);
        return fjp.invoke(task);
    }

    public static class SaveTask extends RecursiveTask<Integer> {
        static final int THRESHOLD = 5000;
        List<?> array;
        int start;
        int end;
        Object mapper;
        String method;

        SaveTask(List<?> array, int start, int end, Object mapper, String method) {
            this.array = array;
            this.start = start;
            this.end = end;
            this.mapper = mapper;
            this.method = method;
        }

        @Override
        protected Integer compute() {
            if (end - start <= THRESHOLD) {
                Object invoke = ReflectUtil.invoke(mapper, method, array.subList(start, end));
                return Integer.parseInt(String.valueOf(invoke));
            }
            int middle = (end + start) / 2;
            SaveTask task1 = new SaveTask(this.array, start, middle, mapper, method);
            SaveTask task2 = new SaveTask(this.array, middle, end, mapper, method);
            invokeAll(task1, task2);
            int subresult1 = task1.join();
            int subresult2 = task2.join();
            int result = subresult1 + subresult2;
            return result;
        }
    }

    public static int batchInsert(List<?> tables, Class<?> mapperClass, String methodName, Object ob) {
        if (tables == null || tables.size() == 0) {
            return 0;
        }
        int insert = 5000;
        if (tables.size() < insert) {
            insert = tables.size();
        }
        int loop = tables.size() / insert;
        Object mapper = U8Produce.applicationContext.getBean(mapperClass);
        int result = 0;
        for (int i = 0; i < loop; i++) {
            int start = i * insert;
            int end = (i + 1) * insert;
            if (loop - 1 == i) {
                end = tables.size();
            }
            Object invoke = ReflectUtil.invoke(mapper, methodName, tables.subList(start, end), ob);
            int sum = Integer.parseInt(String.valueOf(invoke));
            result = sum + result;
        }
        return result;
    }

}
