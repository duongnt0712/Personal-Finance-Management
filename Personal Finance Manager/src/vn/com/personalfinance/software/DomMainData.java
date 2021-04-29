package vn.com.personalfinance.software;

import domainapp.basics.exceptions.DataSourceException;
import domainapp.basics.exceptions.NotFoundException;
import domainapp.software.SoftwareFactory;
import domainapp.softwareimpl.DomSoftware;
import vn.com.personalfinance.services.account.TotalBalance;

/**
 * @overview 
 *
 * @author Nguyen Thuy Duong - Group 2
 *
 * @version 
 */
public class DomMainData {
  
  public static void main(String[] args) {
    DomSoftware sw = SoftwareFactory.createDefaultDomSoftware();
    
    // this should be run subsequent times
    sw.init();
    
    try {
      // register a domain model fragment concerning Student
		Class[] domFrag = { 
			TotalBalance.class
		};
		sw.addClasses(domFrag);
      
      // create some Student objects
		createTotalBalance(sw);
    } catch (DataSourceException e) {
      e.printStackTrace();
    }
  }

  /**
   * @effects 
   * 
   */
  private static void createTotalBalance(DomSoftware sw) throws NotFoundException, DataSourceException {
    // create a TotalBalance
    sw.addObject(TotalBalance.class, new TotalBalance());    
  }
}
