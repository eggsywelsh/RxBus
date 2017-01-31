package com.eggsy.rxbus.util;

import javax.lang.model.element.Element;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

public final class ClassValidator
{
    public static boolean isPublic(Element annotatedClass)
    {
        return annotatedClass.getModifiers().contains(PUBLIC);
    }

    public static boolean isPrivate(Element annotatedClass)
    {
        return annotatedClass.getModifiers().contains(PRIVATE);
    }

    public static boolean isAbstract(Element annotatedClass)
    {
        return annotatedClass.getModifiers().contains(ABSTRACT);
    }
}
