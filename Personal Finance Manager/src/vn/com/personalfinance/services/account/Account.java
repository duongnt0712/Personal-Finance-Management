package vn.com.personalfinance.services.account;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.MetaConstants;
import domainapp.basics.model.meta.Select;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.util.Tuple;
import vn.com.personalfinance.services.log.Log;
import vn.com.personalfinance.services.borrowandlend.BorrowAndLend;
import vn.com.personalfinance.services.expense.model.DailyExpense;

/**
 * Represents an account. The account ID is auto-incremented from the current year.
 * 
 * @author Group 2
 * @version 1.0
 */
@DClass(schema="personalfinancemanagement")
public class Account {
	public static final String A_id = "id";
	public static final String A_name = "name";
	public static final String A_type = "type";
	public static final String A_balance = "balance";
	
	// attributes of accounts
	@DAttr(name = A_id, id = true, type = Type.String, auto = true, length = 6, mutable = false, optional = false)
	private String id;
	// static variable to keep track of account id
	private static int idCounter = 0;
	
	@DAttr(name = A_name, type = Type.String, length = 20, optional = false, cid=true)
	private String name;
	
	@DAttr(name = A_type, type = Type.Domain, length = 20)
	@DAssoc(ascName = "type-has-account", role = "account", ascType = AssocType.One2Many,
	endType = AssocEndType.Many, associate = @Associate(type = AccountType.class, cardMin = 1, cardMax = 1), dependsOn=true)
	private AccountType type;
	
	@DAttr(name = A_balance, type = Type.Double, length = 15, optional = false)
	private double balance;
	
	@DAttr(name = "dailyExpense", type = Type.Collection, optional = false,
	serialisable = false, filter = @Select(clazz = DailyExpense.class))
	@DAssoc(ascName = "account-has-dailyExpense", role = "account",
	ascType = AssocType.One2Many, endType = AssocEndType.One, 
	associate = @Associate(type = DailyExpense.class, cardMin = 0, cardMax = MetaConstants.CARD_MORE ))
	private Collection<DailyExpense> dailyExpense;
	private int dailyExpenseCount;
	
	@DAttr(name = "log", type = Type.Collection, optional = false, 
	serialisable = false, filter = @Select(clazz = Log.class))
	@DAssoc(ascName = "account-has-log", role = "account",
	ascType = AssocType.One2Many, endType = AssocEndType.One,
	associate = @Associate(type = Log.class, cardMin = 0, cardMax = MetaConstants.CARD_MORE))
	private Collection<Log> log;
	private int logCount;
	
	@DAttr(name = "borrowAndLend", type = Type.Collection, optional = false,
	serialisable = false, filter = @Select(clazz = BorrowAndLend.class))
	@DAssoc(ascName = "account-has-borrowAndLend", role = "account",
	ascType = AssocType.One2Many, endType = AssocEndType.One, 
	associate = @Associate(type = BorrowAndLend.class, cardMin = 0, cardMax = MetaConstants.CARD_MORE ))
	private Collection<BorrowAndLend> borrowAndLend;
	private int borrowAndLendCount;
	
	// constructor methods
	// form constructor into an object
	@DOpt(type=DOpt.Type.ObjectFormConstructor)
	@DOpt(type=DOpt.Type.RequiredConstructor)
	public Account(@AttrRef("name") String name, 
			@AttrRef("balance") Double balance) {
		this(null, name, null, balance);
	}
	
	@DOpt(type=DOpt.Type.ObjectFormConstructor)
	public Account(@AttrRef("name") String name, 
			@AttrRef("type") AccountType type,
			@AttrRef("balance") Double balance) {
		this(null, name, type, balance);
	}
	
	// a shared constructor that is invoked by other constructors
	// load db 
	@DOpt(type=DOpt.Type.DataSourceConstructor)
	public Account (String id, String name, AccountType type, Double balance) throws ConstraintViolationException{
		// generate an id
	    this.id = nextID(id);
	    
	    // assign other values
	    this.name = name;
	    this.type = type;
	    this.balance = balance;

	    dailyExpense = new ArrayList<>();
	    dailyExpenseCount = 0;
	    
	    log = new ArrayList<>();
	    logCount = 0;
	    
	    borrowAndLend = new ArrayList<>();
	    borrowAndLendCount = 0;
	}
	
	// DailyExpense Assoc
	@DOpt(type = DOpt.Type.LinkAdder)
	// only need to do this for reflexive association: @MemberRef(name="accounts")
	public boolean addDailyExpense(DailyExpense s) {
		if (!this.dailyExpense.contains(s)) {
			dailyExpense.add(s);
		}
		// no other attributes changed
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewDailyExpense(DailyExpense s) {
		dailyExpense.add(s);
		dailyExpenseCount++;
		// no other attributes changed
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addDailyExpense(Collection<DailyExpense> dailyExpense) {
		for (DailyExpense s : dailyExpense) {
			if (!this.dailyExpense.contains(s)) {
				this.dailyExpense.add(s);
			}
		}
		// no other attributes changed
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewDailyExpense(Collection<DailyExpense> dailyExpense) {
		this.dailyExpense.addAll(dailyExpense);
		dailyExpenseCount += dailyExpense.size();
		// no other attributes changed
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkRemover)
	// only need to do this for reflexive association: @MemberRef(name="accounts")
	public boolean removeDailyExpense(DailyExpense s) {
		boolean removed = dailyExpense.remove(s);

		if (removed) {
			dailyExpenseCount--;
			if(s.getId().contains("I")) {
				balance-=s.getAmount();
			} else {
				balance+=s.getAmount();
			}
			
		}
		// no other attributes changed
		return false;
	}
	
	// BorrowAndLend Assoc
	@DOpt(type = DOpt.Type.LinkAdder)
	// only need to do this for reflexive association: @MemberRef(name="accounts")
	public boolean addBorrowAndLend(BorrowAndLend bL) {
		if (!this.borrowAndLend.contains(bL))
			borrowAndLend.add(bL);

		// no other attributes changed
		return false;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewborrowAndLend(BorrowAndLend bL) {
		borrowAndLend.add(bL);
		borrowAndLendCount++;
		// no other attributes changed
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addBorrowAndLend(Collection<BorrowAndLend> bL) {
		for (BorrowAndLend b : bL) {
			if (!this.borrowAndLend.contains(b)) {
				this.borrowAndLend.add(b);
			}
		}
		// no other attributes changed
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewBorrowAndLend(Collection<BorrowAndLend> bL) {
		this.borrowAndLend.addAll(bL);
		borrowAndLendCount += bL.size();
		// no other attributes changed (average mark is not serialisable!!!)
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkRemover)
	// only need to do this for reflexive association: @MemberRef(name="accounts")
	public boolean removeBorrowAndLend(BorrowAndLend bL) {
		boolean removed = borrowAndLend.remove(bL);

		if (removed) {
			borrowAndLendCount--;
		}
		// no other attributes changed
		return false;
	}
	
	// Log Assoc
	@DOpt(type = DOpt.Type.LinkAdder)
	// only need to do this for reflexive association: @MemberRef(name="accounts")
	public boolean addLog(Log s) {
		if (!this.log.contains(s))
			log.add(s);

		// no other attributes changed
		return false;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewLog(Log s) {
		log.add(s);
		logCount++;
		// no other attributes changed
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addLog(Collection<Log> log) {
		for (Log s : log) {
			if (!this.log.contains(s)) {
				this.log.add(s);
			}
		}
		// no other attributes changed
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewLog(Collection<Log> log) {
		this.log.addAll(log);
		logCount += log.size();
		// no other attributes changed (average mark is not serialisable!!!)
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkRemover)
	// only need to do this for reflexive association: @MemberRef(name="accounts")
	public boolean removeLog(Log s) {
		boolean removed = log.remove(s);

		if (removed) {
			logCount--;
			balance += s.getAmount();
		}
		// no other attributes changed
		return false;
	}

	// getter methods
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public AccountType getType() {
		return type;
	}

	public double getBalance() {
		return balance ;
	}
	
	public Collection<DailyExpense> getDailyExpense() {
		return dailyExpense;
	}
	
	@DOpt(type=DOpt.Type.LinkCountGetter)
	public int getDailyExpenseCount() {
		return dailyExpenseCount;
	}

	public Collection<Log> getLog() {
		return log;
	}
	
	public int getLogCount() {
		return logCount;
	}
	
	public Collection<BorrowAndLend> getBorrowAndLend() {
		return borrowAndLend;
	}
	
	public int getBorrowAndLendCount() {
		return borrowAndLendCount;
	}
	
	// setter methods
	public void setName(String name) {
		this.name = name;
	}

	public void setType(AccountType type) {
		this.type = type;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	public void setDailyExpense(Collection<DailyExpense> dailyExpense) {
		this.dailyExpense = dailyExpense;
		dailyExpenseCount = dailyExpense.size();
	}
	
	@DOpt(type=DOpt.Type.LinkCountSetter)
	public void setDailyExpenseCount(int dailyExpenseCount) {
		this.dailyExpenseCount = dailyExpenseCount;
	}
	
	public void setLog(Collection<Log> log) {
		this.log = log;
		logCount = log.size();
	}
	
	public void setLogCount(int logCount) {
		this.logCount = logCount;
	}
	
	public void setBorrowAndLend(Collection<BorrowAndLend> borrowAndLend) {
		this.borrowAndLend = borrowAndLend;
		borrowAndLendCount = borrowAndLend.size();
	}
	
	public void setBorrowAndLend(int borrowAndLendCount) {
		this.borrowAndLendCount = borrowAndLendCount;
	}
	
	// override toString
	/**
	 * @effects returns <code>this.id</code>
	 */
	@Override
	public String toString() {
		return toString(true);
	}
	
	/**
	 * @effects returns <code>Account(id,name,type,balance)</code>.
	 */
	public String toString(boolean full) {
	    if (full)
	      return "Account(" + id + "," + name + "," +  ((type != null) ? "," + type.getName() : "") + "," + balance + ")";
	    else
	      return "Account(" + id + ")";
	  }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Account other = (Account) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	// automatically generate the next account id
	private String nextID(String id) throws ConstraintViolationException {
		if (id == null) { // generate a new id
			if (idCounter == 0) {
				idCounter = Calendar.getInstance().get(Calendar.YEAR);
			} else {
				idCounter++;
			}
			return "A" + idCounter;
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
	      //TODO: update this for the correct attribute if there are more than one auto attributes of this class 

	    	if (attrib.name().equals("id")) {
	  		  String maxId = (String) maxVal;
	  		  
	  		  try {
	  		    int maxIdNum = Integer.parseInt(maxId.substring(1));
	  		    
	  		    if (maxIdNum > idCounter) // extra check
	  		      idCounter = maxIdNum;
	  		    
	  		  } catch (RuntimeException e) {
	  		    throw new ConstraintViolationException(
	  		        ConstraintViolationException.Code.INVALID_VALUE, e, new Object[] {maxId});
	  		  }
	      	}	    
	    }
	 }
}
