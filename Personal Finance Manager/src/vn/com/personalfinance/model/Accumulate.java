package vn.com.personalfinance.model;

import java.util.Date;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.DAttr.Type;

/**
 * Represents an accumulation.
 * 
 * @author Nguyen Hai - Group 2
 * @version 1.0
 */
@DClass(schema="personalfinancemanagement")
public class Accumulate extends Savings {
	
	// attributes of accumulate
	@DAttr(name = "expectedAmount", type = Type.Double, length = 15, optional = false)
	private double expectedAmount;
	
	@DAttr(name = "remainedAmount", type = Type.Double, auto = true, length = 15, mutable = false, optional = false)
	private double remainedAmount;

	// constructor methods
	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	public Accumulate(@AttrRef("amount") Double amount, 
			@AttrRef("name") String name,
			@AttrRef("purpose") String purpose,
			@AttrRef("startDate") Date startDate,
			@AttrRef("monthlyDuration") Integer monthlyDuration,
			@AttrRef("account") Account account,
			Double expectedAmount) {
		this(null, null, amount, name, purpose, startDate, monthlyDuration, account, expectedAmount);
	}

	// a shared constructor that is invoked by other constructors
	@DOpt(type = DOpt.Type.DataSourceConstructor)
	public Accumulate(Integer id, String code, Double amount, String name,
			String purpose, Date startDate, Integer monthlyDuration, Account account, Double expectedAmount) throws ConstraintViolationException {
		super(id, code, amount, name, purpose, startDate, monthlyDuration, account);
		this.expectedAmount = expectedAmount;
		computeRemainedAmount();
	}
	
	// getter methods
	public double getExpectedAmount() {
		return expectedAmount;
	}

	public double getRemainedAmount() {
		return remainedAmount;
	}

	// setter methods
	public void setExpectedAmount(double expectedAmount) {
		this.expectedAmount = expectedAmount;
	}
	
	// calculate accumulate
	public void computeRemainedAmount() {
		if(expectedAmount <= getAmount()) {
			remainedAmount = 0;
		} else {
		remainedAmount = expectedAmount - getAmount();
		}
	}
	
}
