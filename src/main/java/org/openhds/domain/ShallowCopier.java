package org.openhds.domain;


import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.openhds.domain.model.UuidIdentifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Use reflection to make a shallow copy of a UuidIdentifiable object.
 * <p>
 * This ShallowCopier uses reflection to build a field-by-field copy of a given
 * UuidIdentifiable.  Where any field refers to another UuidIdentifiable object
 * (directly or through a Collection), the copy will point to a "stub" object instead
 * of the original object.  The "stub" is a default instance of the same class with
 * only its uuid and no other fields set.
 * <p>
 * Stubs give external clients enough information to rebuild object references.  Since
 * the stubs don't refer to any other objects, they prevent runaway recursion by tools
 * like automatic XML Marshallers.
 * <p>
 * This ShallowCopier assumes that all objects being copied or converted to stubs have
 * no-argument constructors that produce "default" or "blank" objects.  It also assumes
 * that Collection fields will be initialized statically or in the constructor.
 * <p>
 * If provided, populates a List of StubReferences indicating which objects were
 * converted to stubs.  The StubReferences will point to the original objects that were
 * converted to stubs.  That way, the caller has the option to supplement the stubs.
 * <p>
 * This ShallowCopier had an unfortunate dependency on Hibernate.  This is necessary
 * in order to avoid instantiating Hibernate proxy objects.  Instead we always want to
 * instantiate "real" objects using the classes as written.  This avoids issues with
 * tools like automatic JSON Marshallers, which may not know how to Marshall proxies.
 * <p>
 * "Shallow copy" has special meaning for OpenHDS because shallow copies are what we
 * send over the wire to external clients, like the OpenHDS tablet.  This is different
 * from the meaning of "shallow copy" in the context of the Java Cloneable interface.
 * <p>
 * BSH
 */
public class ShallowCopier {

    private static final Logger logger = LoggerFactory.getLogger(ShallowCopier.class);

    public static class StubReference<T extends UuidIdentifiable> {
        private final String fieldName;
        private final T original;
        private final T stub;

        public StubReference(String fieldName, T original, T stub) {
            this.fieldName = fieldName;
            this.original = original;
            this.stub = stub;
        }

        public String getFieldName() {
            return fieldName;
        }

        public T getOriginal() {
            return original;
        }

        public T getStub() {
            return stub;
        }
    }

    // Make a shallow copy of the given object.  Report which objects got converted to stubs.
    public static <T extends UuidIdentifiable> T makeShallowCopy(T original, List<StubReference> stubReport) {
        if (null == original) {
            return null;
        }

        T copy = newDefaultBlank(original);
        Set<Field> allFields = getAllFields(original);
        assignAllFields(original, copy, allFields, stubReport);
        return copy;
    }

    // Walk up the inheritance hierarchy for the given object.
    private static Set<Class<?>> getInheritanceHierarchy(UuidIdentifiable original) {
        Set<Class<?>> superclasses = new HashSet<>();

        if (null == original) {
            return superclasses;
        }

        Class<?> currentClass = original.getClass();
        while (!currentClass.equals(Object.class)) {
            superclasses.add(currentClass);
            currentClass = currentClass.getSuperclass();
        }
        return superclasses;
    }

    // Get all declared fields of the given object and its superclasses.
    private static Set<Field> getAllFields(UuidIdentifiable original) {
        Set<Field> allFields = new HashSet<>();

        if (null == original) {
            return allFields;
        }

        Set<Class<?>> superclasses = getInheritanceHierarchy(original);
        for (Class<?> clazz : superclasses) {
            allFields.addAll(getDeclaredFields(clazz));
        }
        return allFields;
    }

    // Get all declared fields for the given class.
    private static Set<Field> getDeclaredFields(Class<?> clazz) {
        Set<Field> declaredFields = new HashSet<>();

        if (null == clazz) {
            return declaredFields;
        }

        declaredFields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        return declaredFields;
    }

    // Make a new blank object of the same concrete class as the given object using its no-argument constructor.
    private static <T> T newDefaultBlank(T original) {
        if (null == original) {
            return null;
        }

        // Unfortunate dependency on Hibernate to avoid instantiating HibernateProxy objects.
        Class<? extends T> currentClass = original instanceof HibernateProxy ? Hibernate.getClass(original)
                : (Class<? extends T>) original.getClass();

        Constructor<? extends T> constructor;
        try {
            constructor = currentClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            logger.error("Can't find constructor NoSuchMethodException: " + e.getMessage());
            return null;
        }

        T blank = null;
        try {
            blank = constructor.newInstance();
        } catch (InstantiationException e) {
            logger.error("Can't invoke constructor InstantiationException: " + e.getMessage());
        } catch (IllegalAccessException e) {
            logger.error("Can't invoke constructor IllegalAccessException: " + e.getMessage());
        } catch (InvocationTargetException e) {
            logger.error("Can't invoke constructor InvocationTargetException: " + e.getMessage());
        }

        return blank;
    }

    // Make a new stub object of the same concrete class as the given object with only its uuid set.
    private static UuidIdentifiable makeStub(UuidIdentifiable original) {
        if (null == original) {
            return null;
        }

        UuidIdentifiable stub = newDefaultBlank(original);
        stub.setUuid(original.getUuid());
        return stub;
    }

    // Copy multiple fields from an original object to a target of a compatible class.  Make and report stubs as necessary.
    private static void assignAllFields(UuidIdentifiable original, UuidIdentifiable target, Set<Field> fields, List<StubReference> stubReport) {
        if (null == original || null == target || null == fields) {
            return;
        }

        for (Field field : fields) {
            // direct reference to UuidIdentifiable
            if (UuidIdentifiable.class.isAssignableFrom(field.getType())) {
                assignStub(original, target, field, stubReport);
                continue;
            }

            // Collection may contain UuidIdentifiables
            if (Collection.class.isAssignableFrom(field.getType())) {
                addStubsToCollection(original, target, field, stubReport);
                continue;
            }

            // default simple assignment
            assignField(original, target, field);
        }
    }

    // Add a Field to the ongoing Collection of Fields that git Stubbed.
    private static void reportStub(Collection<StubReference> stubReport, Field field, UuidIdentifiable original, UuidIdentifiable stub) {
        if (null == stubReport || null == field || null == original || null == stub) {
            return;
        }
        stubReport.add(new StubReference(field.getName(), original, stub));
    }

    // Copy the given field verbatim from an original object to a target of a compatible class.
    private static void assignField(UuidIdentifiable original, UuidIdentifiable target, Field field) {
        if (null == original || null == target || null == field) {
            return;
        }

        if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
            return;
        }

        try {
            field.setAccessible(true);
            field.set(target, field.get(original));
        } catch (IllegalAccessException e) {
            logger.error("Can't assign field <" + field.getName() + "> IllegalAccessException: " + e.getMessage());
        }
    }

    // Make a stub based on original an object's UuidIdentifiable field and assign it to a target of a compatible class.
    private static void assignStub(UuidIdentifiable original, UuidIdentifiable target, Field field, List<StubReference> stubReport) {
        if (null == original || null == target || null == field) {
            return;
        }

        if (!UuidIdentifiable.class.isAssignableFrom(field.getType())) {
            logger.error("Can't assign <"
                    + field.getType()
                    + "> to UuidIdentifiable field named <"
                    + field.getName()
                    + ">");
            return;
        }

        try {
            field.setAccessible(true);
            UuidIdentifiable originalEntity = (UuidIdentifiable) field.get(original);
            UuidIdentifiable stub = makeStub(originalEntity);
            field.set(target, stub);
            reportStub(stubReport, field, originalEntity, stub);
        } catch (IllegalAccessException e) {
            logger.error("Can't assign UuidIdentifiable stub to field <"
                    + field.getName()
                    + "> IllegalAccessException: "
                    + e.getMessage());
        }
    }

    // Copy elements from an original object's Collection to a compatible target's Collection.  Make stubs as necessary.
    private static void addStubsToCollection(UuidIdentifiable original, UuidIdentifiable target, Field field, List<StubReference> stubReport) {
        if (null == original || null == target || null == field) {
            return;
        }

        if (!Collection.class.isAssignableFrom(field.getType())) {
            logger.error("Can't assign <" + field.getType() + "> to Collection field named <" + field.getName() + ">");
            return;
        }

        try {
            field.setAccessible(true);

            Collection<?> originalCollection = (Collection<?>) field.get(original);
            if (null == originalCollection) {
                return;
            }

            Collection<Object> stubCollection = (Collection<Object>) field.get(target);
            if (null == stubCollection) {
                logger.warn("Skipping uninitialized Collection in field <"
                        + field.getName()
                        + "> of type <"
                        + field.getType().getName()
                        + "> in class <"
                        + original.getClass().getName()
                        + ">");
                return;
            }

            for (Object object : originalCollection) {
                if (UuidIdentifiable.class.isAssignableFrom(object.getClass())) {
                    UuidIdentifiable stub = makeStub((UuidIdentifiable) object);
                    stubCollection.add(stub);
                    reportStub(stubReport, field, (UuidIdentifiable) object, stub);
                } else {
                    stubCollection.add(object);
                }
            }

        } catch (IllegalAccessException e) {
            logger.error("Can't add element to Collection field <"
                    + field.getName()
                    + "> IllegalAccessException: "
                    + e.getMessage());
        }
    }
}
