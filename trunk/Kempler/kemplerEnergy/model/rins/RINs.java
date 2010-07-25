package com.kemplerEnergy.model.rins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.kemplerEnergy.exception.RINException;

public class RINs {
	
	public static char[] add(char[] startGallon, int i) throws RINException {
		return toCharArray(toInt(startGallon) + i);
	}
	
	/**
	 * Used by UI to check overlapping within that invoice except for the SOLD
	 * RINs, it will not re-arrange the order of rins in the given invoice
	 * 
	 * @param invoice
	 * @return List of overlapped rins
	 */
	public static ArrayList<RIN> checkOverlap(Invoice invoice) {
		ArrayList<RIN> rinsToCheck = new ArrayList<RIN>();
		for (RIN rin: invoice.getRins()){
			if (!rin.getRinStatus().equalsIgnoreCase("SOLD")){
				rinsToCheck.add(rin);
			}
		}
		ArrayList<ArrayList<RIN>> rinsGroup = RINs.groupRINs(rinsToCheck, false);
		ArrayList<RIN> overlapRINs = new ArrayList<RIN>();
		for (ArrayList<RIN> rinsList : rinsGroup) {
			overlapRINs.addAll(checkOverlap(rinsList));
		}
		return overlapRINs;
	}

	/**
	 * Return the overlapped RINs within the given set of RINs
	 * Algo: the coverage are sorted by their starting point. The end range
	 * will be extended to the furtherest point. The start range will be
	 * current starting point. If any next range is covered by existing 
	 * coverage range. Overlap is detected.
	 * It will re-order the RINs in the collection!
	 * @param set of RINs which have identical numbers except for the starting
	 * 		  and ending gallons.
	 * @return overlapped RINs
	 */

	public static Set<RIN> checkOverlap(List<RIN> rins) {
		sortByGallon(rins);
		Set<RIN> overlapRINs = new HashSet<RIN>();
		int endRange = toInt(rins.get(0).getEndGallon());
		int currIdx = 0;
		
		for (int i = 1; i < rins.size(); i++) {
			// Overlapping for the next
			if (toInt(rins.get(i).getStartGallon()) <= endRange) {
				if (!rins.get(i).getRinStatus().equalsIgnoreCase("RETIRED"))
					overlapRINs.add(rins.get(i));
				if (!rins.get(currIdx).getRinStatus().equalsIgnoreCase("RETIRED"))
					overlapRINs.add(rins.get(currIdx));
			}
			// change the cover
			if (toInt(rins.get(i).getEndGallon()) > endRange) {
				endRange = toInt(rins.get(i).getEndGallon());
				currIdx = i;
			}
		}
		return overlapRINs;
	}
	
	/**
	 * Check RINs realationship within the given set of RINs
	 * mark their parent/child relationship
	 * @param set of RINs which have identical numbers except for the starting
	 * 		  and ending gallons.
	 * @return overlapped RINs
	 */

	public static void checkSplit(List<RIN> rins) {
		sortByGallon(rins);
		
		for (int i = 0; i < rins.size(); i++) {
			for (int j = 0; j < rins.size(); j++) {
				if (i == j)
					continue;
				if (rins.get(j).getRinStatus().equalsIgnoreCase("SOLD"))
					continue;
				if (toInt(rins.get(i).getStartGallon()) >= 
						toInt(rins.get(j).getStartGallon()) && 
						toInt(rins.get(i).getEndGallon()) <=
						toInt(rins.get(j).getEndGallon())) {
					rins.get(i).setOriginalRIN(rins.get(j));
					rins.get(j).setRinStatus("SPLIT");
				}
			}
		}
	}
	
	/**
	 * Given a set of rins, return the concatenated one
	 * 
	 * @param rins
	 * @return
	 * @throws RINException
	 */
	public static RIN concatenate(List<RIN> rins) throws RINException {
		sortByGallon(rins);

		int i;
		for (i = 1; i < rins.size(); i++) {
			if (toInt(rins.get(i).getStartGallon())
					- toInt(rins.get(i - 1).getEndGallon()) != 1)
				throw new RINException(
						"Fail to concatenate: RINs are not sequencial.");
		}

		RIN newRIN = new RIN(rins.get(0));
		newRIN.setEndGallon(rins.get(i - 1).getEndGallon());
		
		return newRIN;
	}
	
	/**
	 * Concatenate all possible rins of a rin list
	 * @param rins
	 * @return
	 * @throws RINException
	 */
	public static ArrayList<RIN> combine (List<RIN> rins) throws RINException {
		if (rins == null || rins.size() == 0)
			return null;
		
		ArrayList<ArrayList<RIN>> groupedRINs = groupRINs(rins, true);
		ArrayList<RIN> mergedRINs = new ArrayList<RIN>();
		List<RIN> toBeMergedRINs = new ArrayList<RIN>();

		for (ArrayList<RIN> group: groupedRINs) 
		{
			if (group.size() <= 1)
			{
				mergedRINs.addAll(group);
				continue;
			}
				
			if (checkOverlap(group).size() > 0)
				throw new RINException("Can't merge: Overlapped RINs!");
			
			int i;
			for (i = 1; i < group.size(); i++) 
			{
				if (toInt(group.get(i).getStartGallon())
						- toInt(group.get(i - 1).getEndGallon()) == 1) 
				{
					if (toBeMergedRINs.size() == 0) 
						toBeMergedRINs.add(group.get(i - 1));
					toBeMergedRINs.add(group.get(i));
				}
				else if (toBeMergedRINs.size() > 0) 
				{
					RIN newRIN = concatenate(toBeMergedRINs);
					newRIN.setOriginalRIN(newRIN);
					mergedRINs.add(newRIN);
					toBeMergedRINs.clear();
				} else {
					mergedRINs.add(group.get(i - 1));
				}
			}
			
			if (toBeMergedRINs.size() > 0) {
				RIN newRIN = concatenate(toBeMergedRINs);
				newRIN.setOriginalRIN(newRIN);
				mergedRINs.add(newRIN);
				toBeMergedRINs.clear();
			}
			else mergedRINs.add(group.get(i - 1));
		}
		
		return mergedRINs;
	}
	
	/**
	 * Group RINs according to their UNI_RIN
	 * @param rins
	 * @param singleAllowed
	 * @return
	 */
    public static ArrayList<ArrayList<RIN>> groupRINs(List<RIN> rins,
			boolean singleAllowed) {
		if (rins == null || rins.size() == 0)
			throw new RINException("Empty rins supplied");
		
		ArrayList<ArrayList<RIN>> grouppedRINs = new ArrayList<ArrayList<RIN>>();
		ArrayList<RIN> rinsList = new ArrayList<RIN>();
		ArrayList<RIN> rinsCopy = new ArrayList<RIN>();
		
		rinsCopy.addAll(rins);
		
		Collections.sort(rinsCopy, new Comparator<RIN>() {
			public int compare(RIN r1, RIN r2) {
				return r1.getUniRIN().compareTo(r2.getUniRIN());
			}
		});

		rinsList.add(rinsCopy.get(0));
		for (int i = 1; i < rinsCopy.size(); i++) {
			if (rinsCopy.get(i).getUniRIN().equalsIgnoreCase(
					rinsCopy.get(i-1).getUniRIN())) {
				rinsList.add(rinsCopy.get(i));
				continue;
			}  
			
			if (!singleAllowed && rinsList.size() == 1) {
				rinsList.clear();
			} else if (!rinsList.isEmpty()) {
				grouppedRINs.add(rinsList);
				rinsList = new ArrayList<RIN>();
			}
			rinsList.add(rinsCopy.get(i));

		}
		if (!rinsList.isEmpty())
			grouppedRINs.add(rinsList);

		return grouppedRINs;
	}
	
	/**
	 * Sort the set of RINs which are only different in gallons (starting
	 * gallons and ending gallons) according to their starting gallons.
	 * 
	 * 
	 * @param set of RINs
	 */
	public static void sortByGallon(List<RIN> rins) {
		if (rins == null || rins.size() == 0)
			throw new RINException("Empty rins supplied");

		String uniRIN = rins.get(0).getUniRIN();
		for (RIN r : rins) {
			if (!r.getUniRIN().equalsIgnoreCase(uniRIN)) {
				throw new RINException(
						"Fail to concatenate: RINs are not consistent\n" +
						r.getUniRIN() + "\n" + uniRIN);
			}
		}

		Collections.sort(rins, new Comparator<RIN>() {
			public int compare(RIN r1, RIN r2) {
				int diff = toInt(r1.getStartGallon()) - toInt(r2.getStartGallon());
				try {
					if (diff != 0) 
						return diff;
					else return toInt(r1.getEndGallon()) - toInt(r2.getEndGallon());
				} catch (RINException e) {
					return 0;
				}
			}
		});
	}

	public static int tensfolder(int i) throws RINException {
		if (i < 0 || i > 8)
			throw new RINException("not supported yet");
		String result = "1";
		for (int j = 0; j < i; j++) {
			result += "0";
		}
		return Integer.valueOf(result);
	}
	// only support 8 digit
	public static char[] toCharArray(int num) {
		int i = 7;
		char[] c = new char[8];
		while (i >= 0) {
			c[i] = (char) ((num % 10) + '0');
			num /= 10;
			i--;
		}
		return c;
	}

	public static int toInt(char[] c) throws RINException {
		int num = 0;
		if (c.length > 8)
			throw new NumberFormatException("char[] too long");
		for (int i = c.length - 1; i >= 0; i--) {
			num += (c[i] - '0') * tensfolder(c.length - i - 1);
		}
		return num;
	}
	
}
