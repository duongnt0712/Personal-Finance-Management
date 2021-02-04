/**
 * 
 */
package vn.com.courseman.test.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import vn.com.courseman.model.Student;

/**
 * @overview 
 *
 * @author dmle
 *
 * @version 
 */
public class ReflectStudent {
  public static void main(String[] args) {
    Class c = Student.class;
    
    Method[] methods = c.getDeclaredMethods();
    
    for (Method m : methods) {
      System.out.println(m);
    }
    
    Annotation[] anos = c.getAnnotations();
  }
}
