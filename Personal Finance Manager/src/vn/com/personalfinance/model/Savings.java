package vn.com.personalfinance.model;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.util.Tuple;

/**
 * Represents savings. The account ID is auto-incremented.
 * 
 * @author Nguyen Hai - Group 2
 * @version 1.0
 */

@DClass(schema="personalfinancemanagement")
public abstract class Savings {
	public static final String S_id = "id";
	public static final String S_code = "code";
	public static final String S_amount = "amount";
	public static final String S_name = "name";
	public static final String S_purpose = "purpose";
	public static final String S_startDate = "startDate";
	public static final String S_monthlyDuration = "monthlyDuration";
	public static final String S_account = "account";

	// attributes of savings
	@DAttr(name = S_id, id = true, type = Type.Integer, auto = true, length = 6, mutable = false, optional = false)
	private int id;
	// static variable to keep track of account id
	private static int idCounter = 0;
	
	@DAttr(name = S_code, auto = true, type = Type.String, length = 15, mutable = false, optional = false)
	private String code;
		
	@DAttr(name = S_amount, type = Type.Double, length = 15, optional = false)
	private double amount;
		
	@DAttr(name = S_name, type = Type.String, length = 15, optional = false)
	private String name;
	
	@DAttr(name = S_purpose, type = Type.String, length = 30, optional = true)
	private String purpose;
		
	@DAttr(name = S_startDate, type = Type.Date, length = 15, optional = false) 
	private Date startDate;
	
	@DAttr(name = S_monthlyDuration, type = Type.Integer, length = 2, optional = false) 
	private int monthlyDuration;
	
	@DAttr(name = S_account, type = Type.Domain, length = 20)
	@DAssoc(ascName = "account-has-savingsBook", role = "savingsBook",
	ascType = AssocType.One2Many, endType = AssocEndType.Many,
	associate = @Associate(type = Account.class, cardMin = 1, cardMax = 1),
	dependsOn=true)
	private Account account;
	
	// static variable to keep track of savings code
	private static Map<Tuple,Integer> currNums = new LinkedHashMap<Tuple,Integer>();
	
	// constructor methods
	@DOpt(type=DOpt.Type.ObjectFormConstructor)
	protected Savings(@AttrRef("amount") Double amount,
			@AttrRef("name") String name,
			@AttrRef("purpose") String purpose,
			@AttrRef("startDate") Date startDate,
			@AttrRef("monthlyDuration") Integer monthlyDuration,
			@AttrRef("account") Account account) {
		this(null, null, amount, name, purpose, startDate, monthlyDuration, account);
	}
		
	// a shared constructor that is invoked by other constructors
	@DOpt(type=DOpt.Type.DataSourceConstructor)
	protected Savings (Integer id, String code, Double amount, String name,
		String purpose, Date startDate, Integer monthlyDuration, Account account) throws ConstraintViolationException {
		// generate an id
		this.id = nextID(id);
		this.code = nextCode(code);    
		// assign other values
		this.amount = amount;
		this.name = name;
		this.purpose = purpose;
		this.startDate = startDate;
		this.monthlyDuration = monthlyDuration;
		this.account = account;
	}
	
	// getter methods
	
	public int getId() {
		return id;
	}
	
	public String getCode() {
		return code;
	}
	
	public double getAmount() {
		return amount;
	}

	public String getName() {
		return name;
	}

	public String getPurpose() {
		return purpose;
	}

	public Date getStartDate() {
		return startDate ;
	}
	
	public int getMonthlyDuration() {
		return monthlyDuration;
	}
	
	public Account getAccount() {
		return account;
	}

	// setter methods

	public void setAmount(double amount) {
		this.amount = amount;
	}
		
	public void setName(String name) {
		this.name = name;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public void setMonthlyDuration(int monthlyDuration) {
		this.monthlyDuration = monthlyDuration;
	}
	
	public void setAccount(Account account) {
		this.account = account;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(" + getCode() + "," + getAmount() + "," + getName() + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		Savings other = (Savings) obj;
		if (id == 0) {
			if (other.id != 0)
				return false;
		} else if (!(id == other.id))
			return false;
		return true;
	}
	
	private static int nextID(Integer currID) {
		if (currID == null) { 
			// generate one
			idCounter++;
			return idCounter;
		} else { 
			// update
			int num;
			num = currID.intValue();

			// if (num <= idCounter) {
			// throw new
			// ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE,
			// "Lỗi giá trị thuộc tính ID: {0}", num + "<=" + idCounter);
			// }

			if (num > idCounter) {
				idCounter = num;
			}
			return currID;
		}
	}
	
	// automatically generate a next savings code
	private String nextCode(String currCode) throws ConstraintViolationException {
		Tuple derivingVal = Tuple.newInstance(id);
		if (currCode == null) { 
			// generate one
			Integer currNum = currNums.get(derivingVal);
			if (currNum == null) {
				currNum = id * 100;
			} else {
				currNum++;
			}
			currNums.put(derivingVal, currNum);
			return "S" + currNum;
		} else { 
			// update
			int num;
			try {
				num = Integer.parseInt(currCode.substring(1));
			} catch (RuntimeException e) {
				throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, e,
						"Lỗi giá trị thuộc tính: {0}", currCode);
			}

			Integer currMaxVal = currNums.get(derivingVal);
			if (currMaxVal == null || num > currMaxVal) {
				currNums.put(derivingVal, num);
			}

			return currCode;
		}
	}
	 
	/**
	 * @requires minVal != null /\ maxVal != null
	 * @effects update the auto-generated value of attribute <tt>attrib</tt>,
	 *          specified for <tt>derivingValue</tt>, using <tt>minVal, maxVal</tt>
	 */
	@DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
	public static void updateAutoGeneratedValue(DAttr attrib, Tuple derivingValue, Object minVal, Object maxVal)
			throws ConstraintViolationException {
		if (minVal != null && maxVal != null) {
			// check the right attribute
			if (attrib.name().equals("id")) {
				int maxIdVal = (Integer) maxVal;
				if (maxIdVal > idCounter)
					idCounter = maxIdVal;
			} else if (attrib.name().equals("code")) {
		        String maxCode = (String) maxVal;
		        
		        try {
		          int maxCodeNum = Integer.parseInt(maxCode.substring(1));
		          
		          // current max num for the semester
		          Integer currNum = currNums.get(derivingValue);
		          
		          if (currNum == null || maxCodeNum > currNum) {
		            currNums.put(derivingValue, maxCodeNum);
		          }
		          
		        } catch (RuntimeException e) {
		          throw new ConstraintViolationException(
		              ConstraintViolationException.Code.INVALID_VALUE, e, new Object[] {maxCode});
		        }
			}
		}
	}
}
