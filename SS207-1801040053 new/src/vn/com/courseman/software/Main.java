package vn.com.courseman.software;

import domainapp.software.SoftwareFactory;
import domainapp.softwareimpl.DomSoftware;
import vn.com.courseman.services.coursemodule.model.CompulsoryModule;
import vn.com.courseman.services.coursemodule.model.CourseModule;
import vn.com.courseman.services.coursemodule.model.ElectiveModule;
import vn.com.courseman.services.enrolment.model.Enrolment;
import vn.com.courseman.services.sclass.model.SClass;
import vn.com.courseman.services.student.model.City;
import vn.com.courseman.services.student.model.Student;
import vn.com.courseman.services.student.reports.StudentsByCityJoinReport;
import vn.com.courseman.services.student.reports.StudentsByNameReport;


/**
 * @overview 
 *  Create and run a UI-based {@link DomSoftware} for a pre-defined model.  
 *  
 * @author dmle
 */
public class Main {
  
  // 1. initialise the model
  static final Class[] model = {
      CourseModule.class, 
      CompulsoryModule.class, 
      ElectiveModule.class, 
      Enrolment.class, 
      Student.class, 
      City.class, 
      SClass.class,
      // reports
      StudentsByNameReport.class,
      StudentsByCityJoinReport.class
  };
  
  /**
   * @effects 
   *  create and run a UI-based {@link DomSoftware} for a pre-defined model. 
   */
  public static void main(String[] args){
    // 2. create UI software
    DomSoftware sw = SoftwareFactory.createUIDomSoftware();
    
    // 3. run
    // create in memory configuration
    System.setProperty("domainapp.setup.SerialiseConfiguration", "false");
    
    // 3. run it
    try {
      sw.run(model);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }   
  }

}
