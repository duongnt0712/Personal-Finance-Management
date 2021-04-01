package vn.com.personalfinance.model;

import java.util.Date;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.util.cache.StateHistory;

/**
 * Represents an accumulation.
 * 
 * @author Nguyen Hai - Group 2
 * @version 1.0
 */
@DClass(schema="personalfinancemanagement")
public class Accumulate extends Savings {
	public static final String S_expectedAmount = "expectedAmount";
	public static final String S_remainedAmount = "remainedAmount";
	
	// attributes of accumulate
	@DAttr(name = S_expectedAmount, type = Type.Double, length = 15, optional = false)
	private double expectedAmount;
	
	@DAttr(name = S_remainedAmount, type = Type.Double, auto = true, length = 15, mutable = false, optional = true,
			serialisable=false, derivedFrom={S_amount, S_expectedAmount})
	private Double remainedAmount;

	private StateHistory<String, Object> stateHist;
	
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
					String purpose, Date startDate, Integer monthlyDuration, Account account, 
					Double expectedAmount) throws ConstraintViolationException {
		
		super(id, code, amount, name, purpose, startDate, monthlyDuration, account);
		this.expectedAmount = expectedAmount;
		
		stateHist = new StateHistory<>();
		computeRemainedAmount();
	}
	
	// getter methods
	public double getExpectedAmount() {
		return expectedAmount;
	}

	//devired attribute
	public double getRemainedAmount() {
		return getRemainedAmount(false);
	}
	
	public double getRemainedAmount(boolean cached) throws IllegalStateException {
		if (cached) {
			Object val = stateHist.get(S_remainedAmount);

			if (val == null)
				throw new IllegalStateException("Accumulate.getRemainedAmount: cached value is null");
			return (Double) val;
		} else {
			if (remainedAmount != null)
				return remainedAmount;
			else
				return 0;
		}
	}

	// setter methods
	@Override
	public void setAmount(double amount) {
		setAmount(amount, false);
	}
	
	public void setAmount(double amount, boolean computeRemainedAmount) {
		amount = getAmount();
		if (computeRemainedAmount)
			computeRemainedAmount();
	}
	
	public void setExpectedAmount(double expectedAmount) {
		setExpectedAmount(expectedAmount, false);
	}
	
	public void setExpectedAmount(double expectedAmount, boolean computeRemainedAmount) {
		this.expectedAmount = expectedAmount;
		if (computeRemainedAmount)
			computeRemainedAmount();
	}
	
	// calculate accumulate
	public void computeRemainedAmount() {
		stateHist.put(S_remainedAmount, remainedAmount);
		
		if(expectedAmount <= getAmount()) {
			remainedAmount = 0.0;
		} else {
		remainedAmount = expectedAmount - getAmount();
		}
	}
	
}
