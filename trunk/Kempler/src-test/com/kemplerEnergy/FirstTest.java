package com.kemplerEnergy;

import java.util.*;
import com.kemplerEnergy.model.*;

import org.hibernate.*;
import com.kemplerEnergy.persistence.*;

import org.apache.log4j.Logger;

public class FirstTest {

	/**
	 * Log4j logger
	 */
	static Logger log4j = Logger.getLogger("com.kemplerEnergy");
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		char[] a = {'1','1'};
		char[] b;
		char [] c = {'0','0'};
		b = a;
		System.out.println(a);
		b = c;
		System.out.println(b);
		System.out.println(c);
	}
}
