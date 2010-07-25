package com.kemplerEnergy.model.rins;

import java.util.ArrayList;
import com.kemplerEnergy.exception.RINException;
import com.kemplerEnergy.model.rins.Invoice.RinType;

/**
 * Contains all the static methods involving Invoice objcets
 * @author Xiaoyi Sheng
 *
 */
public class Invoices {
	/**
	 * Generates the rejection reasons for an invalid buy invoice.
	 * Reasons include incorrect RIN gallons (actual amount doesn't 
	 * match with expected amount or gap doesn't match any allowed gap).
	 * If the invoice being replaced for retired RINs, sets the invoice 
	 * status to "REPLACEMENT_REQUESTED"
	 * @param invoice The invoice to generate reasons
	 * @return The reasons for rejection in HTML to send to vendor
	 */
	public static String generateRejectionReasons(Invoice invoice) throws RINException{
		int newRINAmount = invoice.calculateGallons("NEW");
		ArrayList<RIN> overLapRINs =  RINs.checkOverlap(invoice);
		//If a replacement invoice is rejected by Accountant, it should not be deleted
		if (invoice.getStatus().equalsIgnoreCase("REJECTED") && invoice.getVersionNo() > 1){
			invoice.setStatus("REPLACEMENT_REQUESTED");
			
			//Gets rid of the newly entered RINs
			ArrayList<RIN> newRINList = invoice.retrieveRINs(RinType.Both, "NEW");
			invoice.getRins().removeAll(newRINList);
		}

		String reasons = "";
		if (invoice.getGaps() == null || invoice.getGaps().isEmpty()){
			if (!matchGallonNumbers(invoice)){
				reasons += "The RIN gallon amount doesn't match invoice. The invoice gallon amount is: " +
				invoice.getExpectedGallons() + ". However, the actual gallon amount is: " + 
				invoice.calculateActualGallons() + ".</br></br>";
			}
		}else if (!matchAllowedGaps(invoice)){//Several retirements have been made to the same invoice
			
			//summary of all the request gallons except the first one (not recorded in invoice)
			int sumOfGaps = 0;
			
			for (int gap: invoice.getGaps()){
				sumOfGaps += gap;
			}

			int acceptedAmount = invoice.calculateActualGallons() - invoice.calculateGallons("NEW");
			
			//As the first request amount isn't recorded in the invoice, it needs to be computed
			int firstRequest = invoice.getExpectedGallons()- acceptedAmount- sumOfGaps;
			
			if (firstRequest < 1){
				throw new RINException("Error in requests calculation: "+firstRequest);
			}

			String requests = firstRequest + "";
			for (int i = 0; i < invoice.getGaps().size(); i ++){
				if (i == invoice.getGaps().size() - 1)
					requests += " and " + invoice.getGaps().get(i) ;
				else requests += ", " + invoice.getGaps().get(i);
			}
			requests += " RIN gallons, ";
			reasons += " The replacement RIN gallon amount doesn't match our requests. "
				+ "We have requested replacement(s) of "
				+ requests
				+ ". And the replacement of "
				+ newRINAmount + " doesn't match.</br>";
		}
		if (overLapRINs != null && !overLapRINs.isEmpty()){
			reasons += "The following RINs in the PTD overlap:</br>";
			for (RIN rin: overLapRINs){
				reasons += rin.toString()+"</br>";
			}
		}
		return reasons;
	}


	/**
	 * Check if the expected gallons matches the actual gallons
	 * Alert if an RIN entry isn't updated.
	 * @return
	 */
	public static boolean matchGallonNumbers(Invoice invoice) {		
		int actualGallons = invoice.calculateActualGallons();
		if (actualGallons != invoice.getExpectedGallons()){
			System.out.println("Actual: "+actualGallons + " expected: "+invoice.getExpectedGallons());
			return false;
		}
		else return true;
	}
	
	public static boolean matchAllowedGaps(Invoice invoice){
		if (Invoices.matchGallonNumbers(invoice)){
			return true;
		}
		//Check if the difference between expected and actual gallons is allowed
		ArrayList<Integer> allowedGaps;
		ArrayList<Integer> invoiceGaps = invoice.getGaps();
		if (invoiceGaps != null && !invoiceGaps.isEmpty()){
			allowedGaps = Invoices.calculateAllowedGaps(invoiceGaps);
			for (int i = 0; i < allowedGaps.size(); i ++){
				if (invoice.getExpectedGallons() - invoice.calculateActualGallons() == allowedGaps.get(i)){
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * Calculates allowed invoice gaps when saving from invoice gaps.
	 * Allowed gaps are (last invoice gap), (last invoice gap + second last invoice gap),
	 * (last invoice gap + second last invoice gap + third invoice gap), etc.
	 * For example, an invoice gap list is (200, 300, 400). The allowed gaps are
	 * (400, 400+300, 400+300+200)
	 * @param invoiceGaps
	 * @return list of allowed gaps
	 */
	public static ArrayList<Integer> calculateAllowedGaps(ArrayList<Integer> invoiceGaps){
		ArrayList<Integer> allowedGaps = new ArrayList<Integer>();
		int gap = 0;
		for (int i = invoiceGaps.size() - 1; i >= 0; i --){
			gap += invoiceGaps.get(i);
			allowedGaps.add(gap);
		}
		return allowedGaps;
	}
}
