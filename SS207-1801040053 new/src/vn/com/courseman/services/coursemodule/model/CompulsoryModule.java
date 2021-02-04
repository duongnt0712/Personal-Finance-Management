package vn.com.courseman.services.coursemodule.model;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.DAttr.Type;

/**
 * Represents a compulsory module (a subclass of Module)
 * 
 * @author dmle
 * 
 */
@DClass(schema="courseman")
public class CompulsoryModule extends CourseModule {

	//attribute
	@DAttr(name="lecturerName",type=Type.String,length=30,optional=false)
	private String lecturerName;
  // constructor method
  // the order of the arguments must be this: 
  // - super-class arguments first, then sub-class
//  @DOpt(type=DOpt.Type.ObjectFormConstructor)
//  public CompulsoryModule(@AttrRef("name") String name, 
//      @AttrRef("semester") int semester, @AttrRef("credits") int credits) {
//    this(null, null, name, semester, credits);
//  }

// the order of the arguments must be this: 
  // - super-class arguments first, then sub-class
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  public CompulsoryModule(@AttrRef("name") String name, 
      @AttrRef("semester") Integer semester, @AttrRef("credits") Integer credits, String lecturerName) {
    this(null, null, name, semester, credits, null);
  }

  @DOpt(type=DOpt.Type.DataSourceConstructor)
  public CompulsoryModule(Integer id, String code, String name, Integer semester, Integer credits, String lecturerName) 
    throws ConstraintViolationException {
    super(id, code, name, semester, credits);
  }
  
  public String getLecturerName() {
		return lecturerName;
	}

	public void setLecturerName(String lecturerName) {
		this.lecturerName = lecturerName;
	}

}
