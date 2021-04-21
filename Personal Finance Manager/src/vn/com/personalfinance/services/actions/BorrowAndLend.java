package vn.com.personalfinance.services.actions;

import java.util.Date;

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
import vn.com.personalfinance.services.account.Account;

@DClass(schema="personalfinancemanagement")
public class BorrowAndLend {
//		static final attribute
		public static final String T_id = "id";
		public static final String T_account = "account";
		public static final String T_name = "name";
		public static final String T_subject = "subject";
		public static final String T_type = "type";
		public static final String T_money = "money";
		public static final String T_start_date = "startDate";
		public static final String T_period = "period";
		public static final String T_interested_rate = "interestedRate";
		public static final String T_final_money = "finalMoney";
		
//		attributes
		@DAttr (name = T_id, id = true, type = Type.Integer, auto = true, length = 6, mutable = false, optional = false)
		private int id;
		
//		static variable to keep track of account id
		private static int idCounter = 0;
		
		@DAttr (name = T_account, type = Type.Domain, length = 20, optional = false, cid = true)
		@DAssoc (ascName = "account-has-action", role = "action", ascType = AssocType.One2Many, endType = AssocEndType.Many,
				associate = @Associate(type = Account.class, cardMin = 1, cardMax = 1), dependsOn = true)
		private Account account; 
		
		@DAttr (name = T_name, type = Type.String, length = 30, optional = false)
		private String name;
		
		@DAttr (name = T_subject, type = Type.Domain, length = 30, optional = false)
		@DAssoc (ascName = "subject-has-action", role = "action", ascType = AssocType.One2Many, endType = AssocEndType.Many,
				associate = @Associate(type = Subjects.class, cardMin = 1, cardMax = 1), dependsOn = true)
		private Subjects subject;
		
		@DAttr (name = T_type, type = Type.Domain, length = 30, optional = false)
		private ActionType type;
		
		@DAttr (name = T_money, type = Type.Double, length = 15, optional = false)
		private double money;
		
		@DAttr (name = T_start_date, type = Type.Date, length = 20, optional = false)
		private Date startDate;
		
		@DAttr (name = T_period, type = Type.Integer, length = 20, mutable = true, optional = false)
		private int period;
		
		@DAttr (name = T_interested_rate, type = Type.Double, length = 20, optional = false)
		private double interestedRate;
		
//		derived attribute
		@DAttr (name = T_final_money, type = Type.Double, auto = true, length = 20, mutable = false, optional = false)
		private double finalMoney;
		
//		Constructor method
		@DOpt(type = DOpt.Type.ObjectFormConstructor)
		@DOpt(type = DOpt.Type.RequiredConstructor)
		public BorrowAndLend (@AttrRef("account") Account account, @AttrRef("name") String name,  @AttrRef("subject") Subjects subject, 
				@AttrRef("type") ActionType type, @AttrRef("money") Double money, @AttrRef("startDate") Date startDate, 
				@AttrRef("period") Integer period, @AttrRef("interestedRate") Double interestedRate) {
			this(null, account, name, subject, type, money, startDate, period, interestedRate, null);
		}
		
//		a shared constructor that is invoked by other constructors
		@DOpt (type = DOpt.Type.DataSourceConstructor)
		public BorrowAndLend (Integer id, Account account, String name, Subjects subject, ActionType type, Double money, Date startDate, Integer period, Double interestedRate, Double finalMoney) {
		    this.id = nextId(id);
		    
		    this.account = account;
			this.name = name;
			this.subject = subject;
			this.type = type;
			this.money = money;
			this.startDate = startDate;
			this.period = period;
			this.interestedRate = interestedRate;
			
			this.finalMoney = computeFinalMoney(money, interestedRate, period);
		}

//		Getter Method
		public int getId() {
			return id;
		}
		
		public Account getAccount() {
			return account;
		}
		
		public String getName() {
			return name;
		}
		
		public Subjects getSubject() {
			return subject;
		}

		public ActionType getType() {
			return type;
		}

		public double getMoney() {
			return money;
		}
		
		public Date getStartDate() {
			return startDate;
		}
		public int getPeriod() {
			return period;
		}
		
		public double getInterestedRate() {
			return interestedRate;
		}

		public double getFinalMoney() {
			return finalMoney;
		}

//		Setter Method
		public void setAccount (Account account) {
			this.account = account;
		}
		
		public void setName (String name) {
			this.name = name;
		}
		
		public void setSubject (Subjects subject) {
			this.subject = subject;
		}
		
		public void setType (ActionType type) {
			this.type = type;
		}
		
		public void setMoney(double money) {
			this.money = money;
		}
		
		public void setStartDate (Date startDate) {
			this.startDate = startDate;
		}
		
		public void setPeriod (int period) {
			this.period = period;
		}

		public void setInterestedRate(double interestedRate) {
			this.interestedRate = interestedRate;
		}

		public void setFinalMoney(double finalMoney) {
			this.finalMoney = finalMoney;
		}

		@Override
		public String toString() {
			return "TackleAndLoan [id=" + id + ", name=" + name + ", subject=" + subject + ", type=" + type + ", money="
					+ money + ", startDate=" + startDate + ", period=" + period + ", interestedRate=" + interestedRate
					+ ", finalMoney=" + finalMoney + "]";
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
			BorrowAndLend other = (BorrowAndLend) obj;
			if (id != other.id)
				return false;
			return true;
		}

		private static int nextId (Integer currId) {
			if (currId == null) {
				idCounter++;
				return idCounter;
			} else {
				int num;
				num = currId.intValue();
				
				if (num > idCounter) {
					idCounter = num;
				}
				return currId;
			}
		}
		
		private double computeFinalMoney(Double money, Double interestedRate, Integer period) {
			double currMoney = money + (money * (interestedRate / 100 / (double)period));
			return currMoney;
		}
		
		@DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
		public static void updateAutoGeneratedValue(DAttr attrib, Tuple derivingValue, Object minVal, Object maxVal)
				throws ConstraintViolationException {
			if (minVal != null && maxVal != null) {
				// check the right attribute
				if (attrib.name().equals("id")) {
					int maxIdVal = (Integer) maxVal;
					if (maxIdVal > idCounter)
						idCounter = maxIdVal;
				}
			}
		}
		
}
