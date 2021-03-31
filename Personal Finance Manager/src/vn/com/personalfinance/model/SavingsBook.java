package vn.com.personalfinance.model;

import java.util.Date;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.DAttr.Type;

/**
 * Represents a saving book.
 * 
 * @author Nguyen Hai - Group 2
 * @version 1.0
 */
@DClass(schema="personalfinancemanagement")
public class SavingsBook extends Savings {
	public static final String S_interestRate = "interestRate";
	public static final String S_finalBalance = "finalBalance";
	
	// attributes of savings book
	@DAttr(name = S_interestRate, type = Type.Double, length = 15, optional = false)
	private double interestRate;
	
	@DAttr(name = S_finalBalance, type = Type.Double, auto = true, length = 15, mutable = false, optional = false)
	private double finalBalance;

	// constructor methods
	@DOpt(type=DOpt.Type.ObjectFormConstructor)
	public SavingsBook(@AttrRef("amount") Double amount,
			@AttrRef("name") String name,
			@AttrRef("purpose") String purpose,
			@AttrRef("startDate") Date startDate,
			@AttrRef("monthlyDuration") Integer monthlyDuration,
			@AttrRef("account") Account account,
			Double interestRate)
			{
		this(null, null, amount, name, purpose, startDate, monthlyDuration, account, interestRate);
	}

	// a shared constructor that is invoked by other constructors
	@DOpt(type = DOpt.Type.DataSourceConstructor)
	public SavingsBook(Integer id, String code, Double amount, String name,
		String purpose, Date startDate, Integer monthlyDuration, Account account, 
		Double interestRate) throws ConstraintViolationException {
		super(id, code, amount, name, purpose, startDate, monthlyDuration, account);
		this.interestRate = interestRate;
		computeFinalBalance();
	}
	
	// getter methods
	public double getInterestRate() {
		return interestRate;
	}
	
	public double getFinalBalance() {
		return finalBalance;
	}

	// setter methods
	public void setInterestRate(double interestRate) {
		this.interestRate = interestRate;
	}
	
	// calculate finalBalance from interstRate 
	private void computeFinalBalance() {
		double interest = getAmount() * interestRate / 12 * getMonthlyDuration();
		finalBalance = getAmount() + interest;
	}

}
