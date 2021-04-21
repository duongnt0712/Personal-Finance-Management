package vn.com.personalfinance.services.actions;

import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;

public enum ActionType {
	Borrow_money,
	Lend_money,
	Collect_debts,
 	Repay_money;
		
	@DAttr(name = "name", type = Type.String, id = true, length = 30)
	public String getName() {
		return name();
	}	
}
