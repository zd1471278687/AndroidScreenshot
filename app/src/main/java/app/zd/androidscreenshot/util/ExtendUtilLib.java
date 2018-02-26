package app.zd.androidscreenshot.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * extend util
 * Created by zhangdong on 2018/2/26.
 */

public class ExtendUtilLib {

    /**
     * Don't let anyone instantiate this class
     */
    private ExtendUtilLib() {
    }

    /**
     * 删除列表中为空的数据
     *
     * @param dataList 列表数据
     */
    public static void removeNullDataInList(List dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return;
        }
        Iterator iterator = dataList.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() == null) {
                iterator.remove();
            }
        }
    }

    /**
     * 判断列表是否为空
     *
     * @param collection 列表数据
     * @return true: 为空 false: 不为空
     */
    public static boolean listIsNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 去掉List中的null
     *
     * @param list 原始List
     * @return 去null以后的list
     */
    public static <T> List<T> removeNull(List<T> list) {
        if (list != null) {
            for (int pos = list.size() - 1; pos >= 0; pos--) {
                if (list.get(pos) == null) {
                    list.remove(pos);
                }
            }
        }
        return list;
    }

    /**
     * 获取堆栈信息字符串
     *
     * @param throwable 异常类型
     * @return 堆栈信息字符串
     */
    public static String getStackTrace(Throwable throwable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        Throwable cause = throwable;
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        final String stacktraceAsString = result.toString();
        printWriter.close();
        return stacktraceAsString;
    }

}
