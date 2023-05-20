package de.pho.dsapdfreader.tools.merger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

public class ObjectMerger
{
  public static <T> T merge(T source, T target)
  {

    //we use a hashmap to store already copied values
    HashMap<Object, Object> copiedValues = new HashMap<>();

    //we loop through the class fields of each objects
    for (java.lang.reflect.Field field : source.getClass().getDeclaredFields())
    {
      try
      {
        //checks that the target does not already have a set value for this field
        if (field.get(target) == null || isEmptyList(field.get(target)) || field.getType().isEnum())
        {
          //we use the hashmap to cache an already copied object
          Object fromCache = copiedValues.get(field.get(source));
          if (fromCache != null)
          {
            //if an object was already cached we use that one
            field.set(target, fromCache);
          }
          else
          {
            Object sourceValue = field.get(source);
            if (sourceValue != null && !isSimpleType(sourceValue.getClass()) && !(sourceValue instanceof List) && !field.getType().isEnum())
            {
              //if the source value is not null we check if it is a simple type
              //if it is not a simple type we create a new instance of this object
              genInstance(field, target, sourceValue, copiedValues);
              //if the source value is null we just skip it
            }
            else if (sourceValue != null)
            {
              field.set(target, sourceValue);
            }
          }
        }
        else if (!isSimpleType(field.get(target).getClass()))
        {
          field.set(target, merge(field.get(source), field.get(target)));
        }
      }
      catch (IllegalAccessException e)
      {
        //handle any error with accessing fields
      }
    }
    return target;
  }

  private static boolean isEmptyList(Object o) throws IllegalAccessException
  {
    return (o instanceof List) && ((List) o).size() == 0;
  }

  //method to determine if the type is a simple type
  private static boolean isSimpleType(Class<?> type)
  {
    return type.equals(String.class)
        || type.equals(Integer.class)
        || type.equals(Long.class)
        || type.equals(Double.class)
        || type.equals(Float.class)
        || type.equals(Boolean.class)
        || type.equals(List.class);
  }

  //method to generate a new instance of an object
  private static <T> void genInstance(Field field, T target, Object sourceValue, HashMap<Object, Object> copiedValues)
      throws IllegalAccessException
  {
    try
    {
      //we create a new instance of the source object
      Object newInstance = sourceValue.getClass().getConstructor().newInstance();
      //we try to perform a deep merge with the source and the target object
      field.set(target, merge(sourceValue, newInstance));
      //we add the newly generated instance to the cache
      copiedValues.put(sourceValue, newInstance);
    }
    catch (InstantiationException | IllegalAccessException
           | NoSuchMethodException | InvocationTargetException e)
    {
      e.printStackTrace();
    }
  }

}
