
package edu.upenn.cis505;

/////////////////////////////////////////////////////////////////////////////
//Copyright (c) 2009 The University of Pennylvania
//Permission to use, copy, modify, and distribute this software and
//its documentation for any purpose, without fee, and without a
//written agreement is hereby granted, provided that the above copyright 
//notice and this paragraph and the following two paragraphs appear in
//all copies. 
//
//IN NO EVENT SHALL THE UNIVERSITY OF PENNSYLVANIA BE LIABLE TO ANY PARTY FOR
//DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING
//LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION,
//EVEN IF THE UNIVERSITY OF PENNSYLVANIA HAS BEEN ADVISED OF THE POSSIBILITY OF
//SUCH DAMAGE. 
//
//THE UNIVERSITY OF PENNSYLVANIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
//INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
//AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE PROVIDED HEREUNDER IS ON
//AN "AS IS" BASIS, AND THE UNIVERSITY OF PENNSYLVANIA HAS NO OBLIGATIONS TO
//PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. 
//////////////////////////////////////////////////////////////////////////////
//
//Author: Rafi Rubin
//
//////////////////////////////////////////////////////////////////////////////

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class ArgParse extends HashMap<String, Object> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8406396484434675365L;

	public List<String> rest = new ArrayList<String>();

	public Map<String, String> help = null;

	public Map<String, Object> arg_map = null;

	public void print() {
		System.out.println("Printing args:");
		for (Map.Entry e : entrySet()) {
			System.out.println("\t" + e.getKey() + " -> " + e.getValue());
		}

		for (String s : rest) {
			System.out.println("\t\t" + s);
		}
	}

	public void help() {
		System.out.println("Printing args:");
		Map<String,Set<String>> flipped_args=new TreeMap<String,Set<String>>();
		for (Map.Entry<String,Object> e : arg_map.entrySet()) {
			if(e.getValue() instanceof Integer)
			{
				if(!flipped_args.containsKey(e.getKey()))
					flipped_args.put(e.getKey(),new TreeSet<String>());
				flipped_args.get(e.getKey()).add(e.getKey());
			}
			else
			{
				String key=e.getValue().toString();
				if(!flipped_args.containsKey(key))
					flipped_args.put(key,new TreeSet<String>());
				flipped_args.get(key).add(e.getKey());	
			}
		}
		for (String flag:flipped_args.keySet()) {
			for(String af: flipped_args.get(flag))
				System.out.print("\n" +af);
			if (help != null)
				if (help.containsKey(flag))
					System.out.println("\t"+help.get(flag));
				else
					System.out.println();
			else
				System.out.println();
		}

		for (String s : rest) {
			System.out.println("\t\t" + s);
		}
	}

	public ArgParse(Map<String, Object> arg_map, String[] args,
			Set<String> mandatory, Map<String, String> help) {
		this.help = help;
		this.arg_map = arg_map;

		for (int i = 0; i < args.length; i++) {
			if (arg_map.containsKey(args[i])) {
				String key = args[i];

				while (arg_map.containsKey(arg_map.get(key))
						&& (arg_map.get(key) instanceof String))
					key = arg_map.get(key).toString();

				int count = ((Integer) arg_map.get(key)).intValue();

				switch (count) {
				case 0:
					put(key, true);
					break;
				case 1:
					i++;
					put(key, args[i]);
					break;

				default:
					if(count<0)
					{
						System.err.println("don't support negative numbers of arguments");
						System.exit(1);
					}
					else
					{
						String [] values=new String[count];
						for(int j=0;j<count;j++)
						{
							i++;
							values[j]=args[i];
						}
						put(key,values);
					}
				}
			} else {
				rest.add(args[i]);
			}
		}

		if (mandatory != null) {
			for (String key : mandatory) {
				if (!containsKey(key)) {
					help();

					System.err.println("Missing argument: " + key);
					System.exit(1);
				}
			}
		}
	}

	public ArgParse(Map<String, Object> arg_map, String[] args) {
		this(arg_map, args, null, null);
	}
}

