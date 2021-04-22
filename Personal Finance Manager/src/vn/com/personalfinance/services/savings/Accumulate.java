package vn.com.personalfinance.services.savings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.DAttr.Type;
import vn.com.personalfinance.services.log.Log;

/**
 * Represents an accumulation.
 * 
 * @author Nguyen Hai - Group 2
 * @version 1.0
 */
@DClass(schema="personalfinancemanagement")
public class Accumulate extends Savings {
	public static final String S_remainedAmount = "remainedAmount";
	
	@DAttr(name = S_remainedAmount, type = Type.Double, auto = true, length = 15, mutable = false, optional = true,
			serialisable=true)
	private Double remainedAmount;
	
	// constructor methods
	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	public Accumulate(@AttrRef("name") String name,
			@AttrRef("purpose") String purpose,
			@AttrRef("amount") Double amount, 
			@AttrRef("startDate") Date startDate) {
		this(null, name, purpose, amount, startDate, amount);
	}

	// a shared constructor that is invoked by other constructors
	@DOpt(type = DOpt.Type.DataSourceConstructor)
	public Accumulate(Integer id, String name, String purpose, 
		Double amount, Date startDate, Double remainedAmount) throws ConstraintViolationException {
		super(id, name, purpose, amount, startDate);
		
		Collection<Log> log = getLog();
		setLog(log = new ArrayList<>());
		setLogCount(0);
		
		this.remainedAmount=remainedAmount;
	}
	
	//getter
	public double getRemainedAmount() {
		return remainedAmount;
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
	
	// calculate accumulate
	private void computeRemainedAmount() {
		if (getLogCount() >= 0 && remainedAmount <= getAmount() && remainedAmount >= 0) {
			double accumAmount = 0d;
			for (Log log : getLog()) {
				accumAmount += log.getAmount();
			}
			if (accumAmount <= getAmount()) {
				remainedAmount = getAmount() - accumAmount;
			} else {
				remainedAmount = 0d;
			}
		}
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	// only need to do this for reflexive association: @MemberRef(name="accounts")
	public boolean addLog(Log s) {
		if (!getLog().contains(s))
			getLog().add(s);

		// no other attributes changed
		return true;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewLog(Log s) {
		getLog().add(s);
		int count = getLogCount();
		setLogCount(count + 1);

		computeRemainedAmount();
		// no other attributes changed
		return true;
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addLog(Collection<Log> log) {
		for (Log s : log) {
			if (!getLog().contains(s)) {
				getLog().add(s);
			}
		}
		// no other attributes changed
		return true;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewLog(Collection<Log> log) {
		getLog().addAll(log);
		int count = getLogCount();
		count += log.size();
		setLogCount(count);

		computeRemainedAmount();
		// no other attributes changed (average mark is not serialisable!!!)
		return true;
	}

	@DOpt(type = DOpt.Type.LinkRemover)
	// only need to do this for reflexive association: @MemberRef(name="accounts")
	public boolean removeLog(Log s) {
		boolean removed = getLog().remove(s);

		if (removed) {
			int count = getLogCount();
			setLogCount(count - 1);

			double currentAccountBalance = s.getAccount().getBalance();
			s.getAccount().setBalance(currentAccountBalance += s.getAmount());

			computeRemainedAmount();
		}
		// no other attributes changed
		return true;
	}
}
