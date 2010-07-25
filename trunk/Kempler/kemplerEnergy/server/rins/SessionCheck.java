package com.kemplerEnergy.server.rins;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionCheck {
	public static boolean check(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if(session == null){
//			System.out.println("In SessionCheck: is NUll");
			return false;
		}
//		System.out.println("in SessionCheck : " + session.getId());
//		System.out.println(!session.isNew());
		return (!session.isNew());
	}
}
