package com.lp.transfer.transferproject.utils;

import com.lp.transfer.transferproject.bean.RicStreamException;
import com.lp.transfer.transferproject.enums.EmReturnCode;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @Author: zhangmingkun3
 * @Description:
 * @Date: 2020/8/18 14:25
 */
public class AsyncUtils {


    /**
     * 异步调用(Function)
     * */
    public static <T, R> Future<R> asyncInvoke(Function<T, R> function, T params) {
        return ThreadPoolUtils.COMMON_POOL.submit(() -> function.apply(params));
    }

    /**
     * 异步调用(Supplier)
     * */
    public static <R> Future<R> asyncInvoke(Supplier<R> supplier) {
        return ThreadPoolUtils.COMMON_POOL.submit(supplier::get);
    }


    public static <T> void asyncInvoke(Consumer<T> consumer) {
        ThreadPoolUtils.COMMON_POOL.submit(() -> {
            try {
                consumer.accept(null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }


    public static <T> void asyncInvoke(Consumer<T> consumer, T params) {
        ThreadPoolUtils.COMMON_POOL.submit(() -> {
            try {
                consumer.accept(params);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    /**
     * 统一处理Future抛出的异常
     * */
    public static <T> void process4FutureEx(Consumer<T> consumer, T req) {
        try {
            consumer.accept(req);
        } catch (InterruptedException ex) {
            throw new RicStreamException(EmReturnCode.COMMON_UNKNOWN_EXCEPTION, "thread is interrupted", ex);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof RicStreamException) {
                throw (RicStreamException) e.getCause();
            }
            throw new RicStreamException(EmReturnCode.COMMON_UNKNOWN_EXCEPTION, "未知异常", e);
        }
    }



    /**
     * 统一处理Future抛出的异常
     * */
    public static <T,R> R process4FutureEx(Function<T,R> function, T req) {
        try {
            return function.apply(req);
        } catch (InterruptedException ex) {
            throw new RicStreamException(EmReturnCode.COMMON_UNKNOWN_EXCEPTION, "thread is interrupted", ex);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof RicStreamException) {
                throw (RicStreamException) e.getCause();
            }
            throw new RicStreamException(EmReturnCode.COMMON_UNKNOWN_EXCEPTION, "未知异常", e);
        }
    }

    /**
     * 统一处理Future抛出的异常
     * */
    public static <R> R process4FutureEx(Supplier<R> supplier) {
        try {
            return supplier.get();
        } catch (InterruptedException ex) {
            throw new RicStreamException(EmReturnCode.COMMON_UNKNOWN_EXCEPTION, "thread is interrupted", ex);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof RicStreamException) {
                throw (RicStreamException) e.getCause();
            }
            throw new RicStreamException(EmReturnCode.COMMON_UNKNOWN_EXCEPTION, "未知异常", e);
        }
    }

    public interface Consumer<T> {
        void accept(T req) throws InterruptedException, ExecutionException;
    }

    public interface Function<T,R> {
        R apply(T req) throws InterruptedException, ExecutionException;
    }

    public interface Supplier<R> {
        R get() throws InterruptedException, ExecutionException;
    }
}
