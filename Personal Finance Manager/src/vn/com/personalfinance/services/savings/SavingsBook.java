package vn.com.personalfinance.services.savings;

import java.util.Collection;
import java.util.Date;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.Select;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.util.cache.StateHistory;
import vn.com.personalfinance.exceptions.DExCode;
import vn.com.personalfinance.services.log.Log;

/**
 * Represents a saving book.
 * 
 * @author Nguyen Hai - Group 2
 * @version 1.0
 */
@DClass(schema="personalfinancemanagement")
public class SavingsBook extends Savings {
	public static final String S_monthlyDuration = "monthlyDuration";
	public static final String S_interestRate = "interestRate";
	public static final String S_finalBalance = "finalBalance";
	
	// attributes of savings book
	@DAttr(name = "log", type = Type.Collection, optional = false, serialisable = false,
	filter = @Select(clazz = Log.class))
	@DAssoc(ascName = "savings-has-log", role = "savings",
	ascType = AssocType.One2Many, endType = AssocEndType.One,
	associate = @Associate(type = Log.class, cardMin = 1, cardMax = 1))
	@Override public Collection<Log> getLog() { return super.getLog(); }
	
	@DAttr(name = S_monthlyDuration, type = Type.Integer, length = 2, optional = false) 
	private int monthlyDuration;
	
	@DAttr(name = S_interestRate, type = Type.Double, length = 15, optional = false)
	private double interestRate;
	
	@DAttr(name = S_finalBalance, type = Type.Double, auto = true, length = 15, mutable = false, optional = true,
			serialisable=false, derivedFrom={S_amount, S_interestRate, S_monthlyDuration})
	private Double finalBalance;

	private StateHistory<String, Object> stateHist;
	
	// constructor methods	
	@DOpt(type=DOpt.Type.ObjectFormConstructor)
	public SavingsBook(@AttrRef("name") String name,
					   @AttrRef("purpose") String purpose,
					   @AttrRef("amount") Double amount,	
				       @AttrRef("startDate") Date startDate,
					   @AttrRef("monthlyDuration") Integer monthlyDuration,
					   @AttrRef("interestRate") Double interestRate) {
		this(null, name, purpose, amount, startDate, monthlyDuration, interestRate);
	}

	// a shared constructor that is invoked by other constructors
	@DOpt(type = DOpt.Type.DataSourceConstructor)
	public SavingsBook(String id, String name, String purpose, 
		Double amount, Date startDate, Integer monthlyDuration, 
		Double interestRate) throws ConstraintViolationException {
		
		super(id, name, purpose, amount, startDate);
		this.monthlyDuration = monthlyDuration;
		this.interestRate = interestRate;
		
		stateHist = new StateHistory<>();
		computeFinalBalance();
	}
	
	// getter methods
	public int getMonthlyDuration() {
		return monthlyDuration;
	}
	
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
//	@Override
//	public void setLogCount(int logCount) {
//		if(logCount < 0 || logCount > 1) {
//			throw new ConstraintViolationException(DExCode.INVALID_LOG, logCount);
//		}
//		super.setLogCount(logCount);
//	}
	
	public void setMonthlyDuration(int monthlyDuration) {
		setMonthlyDuration(monthlyDuration, false);
	}
	
	public void setMonthlyDuration(int monthlyDuration, boolean computeFinalBalance) {
		this.monthlyDuration = monthlyDuration;
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
	
	/*
	 * @DOpt(type = DOpt.Type.LinkAdder) // only need to do this for reflexive
	 * association: @MemberRef(name="accounts") public boolean addLog(Log s) { if
	 * (!getLog().contains(s)) getLog().add(s);
	 * 
	 * // no other attributes changed return true; }
	 * 
	 * @DOpt(type = DOpt.Type.LinkAdderNew) public boolean addNewLog(Log s) {
	 * if(getLogCount() < 0 || getLogCount() > 1) { throw new
	 * ConstraintViolationException(DExCode.INVALID_LOG, getLogCount()); }
	 * getLog().add(s); int count = getLogCount(); setLogCount(count + 1);
	 * 
	 * // no other attributes changed return true; }
	 * 
	 * @DOpt(type = DOpt.Type.LinkAdder) public boolean addLog(Collection<Log> log)
	 * { for (Log s : log) { if (!getLog().contains(s)) { getLog().add(s); } } // no
	 * other attributes changed return true; }
	 * 
	 * @DOpt(type = DOpt.Type.LinkAdderNew) public boolean addNewLog(Collection<Log>
	 * log) { if(getLogCount() < 0 || getLogCount() > 1) { throw new
	 * ConstraintViolationException(DExCode.INVALID_LOG, getLogCount()); }
	 * getLog().addAll(log); int count = getLogCount(); count += log.size();
	 * setLogCount(count);
	 * 
	 * // no other attributes changed (average mark is not serialisable!!!) return
	 * true; }
	 * 
	 * @DOpt(type = DOpt.Type.LinkRemover) // only need to do this for reflexive
	 * association: @MemberRef(name="accounts") public boolean removeLog(Log s) {
	 * boolean removed = getLog().remove(s);
	 * 
	 * if (removed) { int count = getLogCount(); setLogCount(count - 1);
	 * 
	 * double currentAccountBalance = s.getAccount().getBalance();
	 * s.getAccount().setBalance(currentAccountBalance += s.getAmount()); } // no
	 * other attributes changed return true; }
	 */
	
	// calculate finalBalance 
	@DOpt(type=DOpt.Type.DerivedAttributeUpdater)
	@AttrRef(value=S_finalBalance)
	public void computeFinalBalance() {
		stateHist.put(S_finalBalance, finalBalance);
		
		double interestAmount = getAmount() * interestRate / 12 * monthlyDuration;
		finalBalance = (Double)(getAmount() + interestAmount);
	}
	
	// automatically generate the next account id
	@Override
	public String nextID(String id) throws ConstraintViolationException {
		if (id == null) { // generate a new id
			idCounter++;
			return "S" + idCounter;
		} else {
			// update id
			int num;
			try {
				num = Integer.parseInt(id.substring(1));
			} catch (RuntimeException e) {
				throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, e,
						new Object[] { id });
			}

			if (num > idCounter) {
				idCounter = num;
			}

			return id;
		}
	}
}
