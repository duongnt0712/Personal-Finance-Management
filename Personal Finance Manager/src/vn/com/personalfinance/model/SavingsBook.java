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
	
	@DAttr(name = S_finalBalance, type = Type.Double, auto = true, length = 15, mutable = false, optional = true,
			serialisable=false, derivedFrom={S_amount, S_interestRate})
	private Double finalBalance;

	private StateHistory<String, Object> stateHist;
	
	// constructor methods	
	@DOpt(type=DOpt.Type.ObjectFormConstructor)
	public SavingsBook(@AttrRef("amount") Double amount,
						@AttrRef("name") String name,
						@AttrRef("purpose") String purpose,
						@AttrRef("startDate") Date startDate,
						@AttrRef("monthlyDuration") Integer monthlyDuration,
						@AttrRef("account") Account account,
						Double interestRate) {
		this(null, null, amount, name, purpose, startDate, monthlyDuration, account, interestRate);
	}

	// a shared constructor that is invoked by other constructors
	@DOpt(type = DOpt.Type.DataSourceConstructor)
	public SavingsBook(Integer id, String code, Double amount, String name,
				String purpose, Date startDate, Integer monthlyDuration, Account account, 
				Double interestRate) throws ConstraintViolationException {
		
		super(id, code, amount, name, purpose, startDate, monthlyDuration, account);
		this.interestRate = interestRate;
		
		stateHist = new StateHistory<>();
		computeFinalBalance();
	}
	
	// getter methods
	public double getInterestRate() {
		return interestRate;
	}
	
	//devired attribute
	public double getFinalBalance() {
		return getFinalBalance(false);
	}
	
	public double getFinalBalance(boolean cached) throws IllegalStateException {
		if (cached) {
			Object val = stateHist.get(S_finalBalance);

			if (val == null)
				throw new IllegalStateException("SavingsBook.getFinalBalance: cached value is null");
			return (Double) val;
		} else {
			if (finalBalance != null)
				return finalBalance;
			else
				return 0;
		}
	}

	// setter methods
	@Override
	public void setAmount(double amount) {
		setAmount(amount, false);
	}
	
	public void setAmount(double amount, boolean computeFinalBalance) {
		amount = getAmount();
		if (computeFinalBalance)
			computeFinalBalance();
	}
	
	public void setInterestRate(double interestRate) {
		setInterestRate(interestRate, false);
	}
	
	public void setInterestRate(double interestRate, boolean computeFinalBalance) {
		this.interestRate = interestRate;
		if (computeFinalBalance)
			computeFinalBalance();
	}
	
	// calculate finalBalance from interstRate 
	@DOpt(type=DOpt.Type.DerivedAttributeUpdater)
	@AttrRef(value=S_finalBalance)
	private void computeFinalBalance() {
		stateHist.put(S_finalBalance, finalBalance);
		
		double interestAmount = getAmount() * interestRate / 12 * getMonthlyDuration();
		finalBalance = (Double)(getAmount() + interestAmount);
	}

}
