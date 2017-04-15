package com.yujun.core;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.yujun.domain.Account;

@Service
public class AccountService {
	private List<Account> list;
	@PostConstruct
	public void initAccount() {
		list = new ArrayList<Account>();
		
		Account account = new Account();
		account.setId("62124349");
		account.setPassword("122541");
		account.setGddm("A238440910,0129170967");
		list.add(account);
		
		account = new Account();
		account.setId("62278700");
		account.setPassword("324877");
		account.setGddm("A317676125,0187394552");
		list.add(account);
		
	}

	public Account getAccountByPassWord(String passWord) {
		for(Account account : list) {
			if (account.getPassword().equals(passWord)) {
				return account;
			}
		}
		return null;
	}
	
	public Account getAccountByUserId(String userId) {
		for(Account account : list) {
			if (account.getId().equals(userId)) {
				return account;
			}
		}
		return null;
	}
	
	public List<Account> getAllAccount() {
		return list;
	}
	
}
