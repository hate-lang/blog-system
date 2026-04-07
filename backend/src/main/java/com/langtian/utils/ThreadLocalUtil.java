package com.langtian.utils;

@SuppressWarnings("all") // 压制一些编辑器没必要的泛型警告
public class ThreadLocalUtil {
    // 提供 ThreadLocal 对象（这就是那个储物柜本柜）
    // 用 Object 是为了让它成为一个万能柜，你想存 Map 还是存 User 对象都可以
    private static final ThreadLocal<Object> THREAD_LOCAL = new ThreadLocal<>();

    //1.存数据
    public static void set(Object value) {
        THREAD_LOCAL.set(value);
    }

    //2.取数据 (用了泛型强转，方便拿出来的时候直接用)
    public static <T> T get() {
        return (T) THREAD_LOCAL.get();
    }

    //3.清空数据（极其重要！！！）
    public static void remove() {
        THREAD_LOCAL.remove();
    }

}
