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
import domainapp.basics.util.Tuple;
import vn.com.personalfinance.services.account.Account;
import vn.com.personalfinance.services.account.AccountType;
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
