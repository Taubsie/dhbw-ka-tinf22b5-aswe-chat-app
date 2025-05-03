package de.dhbw.ka.tinf22b5.util;

import java.lang.reflect.Field;

public class FieldSwitcher {

    public static boolean switchField(Object switchObj, String name, Object newObj) {
        try {
            return switchField(switchObj, switchObj.getClass().getDeclaredField(name), newObj);
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    public static boolean switchField(Object switchObj, Field field, Object newObj) {
        field.setAccessible(true);

        try {
            field.set(switchObj, newObj);
        } catch (IllegalAccessException e) {
            return false;
        }

        return true;
    }
}
