package advancedhud;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindMethodException;

public class ReflectionHelper {

    public static final Field getDeclaredField(final Class<?> declaringClass, String... fieldNames) {
        return net.minecraftforge.fml.relauncher.ReflectionHelper.findField(declaringClass, fieldNames);
    }

    public static final Method getDeclaredMethod(final Class<?> declaringClass, String[] methodNames, Class<?>... parameterTypes) {
        Exception failed = null;
        for (String methodName : methodNames) {
            try {
                Method method = declaringClass.getDeclaredMethod(methodName, parameterTypes);
                method.setAccessible(true);
                return method;
            } catch (Exception ex) {
                failed = ex;
            }
        }
        throw new UnableToFindMethodException(methodNames, failed);
    }

    public static final Method getDeclaredMethod(final Class<?> declaringClass, String methodName, Class<?>... parameterTypes) {
        return getDeclaredMethod(declaringClass, new String[] { methodName }, parameterTypes);
    }

    public static final <T, E> T getFieldValue(final Field fieldToAccess, E instance) {
        try {
            return (T)fieldToAccess.get(instance);
        } catch (Exception ex) {
            throw new UnableToAccessFieldException(fieldToAccess, ex);
        }
    }

    public static final <T, E> void setFieldValue(final Field fieldToAccess, E instance, T value) {
        try {
            fieldToAccess.set(instance, value);
        } catch (Exception ex) {
            throw new UnableToAccessFieldException(fieldToAccess, ex);
        }
    }

    public static final <T, E> T invokeMethod(final Method methodToAccess, E instance, Object... args) {
        try {
            if (methodToAccess.getReturnType().equals(Void.TYPE)) {
                methodToAccess.invoke(instance, args);
                return null;
            } else {
                return (T)methodToAccess.invoke(instance, args);
            }
        } catch (Exception ex) {
            throw new UnableToInvokeMethodException(methodToAccess, ex);
        }
    }

    public static class UnableToAccessFieldException extends net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToAccessFieldException {
        public UnableToAccessFieldException(final String[] fieldNames, Exception ex) {
            super(fieldNames, ex);
        }

        public UnableToAccessFieldException(final Field field, Exception ex) {
            this(new String[] { field.getName() }, ex);
        }
    }

    public static class UnableToInvokeMethodException extends RuntimeException {
        public UnableToInvokeMethodException(final Method method, Exception ex) {
            super(ex);
        }
    }

}
