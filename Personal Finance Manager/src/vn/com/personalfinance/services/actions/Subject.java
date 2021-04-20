package vn.com.personalfinance.services.actions;

import java.util.ArrayList;
import java.util.Collection;

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
import domainapp.basics.util.Tuple;
import vn.com.personalfinance.services.account.Account;

@DClass(schema="personalfinancemanagement")
public class Subject {
//	attributes
	@DAttr(name = "id", id = true, auto = true, length = 6, mutable = false, type = Type.Integer)
	private int id; 
	
//	static variable to keep track of account id
	private static int idCounter;
	
	@DAttr(name = "name", type = Type.String, length = 20, optional = false, cid = true) 
	private String name;
	
	@DAttr (name = "actions", type = Type.Collection, serialisable = false, optional = false, filter = @Select(clazz = BorrowAndLend.class))
	@DAssoc (ascName = "subject-has-action", role = "subject", ascType = AssocType.One2Many, endType = AssocEndType.One,
			associate = @Associate(type = BorrowAndLend.class, cardMin = 0, cardMax = 30))
	private Collection<BorrowAndLend> actions;
	// derived attributes
	private int actionsCount;
	 
	 
	// from object form: Account is not included 
	@DOpt(type=DOpt.Type.ObjectFormConstructor)
	@DOpt(type=DOpt.Type.RequiredConstructor)
	public Subject (@AttrRef("name") String name) {
		this(null, name);
	}
	
	// from data source
	@DOpt(type=DOpt.Type.DataSourceConstructor)
	public Subject (@AttrRef("id") Integer id, @AttrRef("name") String name ) {
		this.id = nextId(id);
		this.name = name;
		
		actions = new ArrayList<>();
		actionsCount = 0;
	}
	
//	Setter methods
	@DOpt(type=DOpt.Type.Setter)
	public void setName(String name) {
		this.name = name;
	}
	
//	Getter methods
	@DOpt(type=DOpt.Type.Getter)
	public int getId() {
		return id;
	}
	
	@DOpt(type=DOpt.Type.Getter)
	public String getName() {
		return name;
	}
	
//	add existed object into collection
	@DOpt (type = DOpt.Type.LinkAdder)
	public boolean addActions (BorrowAndLend a) {
		if (!this.actions.contains(a)) {
			actions.add(a);
		}
		// no other attributes changed
		return false;
	}
	
//	add new object into collection
	@DOpt (type = DOpt.Type.LinkAdderNew)
	public boolean addNewActions (BorrowAndLend a) {
		actions.add(a);
		actionsCount++;
		return false;
	}
	
	@DOpt (type = DOpt.Type.LinkAdder)
	public boolean addActions (Collection<BorrowAndLend> actions) {
		for (BorrowAndLend a : actions) {
			if (!this.actions.contains(a)) {
				this.actions.add(a);
			}
		}
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewActions (Collection<BorrowAndLend> actions) {
		this.actions.addAll(actions);
		actionsCount += actions.size();
		// no other attributes changed
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkRemover)
	// only need to do this for reflexive association: @MemberRef(name="accounts")
	public boolean removeActions (BorrowAndLend a) {
		boolean removed = actions.remove(a);

		if (removed) {
			actionsCount--;
		}
		// no other attributes changed
		return false;
	}
	
	@DOpt(type=DOpt.Type.Setter)
	public void setActions(Collection<BorrowAndLend> actions) {
		this.actions = actions;
		actionsCount = actions.size();
	}
	
	@DOpt(type=DOpt.Type.Getter)
	public Collection<BorrowAndLend> getActions() {
		return actions;
	}
	
	/**
	 * @effects return <tt>accountsCount</tt>
	 */
	@DOpt(type=DOpt.Type.LinkCountSetter)
	public void setActionsCount(int actionsCount) {
		this.actionsCount = actionsCount;
	}
	
	@DOpt(type=DOpt.Type.LinkCountGetter)
	public int getActionsCount() {
		return actionsCount;
	}

	private static int nextId(Integer currID) {
		if (currID == null) {
			idCounter++;
			return idCounter;
		} else {
			int num = currID.intValue();
			if (num > idCounter)
				idCounter = num;
			return currID;
		}
	}
	
	@Override
	public String toString() {
		return "Subject [id=" + id + ", name=" + name + "]";
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
		Subject other = (Subject) obj;
		if (id != other.id)
			return false;
		return true;
	}

	/**
	   * @requires 
	   *  minVal != null /\ maxVal != null
	   * @effects 
	   *  update the auto-generated value of attribute <tt>attrib</tt>, specified for <tt>derivingValue</tt>, using <tt>minVal, maxVal</tt>
	   */
	@DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
	public static void updateAutoGeneratedValue(DAttr attrib, Tuple derivingValue, Object minVal, Object maxVal)
			throws ConstraintViolationException {

		if (minVal != null && maxVal != null) {
			int maxIdVal = (Integer) maxVal;
			if (maxIdVal > idCounter)
				idCounter = maxIdVal;
		}
	}	
}
