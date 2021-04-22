package vn.com.personalfinance.services.account;

import java.util.ArrayList;
import java.util.Collection;

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

@DClass(schema="personalfinancemanagement")
public class TotalBalance {
	public static final String A_totalBalance = "totalBalance";
	public static final String A_accounts = "accounts";
	
	@DAttr(name = A_totalBalance, type = Type.Double, auto = true, length = 15, mutable = false, optional = true)
	private Double totalBalance;
	
	@DAttr(name = A_accounts, type = Type.Collection, optional = false,
	serialisable = false, filter = @Select(clazz = Account.class))
	@DAssoc(ascName = "totalBalance-has-account", role = "totalBalance",
	ascType = AssocType.One2Many, endType = AssocEndType.One, 
	associate = @Associate(type = Account.class, cardMin = 0, cardMax = MetaConstants.CARD_MORE ))
	private Collection<Account> accounts;
	private int accountsCount;
	
	@DOpt(type=DOpt.Type.DataSourceConstructor)
	public TotalBalance(Double totalBalance) {    
	    // assign other values
	    this.totalBalance = totalBalance;
	    
	    accounts = new ArrayList<>();
	    accountsCount = 0;
	    
	    computeTotalBalance();
	}
	
	@DOpt(type = DOpt.Type.LinkAdder)
	// only need to do this for reflexive association: @MemberRef(name="accounts")
	public boolean addDailyExpense(Account a) {
		if (!this.accounts.contains(a)) {
			accounts.add(a);
		}
		// no other attributes changed
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewDailyExpense(Account a) {
		accounts.add(a);
		accountsCount++;
		
		computeTotalBalance();
		// no other attributes changed
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addDailyExpense(Collection<Account> account) {
		for (Account a : account) {
			if (!this.accounts.contains(a)) {
				this.accounts.add(a);
			}
		}
		// no other attributes changed
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewDailyExpense(Collection<Account> accounts) {
		this.accounts.addAll(accounts);
		accountsCount += accounts.size();
		
		computeTotalBalance();
		// no other attributes changed
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkRemover)
	// only need to do this for reflexive association: @MemberRef(name="accounts")
	public boolean removeDailyExpense(Account a) {
		boolean removed = accounts.remove(a);

		if (removed) {
			accountsCount--;	
			computeTotalBalance();
		}
		// no other attributes changed
		return false;
	}
	
	//GETTER SETTER
	public Collection<Account> getAccounts() {
		return accounts;
	}
	
	@DOpt(type=DOpt.Type.LinkCountGetter)
	public int getAccountsCount() {
		return accountsCount;
	}
	
	public void setAccounts(Collection<Account> account) {
		this.accounts = account;
		accountsCount = account.size();
	}
	
	@DOpt(type=DOpt.Type.LinkCountSetter)
	public void setAccountsCount(int accountsCount) {
		this.accountsCount = accountsCount;
	}
	
	private void computeTotalBalance() {
		for(Account a : accounts) {
			totalBalance += a.getBalance();
		}
	}
}
