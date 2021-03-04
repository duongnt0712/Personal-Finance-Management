package vn.com.courseman.services.coursemodule.model;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.Select;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.util.Tuple;
import vn.com.courseman.exceptions.DExCode;
import vn.com.courseman.services.enrolment.model.Enrolment;

/**
 * Represents a course module. The module id is auto-incremented from a base
 * calculated by "M" + semester-value * 100.
 * 
 * @author dmle
 * @version 2.0
 */
@DClass(schema="courseman")
public abstract class CourseModule {
	private static final int MIN_SEMESTER = 1;
	private static final int MAX_SEMESTER = 8;
	private static final int  MIN_CREDITS = 1;
	private static final int  MAX_CREDITS = 10;
	
  // attributes
  @DAttr(name="id",id=true,auto=true,type=Type.Integer,length=3,mutable=false,optional=false)
  private int id;
  private static int idCounter;

  @DAttr(name="code",auto=true,type=Type.String, length=6, 
      mutable=false,optional=false,derivedFrom={"semester"})
  private String code;
  
  @DAttr(name="name",type=Type.String,length=30,optional=false)
  private String name;
  
  // Chapter 3 - Exercise 7
  @DAttr(name="semester",type=Type.Integer,length = 2,optional=false,min = 1, max = 8)
  private int semester;
  @DAttr(name="credits",type=Type.Integer,length=2,optional=false,min=1, max = 10)
  private int credits;
  
  //Chapter 3 - Exercise 12
  @DAttr(name="enrolments",type=Type.Collection,optional = false,
	      serialisable=false,filter=@Select(clazz=Enrolment.class))
  @DAssoc(ascName="module-has-enrolments",role="coursemodule",
	      ascType=AssocType.One2Many,endType=AssocEndType.One,
	    associate=@Associate(type=Enrolment.class,cardMin=0,cardMax=30))
  private Collection<Enrolment> enrolments;  
  //derived
  private int enrolmentCount;
  //v2.6.4b: derived: average of the final mark of all enrolments
  private double averageMark;

  
  // static variable to keep track of module code
  private static Map<Tuple,Integer> currNums = new LinkedHashMap<Tuple,Integer>();

  // constructor method: create objects from data source
  @DOpt(type=DOpt.Type.DataSourceConstructor)
  protected CourseModule(Integer id, String code, String name, Integer semester, Integer credits)
      throws ConstraintViolationException {
    this.id = nextID(id);
    // automatically generate a code
    this.code = nextCode(code, semester);

    // assign other values
    this.name = name;
    this.semester = semester;
    this.credits = credits;
  }

//  @DOpt(type=DOpt.Type.ObjectFormConstructor)
//  protected CourseModule(@AttrRef("name") String name, 
//      @AttrRef("semester") int semester, @AttrRef("credits") int credits) {
//    this(null, null, name, semester, credits);
//  }

  // overloading constructor to support object type values
  // @version 2.0
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  protected CourseModule(@AttrRef("name") String name, 
      @AttrRef("semester") Integer semester, @AttrRef("credits") Integer credits) {
    this(null, null, name, semester, credits);
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
  
  public int getId() {
    return id;
  }
  
  // setter methods
  public void setName(String name) {
    this.name = name;
  }

  public void setSemester(int semester) {
	// Chapter 3 - Exercise 13
	if (semester < MIN_SEMESTER || semester > MAX_SEMESTER) {
		throw new ConstraintViolationException(DExCode.INVALID_SEMESTER, semester);
	}
    this.semester = semester;
  }

  public void setCredits(int credits) {
	// Chapter 3 - Exercise 13
	if (credits < MIN_CREDITS || credits > MAX_CREDITS) {
		throw new ConstraintViolationException(DExCode.INVALID_CREDITS, credits);
	}
    this.credits = credits;
  }

  // getter methods
  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public int getSemester() {
    return semester;
  }

  public int getCredits() {
    return credits;
  }

  // override toString
  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "(" + getCode() + "," + getName()
        + ")";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((code == null) ? 0 : code.hashCode());
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
    CourseModule other = (CourseModule) obj;
    if (code == null) {
      if (other.code != null)
        return false;
    } else if (!code.equals(other.code))
      return false;
    return true;
  }

  // automatically generate a next module code
  private String nextCode(String currCode, int semester) throws ConstraintViolationException {
    Tuple derivingVal = Tuple.newInstance(semester); 
    if (currCode == null) { // generate one
      Integer currNum = currNums.get(derivingVal); 
      if (currNum == null) {
        currNum = semester * 100;
      } else {
        currNum++;
      }
      currNums.put(derivingVal, currNum);
      return "M" + currNum;
    } else { // update
      int num;
      try {
        num = Integer.parseInt(currCode.substring(1));
      } catch (RuntimeException e) {
        throw new ConstraintViolationException(
            ConstraintViolationException.Code.INVALID_VALUE, e,
            "Lỗi giá trị thuộc tính: {0}", currCode);
      }

      Integer currMaxVal = currNums.get(derivingVal); 
      if (currMaxVal == null || num > currMaxVal) {
        currNums.put(derivingVal, num);
      }

      return currCode;
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
        
      } else if (attrib.name().equals("code")) {
        String maxCode = (String) maxVal;
        
        try {
          int maxCodeNum = Integer.parseInt(maxCode.substring(1));
          
          // current max num for the semester
          Integer currNum = currNums.get(derivingValue);
          
          if (currNum == null || maxCodeNum > currNum) {
            currNums.put(derivingValue, maxCodeNum);
          }
          
        } catch (RuntimeException e) {
          throw new ConstraintViolationException(
              ConstraintViolationException.Code.INVALID_VALUE, e, new Object[] {maxCode});
        }
      }
    }    
  }
  
  // ENROLMENT REFERENCE
  // Chapter 3 - Exercise 12
  public Collection<Enrolment> getEnrolments() {
	    return enrolments;
	  }

  @DOpt(type=DOpt.Type.LinkCountGetter)
  public Integer getEnrolmentsCount() {
    return enrolmentCount;
    //return enrolments.size();
  }

  @DOpt(type=DOpt.Type.LinkCountSetter)
  public void setEnrolmentsCount(int count) {
    enrolmentCount = count;
  }
  
  @DOpt(type=DOpt.Type.LinkAdder)
  //only need to do this for reflexive association: @MemberRef(name="enrolments")
  public boolean addEnrolment(Enrolment e) {
    if (!enrolments.contains(e))
      enrolments.add(e);
    
    // IMPORTANT: enrolment count must be updated separately by invoking setEnrolmentCount
    // otherwise computeAverageMark (below) can not be performed correctly
    // WHY? average mark is not serialisable
//    enrolmentCount++;
//    
//    // v2.6.4.b
//    computeAverageMark();
    
    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  public boolean addNewEnrolment(Enrolment e) {
    enrolments.add(e);
    
    enrolmentCount++;
    
    // v2.6.4.b
    computeAverageMark();
    
    // no other attributes changed (average mark is not serialisable!!!)
    return false; 
  }
  
  @DOpt(type=DOpt.Type.LinkAdder)
  //@MemberRef(name="enrolments")
  public boolean addEnrolment(Collection<Enrolment> enrols) {
    boolean added = false;
    for (Enrolment e : enrols) {
      if (!enrolments.contains(e)) {
        if (!added) added = true;
        enrolments.add(e);
      }
    }
    // IMPORTANT: enrolment count must be updated separately by invoking setEnrolmentCount
    // otherwise computeAverageMark (below) can not be performed correctly
    // WHY? average mark is not serialisable
//    enrolmentCount += enrols.size();

//    if (added) {
//      // avg mark is not serialisable so we need to compute it here
//      computeAverageMark();
//    }

    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  public boolean addNewEnrolment(Collection<Enrolment> enrols) {
    enrolments.addAll(enrols);
    enrolmentCount+=enrols.size();
    
    // v2.6.4.b
    computeAverageMark();

    // no other attributes changed (average mark is not serialisable!!!)
    return false; 
  }
  
  @DOpt(type=DOpt.Type.LinkRemover)
  //@MemberRef(name="enrolments")
  public boolean removeEnrolment(Enrolment e) {
    boolean removed = enrolments.remove(e);
    
    if (removed) {
      enrolmentCount--;
      
      // v2.6.4.b
      computeAverageMark();
    }
    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkUpdater)
  //@MemberRef(name="enrolments")
  public boolean updateEnrolment(Enrolment e)  throws IllegalStateException {
    // recompute using just the affected enrolment
    double totalMark = averageMark * enrolmentCount;
    
    int oldFinalMark = e.getFinalMark(true);
    
    int diff = e.getFinalMark() - oldFinalMark;
    
    // TODO: cache totalMark if needed 
    
    totalMark += diff;
    
    averageMark = totalMark / enrolmentCount;
    
    // no other attributes changed
    return true; 
  }

  public void setEnrolments(Collection<Enrolment> en) {
    this.enrolments = en;
    enrolmentCount = en.size();
    
    // v2.6.4.b
    computeAverageMark();
  }
  
//v2.6.4.b
 /**
  * @effects 
  *  computes {@link #averageMark} of all the {@link Enrolment#getFinalMark()}s 
  *  (in {@link #enrolments}.  
  */
 private void computeAverageMark() {
   if (enrolmentCount > 0) {
     double totalMark = 0d;
     for (Enrolment e : enrolments) {
       totalMark += e.getFinalMark();
     }
     
     averageMark = totalMark / enrolmentCount;
   } else {
     averageMark = 0;
   }
 }
 
 // v2.6.4.b
 public double getAverageMark() {
   return averageMark;
 }
}
