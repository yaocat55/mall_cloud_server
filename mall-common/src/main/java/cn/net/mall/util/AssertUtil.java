package cn.net.mall.util;

import cn.net.mall.exception.BusinessException;

import java.util.Collection;
import java.util.Objects;

/**
 * 断言工具
 *
 * @date 2024/1/9 下午4:00
 */
public abstract class AssertUtil {

    public static final int ASSERT_ERROR_CODE = 1;

    private AssertUtil() {
    }

    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new BusinessException(ASSERT_ERROR_CODE, message);
        }
    }

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new BusinessException(ASSERT_ERROR_CODE, message);
        }
    }

    public static void isNull(Object object, String message) {
        if (object != null) {
            throw new BusinessException(ASSERT_ERROR_CODE, message);
        }
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new BusinessException(ASSERT_ERROR_CODE, message);
        }
    }

    public static void hasLength(String text, String message) {
        if (text == null || text.isEmpty()) {
            throw new BusinessException(ASSERT_ERROR_CODE, message);
        }
    }

    public static void notEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new BusinessException(ASSERT_ERROR_CODE, message);
        }
    }

    public static void notEmpty(Object[] array, String message) {
        if (array == null || array.length == 0) {
            throw new BusinessException(ASSERT_ERROR_CODE, message);
        }
    }

    public static void doesNotContain(String textToSearch, String substring, String message) {
        if (textToSearch != null && !textToSearch.isEmpty()
                && substring != null && !substring.isEmpty()
                && textToSearch.contains(substring)) {
            throw new BusinessException(ASSERT_ERROR_CODE, message);
        }
    }
}
