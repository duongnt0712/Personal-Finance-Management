package vn.com.courseman.services.sclass.model;

import java.util.ArrayList;
import java.util.Collection;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.Select;
import domainapp.basics.util.Tuple;
import vn.com.courseman.services.student.model.Student;

/**
 * Represents a student class.
 * 
 * @author dmle
 *
 */
@DClass(schema="courseman")
public class SClass {
	public static final String A_averageMark = "averageMark";
  @DAttr(name="id",id=true,auto=true,length=6,mutable=false,type=Type.Integer)
  private int id;
  private static int idCounter;
  
  // Chapter 3 - Exercise 8
  // candidate identifier
  @DAttr(name="name",length=5,type=Type.String,optional=false, cid=true)
  private String name;
  
  // Chapter 3 - Exercise 5
  @DAttr(name="students",type=Type.Collection,
      serialisable=false,optional=false,
      filter=@Select(clazz=Student.class))
  @DAssoc(ascName="class-has-student",role="class",
      ascType=AssocType.One2Many,endType=AssocEndType.One,
      associate=@Associate(type=Student.class,
      cardMin=1,cardMax=3))  
  private Collection<Student> students;
  
  // derived attributes
  private int studentsCount;
  
  //Chapter 3 -Exercise 15-16
  @DAttr(name= "averageMark",type=Type.Double, auto = true, serialisable = true, mutable = false,optional = true)
  private double averageMark;
  
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  @DOpt(type=DOpt.Type.RequiredConstructor)
  public SClass(@AttrRef("name") String name) {
    this(null, name, 0.0);
  }

  // constructor to create objects from data source
  //Chapter 3 - Exercise 16
  @DOpt(type=DOpt.Type.DataSourceConstructor)
  public SClass(@AttrRef("id") Integer id,@AttrRef("name") String name, 
		  		@AttrRef("averageMark") Double averageMark) {
    this.id = nextID(id);
    this.name = name;
    
    students = new ArrayList<>();
    studentsCount = 0;

    this.averageMark = averageMark;
  }

  @DOpt(type=DOpt.Type.Setter)
  public void setName(String name) {
    this.name = name;
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  //only need to do this for reflexive association: @MemberRef(name="students")  
  public boolean addStudent(Student s) {
    if (!this.students.contains(s)) {
      students.add(s);
    }
    
    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  public boolean addNewStudent(Student s) {
    students.add(s);
    studentsCount++;
    
  //Chapter 3 - Exercise 15
    computeAverageMark();
    // no other attributes changed
    return false; 
  }
  
  @DOpt(type=DOpt.Type.LinkAdder)
  public boolean addStudent(Collection<Student> students) {
    for (Student s : students) {
      if (!this.students.contains(s)) {
        this.students.add(s);
      }
    }
    
    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  public boolean addNewStudent(Collection<Student> students) {
    this.students.addAll(students);
    studentsCount += students.size();

    ///Chapter 3 - Exercise 15
    computeAverageMark();
    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkRemover)
  //only need to do this for reflexive association: @MemberRef(name="students")
  public boolean removeStudent(Student s) {
    boolean removed = students.remove(s);
    
    if (removed) {
      studentsCount--;
    //Chapter 3 - Exercise 15
      computeAverageMark();
    }
    
    // no other attributes changed
    return false; 
  }
  
  @DOpt(type=DOpt.Type.Setter)
  public void setStudents(Collection<Student> students) {
    this.students = students;
    
    studentsCount = students.size();
  //Chapter 3 - Exercise 15
    computeAverageMark();
  }
    
  /**
   * @effects 
   *  return <tt>studentsCount</tt>
   */
  @DOpt(type=DOpt.Type.LinkCountGetter)
  public Integer getStudentsCount() {
    return studentsCount;
  }

  @DOpt(type=DOpt.Type.LinkCountSetter)
  public void setStudentsCount(int count) {
    studentsCount = count;
  }
  
  @DOpt(type=DOpt.Type.Getter)
  public String getName() {
    return name;
  }
  
  @DOpt(type=DOpt.Type.Getter)
  public Collection<Student> getStudents() {
    return students;
  }
  
  @DOpt(type=DOpt.Type.Getter)
  public int getId() {
    return id;
  }
  
  public double getAverageMark() {
	    return averageMark;
  }
  
  //Chapter 3 -Exercise 15
  /**
   * @effects 
   *  computes {@link #averageMark} of all the {@link Student#getAverageMark()}s 
   *  (in {@link #students}.  
   */
  private void computeAverageMark() {
    if (studentsCount > 0) {
      double totalMark = 0d;
      for (Student s : students) {
    	  totalMark += s.getAverageMark();
      }
      averageMark = totalMark / studentsCount;
    } else {
      averageMark = 0;
    }
  }
  
  @Override
  public String toString() {
    return "SClass("+getId()+","+getName()+")";
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SClass other = (SClass) obj;
    if (id != other.id)
      return false;
    return true;
  }

  private static int nextID(Integer currID) {
    if (currID == null) {
      idCounter++;
      return idCounter;
    } else {
      int num = currID.intValue();
      if (num > idCounter)
        idCounter = num;
      
      return currID;
    }
  }

  /**
   * @requires 
   *  minVal != null /\ maxVal != null
   * @effects 
   *  update the auto-generated value of attribute <tt>attrib</tt>, specified for <tt>derivingValue</tt>, using <tt>minVal, maxVal</tt>
   */
  @DOpt(type=DOpt.Type.AutoAttributeValueSynchroniser)
  public static void updateAutoGeneratedValue(
      DAttr attrib,
      Tuple derivingValue, 
      Object minVal, 
      Object maxVal) throws ConstraintViolationException {
    
    if (minVal != null && maxVal != null) {
      if (attrib.name().equals("id")) {
        int maxIdVal = (Integer) maxVal;
        if (maxIdVal > idCounter)  
          idCounter = maxIdVal;
      }
    }
  }
}
