package vn.com.personalfinance.services.savings;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

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
import vn.com.personalfinance.services.savingstransaction.SavingsTransaction;

/**
 * Represents savings. The account ID is auto-incremented.
 * 
 * @author Nguyen Hai - Group 2
 * @version 1.0
 */

@DClass(schema="personalfinancemanagement")
public abstract class Savings {
	public static final String S_id = "id";
	public static final String S_amount = "amount";
	public static final String S_name = "name";
	public static final String S_purpose = "purpose";
	public static final String S_startDate = "startDate";

	// attributes of savings
	@DAttr(name = S_id, id = true, type = Type.String, auto = true, length = 6, mutable = false, optional = false)
	private String id;
	// static variable to keep track of account id
	public static int idCounter = 0;
	
	@DAttr(name = S_name, type = Type.String, length = 20, optional = false, cid=true)
	private String name;
	
	@DAttr(name = S_purpose, type = Type.String, length = 30, optional = true)
	private String purpose;
		
	@DAttr(name = S_amount, type = Type.Double, length = 15, optional = false)
	private double amount;
		
	@DAttr(name = S_startDate, type = Type.Date, length = 15, optional = false) 
	private Date startDate;
	
	@DAttr(name = "savingsTransaction", type = Type.Collection, optional = false, serialisable = false,
	filter = @Select(clazz = SavingsTransaction.class))
	@DAssoc(ascName = "savings-has-savingsTransaction", role = "savings",
	ascType = AssocType.One2Many, endType = AssocEndType.One,
	associate = @Associate(type = SavingsTransaction.class, cardMin = 0, cardMax = MetaConstants.CARD_MORE))
	private Collection<SavingsTransaction> savingsTransaction;

	// derived
	private int savingsTransactionCount;
	
	// constructor methods
	@DOpt(type=DOpt.Type.ObjectFormConstructor)
	protected Savings(@AttrRef("name") String name,
			@AttrRef("purpose") String purpose,
			@AttrRef("amount") Double amount,
			@AttrRef("startDate") Date startDate) {
		this(null, name, purpose, amount, startDate);
	}
		
	// a shared constructor that is invoked by other constructors
	@DOpt(type=DOpt.Type.DataSourceConstructor)
	protected Savings (String id, String name, String purpose, 
		Double amount,  Date startDate) throws ConstraintViolationException {
		// generate an id
		this.id = nextID(id);   
		// assign other values
		this.name = name;
		this.purpose = purpose;
		this.amount = amount;
		this.startDate = startDate;
	}
	
	// getter methods
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public String getPurpose() {
		return purpose;
	}
	
	public double getAmount() {
		return amount;
	}

	public Date getStartDate() {
		return startDate ;
	}

	// setter methods
	
	public void setName(String name) {
		this.name = name;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}
		
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(" + getId() + "," + getAmount() + "," + getName() + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Integer.parseInt(id.substring(1));
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
		
		if (!(id.equals(other.id)))
			return false;
		return true;
	}
	
	public abstract String nextID(String currID);
	
//	private static int nextID(Integer currID) {
//		if (currID == null) { 
//			// generate one
//			idCounter++;
//			return "S" + idCounter;
//		} else { 
//			// update
//			int num;
//			num = currID.intValue();
//
//			if (num > idCounter) {
//				idCounter = num;
//			}
//			return currID;
//		}
//	}
	 
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
				String maxID = (String) maxVal;

				try {
					int maxIDNum = Integer.parseInt(maxID.substring(1));

					if (maxIDNum > idCounter) {
						idCounter = maxIDNum;
					}

				} catch (RuntimeException e) {
					throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, e,
							new Object[] { maxID });
				}
			}
		}
	}
	
	// SAVINGSTRANSACTION PART
	public Collection<SavingsTransaction> getSavingsTransaction() {
		return savingsTransaction;
	}

	@DOpt(type = DOpt.Type.LinkCountGetter)
	public Integer getSavingsTransactionCount() {
		return savingsTransactionCount;
	}

	@DOpt(type = DOpt.Type.LinkCountSetter)
	public void setSavingsTransactionCount(int savingsTransactionCount) {
		this.savingsTransactionCount = savingsTransactionCount;
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	// only need to do this for reflexive association: @MemberRef(name="accounts")
	public boolean addSavingsTransaction(SavingsTransaction s) {
		if (!this.savingsTransaction.contains(s))
			savingsTransaction.add(s);

		// no other attributes changed
		return false;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewSavingsTransaction(SavingsTransaction s) {
		savingsTransaction.add(s);
		savingsTransactionCount++;
		// no other attributes changed
		return false;
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addSavingsTransaction(Collection<SavingsTransaction> savingsTransaction) {
		for (SavingsTransaction s : savingsTransaction) {
			if (!this.savingsTransaction.contains(s)) {
				this.savingsTransaction.add(s);
			}
		}
		// no other attributes changed
		return false;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewSavingsTransaction(Collection<SavingsTransaction> savingsTransaction) {
		this.savingsTransaction.addAll(savingsTransaction);
		savingsTransactionCount += savingsTransaction.size();
		// no other attributes changed (average mark is not serialisable!!!)
		return false;
	}

	@DOpt(type = DOpt.Type.LinkRemover)
	// only need to do this for reflexive association: @MemberRef(name="accounts")
	public boolean removeSavingsTransaction(SavingsTransaction s) {
		boolean removed = savingsTransaction.remove(s);

		if (removed) {
			savingsTransactionCount--;
		}
		// no other attributes changed
		return false;
	}

	public void setSavingsTransaction(Collection<SavingsTransaction> savingsTransaction) {
		this.savingsTransaction = savingsTransaction;
		savingsTransactionCount = savingsTransaction.size();
	}
}
