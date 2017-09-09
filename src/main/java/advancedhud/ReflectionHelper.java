package advancedhud;

import java.lang.reflect.Field;

public class ReflectionHelper {

    public static final Field getDeclaredField(final Class<?> declaringClass, String... fieldNames) {
        return net.minecraftforge.fml.relauncher.ReflectionHelper.findField(declaringClass, fieldNames);
    }

    public static final <T, E> T getFieldValue(final Field fieldToAccess, E instance) {
        try {
            return (T)fieldToAccess.get(instance);
        } catch (Exception ex) {
            throw new UnableToAccessFieldException(fieldToAccess, ex);
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

}
