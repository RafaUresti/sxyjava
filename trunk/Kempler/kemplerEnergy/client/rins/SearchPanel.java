package com.kemplerEnergy.client.rins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.VerticalSplitPanel;
import com.google.gwt.user.client.ui.Widget;
import com.kemplerEnergy.client.util.UIs;
import com.kemplerEnergy.model.rins.Invoice;
import com.kemplerEnergy.model.rins.RINInvoiceSearch;

//TODO FJ finish getInvoiceList function, currently searching only by CounterParty and invoice number
//return all versions. Fix the test code

/**
 * Provides search function for the entire database
 */
public class SearchPanel extends Composite {
	final RINsRPCAsync svc = RINsRPC.Util.getInstance();

	private VerticalPanel vPanel = new VerticalPanel();
	
	private RINInvoiceSearch search = new RINInvoiceSearch();

	private ArrayList<Invoice> invoices;
	private ResultsPanel resultsPanel = new ResultsPanel();;

	// Sort order for 0: P/S, 1: invoice #, 2: CounterParty Name, 3: Date
	// True is ascending, false is descending

	private final ListBox buySell = new ListBox();
	private final TextBox invoiceNumberBox = new TextBox();
	private final String INVOICE_NUMBER = "Invoice #";
	private final EPAPartnerSuggestionPanel epaPartnerSuggestionPanel = new EPAPartnerSuggestionPanel();
	private final SuggestBox epaPartnerNameBox = epaPartnerSuggestionPanel
			.getEpaPartnerNameBox();
	private final String EPA_PARTNER = "EPA Partner";
	private Button getEPAButton = epaPartnerSuggestionPanel.getGetEPAButton();
	private final TextBox fromDateBox = new TextBox();
	private final String FROM_DATE = "From Date";
	private final TextBox toDateBox = new TextBox();
	private final String TO_DATE = "To Date";

	private boolean[] sortOrders = new boolean[] { false, false, false, false };

	final int[] RESULT_WIDTHS = new int[] { 30, 200, 300, 150, 83, 83, 83 };

	// final int TOTAL_WIDTH = sumOf(RESULT_WIDTHS);
	final String PS_WIDTH = RESULT_WIDTHS[0] + "";
	final String INVOICE_WIDTH = RESULT_WIDTHS[1] + "";
	final String COUNTER_PARTY_WIDTH = RESULT_WIDTHS[2] + "";
	final String DATE_WIDTH = RESULT_WIDTHS[3] + "";
	final String RPTD_WIDTH = RESULT_WIDTHS[4] + "";
	final String CSV_WIDTH = RESULT_WIDTHS[5] + "";
	final String INVOICE_LINKWIDTH = RESULT_WIDTHS[6] + "";
	// final String TITLE_WIDTH = TOTAL_WIDTH +"";
	final static String URL = "http://java:8080/RINs/DocRetrieve?filePath=";

	public SearchPanel() {
		this.initWidget(vPanel);
		vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		vPanel.setSize("935","550");
		assembleInputPanel();
		assembleSearchClearPanel();
		assembleResultPanel();
	}

	private void assembleInputPanel() {
		final HorizontalPanel inputPanel = new HorizontalPanel();
		vPanel.add(inputPanel);

		/*
		 * Drop down box to choose invoice type: PURCHASE/SALE/BOTH and record
		 * into RINInvoiceSearch object
		 */

		buySell.addItem("PURCHASE");
		buySell.addItem("SALE");
		buySell.addItem("BOTH");
		buySell.setSelectedIndex(0);
		search.setInvoiceType("PURCHASE");
		inputPanel.add(buySell);
		buySell.addChangeListener(new ChangeListener() {
			public void onChange(Widget sender) {
				int buySellIndex;
				buySellIndex = buySell.getSelectedIndex();
				search.setInvoiceType(buySell.getItemText(buySellIndex));
			}
		});

		/*
		 * Invoice number text box, with background text indicating function,
		 * which disappears upon click and change. Change stored in
		 * RINInvoiceSearch object
		 */

		invoiceNumberBox.setText(INVOICE_NUMBER);
		inputPanel.add(invoiceNumberBox);

		UIs.addDefaultTextFocusListener(invoiceNumberBox, INVOICE_NUMBER);
		invoiceNumberBox.addChangeListener(new ChangeListener() {
			public void onChange(Widget arg0) {
				String entry = invoiceNumberBox.getText().trim();
				if (!entry.isEmpty() && !entry.equalsIgnoreCase(INVOICE_NUMBER))
					search.setInvoiceNumber(entry);
				else
					invoiceNumberBox.setText(INVOICE_NUMBER);
			}
		});

		/*
		 * EPA partner box. Sets EPA partner to RINInvoiceSearch object at
		 * change
		 */

		epaPartnerSuggestionPanel.getEpaPartnerPanel().remove(
				epaPartnerSuggestionPanel.getEpaPartnerNameLabel());
		epaPartnerNameBox.setText(EPA_PARTNER);
		inputPanel.add(epaPartnerSuggestionPanel);
		epaPartnerNameBox.addFocusListener(new FocusListener() {
			public void onFocus(Widget arg0) {
				String entry = epaPartnerNameBox.getText().trim();
				if (entry.equalsIgnoreCase(EPA_PARTNER))
					epaPartnerNameBox.setText("");
			}

			public void onLostFocus(Widget arg0) {
				String entry = epaPartnerNameBox.getText().trim();
				if (entry.isEmpty())
					epaPartnerNameBox.setText(EPA_PARTNER);
			}
		});

		/*
		 * From date box and calendar. Sets from date to RINInvoiceSearch object
		 * at change
		 */

		final CalendarWidget fromDateCalendar = new CalendarWidget();
		final PopupPanel fromDateCalendarPanel = new PopupPanel(true);

		fromDateBox.setText(FROM_DATE);
		inputPanel.add(fromDateBox);
		fromDateCalendarPanel.add(fromDateCalendar);
		fromDateCalendarPanel.center();
		fromDateCalendarPanel.hide();
		UIs.addDefaultTextFocusListener(fromDateBox, FROM_DATE);

		fromDateBox.addFocusListener(new FocusListener() {
			public void onFocus(Widget arg0) {
				fromDateCalendarPanel.show();
			}

			public void onLostFocus(Widget arg0) {
			}
		});
		fromDateBox.addChangeListener(new ChangeListener() {
			public void onChange(Widget arg0) {
				String entry = fromDateBox.getText().trim();
				if (!entry.equalsIgnoreCase(FROM_DATE)) {
					search.setFromDate(entry);
				} else
					search.setFromDate("");
			}
		});

		/*
		 * Sets the changed From Date entry if different from DefaultString.
		 */
		fromDateCalendar.addChangeListener(new ChangeListener() {
			public void onChange(Widget arg0) {
				int month = fromDateCalendar.getMonth() + 1;
				String fromDate = fromDateCalendar.getYear() + "-" + month
						+ "-" + fromDateCalendar.getDay();
				fromDateBox.setText(fromDate);
				search.setFromDate(fromDate);
				fromDateCalendarPanel.hide();
			}
		});

		/*
		 * To date box and calendar. Sets from date to RINInvoiceSearch object
		 * at change
		 */

		final CalendarWidget toDateCalendar = new CalendarWidget();
		final PopupPanel toDateCalendarPanel = new PopupPanel(true);

		toDateBox.setText(TO_DATE);
		inputPanel.add(toDateBox);
		toDateCalendarPanel.add(toDateCalendar);
		toDateCalendarPanel.center();
		toDateCalendarPanel.hide();

		UIs.addDefaultTextFocusListener(toDateBox, TO_DATE);
		toDateBox.addFocusListener(new FocusListener() {
			public void onFocus(Widget arg0) {
				toDateCalendarPanel.show();
			}

			public void onLostFocus(Widget arg0) {
			}
		});

		toDateBox.addChangeListener(new ChangeListener() {
			public void onChange(Widget arg0) {
				String entry = toDateBox.getText().trim();
				if (!entry.equalsIgnoreCase(TO_DATE)) {
					search.setToDate(entry);
				} else
					search.setToDate("");
			}
		});

		/*
		 * Sets the changed To Date entry if different from DefaultString.
		 */
		toDateCalendar.addChangeListener(new ChangeListener() {
			public void onChange(Widget arg0) {
				int month = toDateCalendar.getMonth() + 1;
				String toDate = toDateCalendar.getYear() + "-" + month + "-"
						+ toDateCalendar.getDay();
				toDateBox.setText(toDate);
				search.setToDate(toDate);
				toDateCalendarPanel.hide();
			}
		});
	}

	private void assembleSearchClearPanel() {
		final HorizontalPanel searchClearPanel = new HorizontalPanel();
		vPanel.add(UIs.getSPH10());
		vPanel.add(searchClearPanel);

		searchClearPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		searchClearPanel.setSpacing(10);

		final Button searchButton = new Button();
		final Button clearButton = new Button();
		searchClearPanel.add(searchButton);

		searchButton.setWidth("60");
		searchButton.setText("Search");
		searchButton.addClickListener(new ClickListener() {
			public void onClick(Widget arg0) {
				if (!epaPartnerNameBox.getText().equalsIgnoreCase(EPA_PARTNER)
						&& getEPAButton.isEnabled()) {
					Window.alert("Please get EPA #!");
					return;
				} else if (!getEPAButton.isEnabled()) {
					search.setEpaPartner(epaPartnerSuggestionPanel
							.getEPAPartner());
				}
				searchButton.setEnabled(false);
				svc.getInvoiceList(search,
						new AsyncCallback<ArrayList<Invoice>>() {
							public void onFailure(Throwable arg0) {
								Window.alert("Search failure: "
										+ arg0.getMessage());
								searchButton.setEnabled(true);
							}

							public void onSuccess(ArrayList<Invoice> arg0) {
								if (arg0 != null && !arg0.isEmpty()) {
									invoices = arg0;
									resultsPanel.displayInvoices(invoices);
								} else {
									Window.alert("No invoice found!");
									resultsPanel.clear();
								}
								searchButton.setEnabled(true);
							}
						});
				// -- FOR TEST PURPOSE ONLY:----XXX
				/*
				 * svc.getInvoice(search, new AsyncCallback<Invoice>(){ public
				 * void onFailure(Throwable arg0) {
				 * Window.alert("failed to get invoice"); } public void
				 * onSuccess(Invoice arg0) { invoices = new
				 * ArrayList<Invoice>(); invoices.add(arg0);
				 * resultsPanel.displayInvoices(invoices); } });
				 */
				// ---FOR TEST PURPOSE ONLY:----XXX
				/*
				 * Invoice i = new Invoice(); i.setDefault(); invoices = new
				 * ArrayList<Invoice>(); invoices.add(i); Invoice i1 = new
				 * Invoice(); i1.setDefault(); i1.setInvoiceType("SALE");
				 * i1.setInvoiceNo("Fake invoice");
				 * i1.getEpaPartner().setFullName(
				 * "Dammy counterParty how are tyou djdjahd jjdfa fdlfjal;k flasdja sdfjk"
				 * ); i1.setInvoiceDate("2000-1-1"); invoices.add(i1);
				 * 
				 * Invoice i2 = new Invoice(); i2.setDefault();
				 * i2.setInvoiceType("SALE"); i2.setInvoiceNo("Drake invoice");
				 * i2.getEpaPartner().setFullName("Tummy counterParty");
				 * i2.setInvoiceDate("2008-1-1"); invoices.add(i2);
				 * 
				 * Invoice i3 = new Invoice(); i3.setDefault();
				 * i3.setInvoiceType("PURCHASE");
				 * i3.setInvoiceNo("Lake invoice");
				 * i2.getEpaPartner().setFullName("Cummy counterParty");
				 * i3.setInvoiceDate("2008-2-1"); invoices.add(i3);
				 * 
				 * for (int j = 0; j < 100; j++){ Invoice x = new Invoice();
				 * x.setDefault(); x.setInvoiceNo("Lake invoice"+j);
				 * i2.getEpaPartner().setFullName("Cummy counterParty"+j);
				 * x.setInvoiceDate("2008-2-"+j); invoices.add(x); }
				 * resultsPanel.displayInvoices(invoices);
				 */
			}
		});

		searchClearPanel.add(UIs.getSPW30());
		searchClearPanel.add(clearButton);
		clearButton.setWidth("60");
		clearButton.setText("Clear");
		clearButton.addClickListener(new ClickListener() {
			public void onClick(Widget arg0) {
				// Resets all the fields and the search object
				buySell.setSelectedIndex(0);
				search.setInvoiceType("PURCHASE");

				invoiceNumberBox.setText(INVOICE_NUMBER);
				search.setInvoiceNumber("");

				epaPartnerNameBox.setText(EPA_PARTNER);
				epaPartnerSuggestionPanel.setEPAPartner(null);
				search.setEpaPartner(null);

				fromDateBox.setText(FROM_DATE);
				search.setFromDate("");

				toDateBox.setText(TO_DATE);
				search.setToDate("");

				resultsPanel.clear();
			}
		});
	}

	private void assembleResultPanel() {
		final ScrollPanel scrollPanel = new ScrollPanel();

		final HorizontalPanel searchTitlePanel = new HorizontalPanel();
		searchTitlePanel.setSize("935","0");
		vPanel.add(searchTitlePanel);
		searchTitlePanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		//searchTitlePanel.setWidth("100%");
		//vPanel.setCellWidth(searchTitlePanel, "100%");
		// searchTitlePanel.setWidth(TITLE_WIDTH);

		final Button psButton = new Button();
		psButton.setText("P/S");
		psButton.setWidth(PS_WIDTH);
		searchTitlePanel.add(psButton);
		psButton.addClickListener(new ClickListener() {
			public void onClick(final Widget arg0) {
				if (invoices != null && !invoices.isEmpty()) {
					if (sortOrders[0]) {
						sortOrders[0] = false;
						Collections.sort(invoices, new Comparator<Invoice>() {
							public int compare(Invoice i1, Invoice i2) {
								return i2.getInvoiceType().compareTo(
										i1.getInvoiceType());
							}
						});
						resultsPanel.displayInvoices(invoices);
					} else {
						sortOrders[0] = true;
						Collections.sort(invoices, new Comparator<Invoice>() {
							public int compare(Invoice i1, Invoice i2) {
								return i1.getInvoiceType().compareTo(
										i2.getInvoiceType());
							}
						});
						resultsPanel.displayInvoices(invoices);
					}
				}
			}
		});

		final Button invoiceButton = new Button();
		searchTitlePanel.add(invoiceButton);
		invoiceButton.setWidth(INVOICE_WIDTH);
		invoiceButton.setText("Invoice #");
		invoiceButton.addClickListener(new ClickListener() {
			public void onClick(final Widget arg0) {
				if (invoices != null && !invoices.isEmpty()) {
					if (sortOrders[1]) {
						sortOrders[1] = false;
						Collections.sort(invoices, new Comparator<Invoice>() {
							public int compare(Invoice i1, Invoice i2) {
								return i2.getInvoiceNo().compareTo(
										i1.getInvoiceNo());
							}
						});
						resultsPanel.displayInvoices(invoices);
					} else {
						sortOrders[1] = true;
						Collections.sort(invoices, new Comparator<Invoice>() {
							public int compare(Invoice i1, Invoice i2) {
								return i1.getInvoiceNo().compareTo(
										i2.getInvoiceNo());
							}
						});
						resultsPanel.displayInvoices(invoices);
					}
				}
			}
		});

		final Button counterPartyButton = new Button();
		searchTitlePanel.add(counterPartyButton);
		counterPartyButton.setWidth(COUNTER_PARTY_WIDTH);
		counterPartyButton.setText("Counter Party");
		counterPartyButton.addClickListener(new ClickListener() {
			public void onClick(final Widget arg0) {
				if (invoices != null && !invoices.isEmpty()) {
					if (sortOrders[2]) {
						sortOrders[2] = false;
						Collections.sort(invoices, new Comparator<Invoice>() {
							public int compare(Invoice i1, Invoice i2) {
								return i2.getEpaPartner().getFullName()
										.compareTo(
												i1.getEpaPartner()
														.getFullName());
							}
						});
						resultsPanel.displayInvoices(invoices);
					} else {
						sortOrders[2] = true;
						Collections.sort(invoices, new Comparator<Invoice>() {
							public int compare(Invoice i1, Invoice i2) {
								return i1.getEpaPartner().getFullName()
										.compareTo(
												i2.getEpaPartner()
														.getFullName());
							}
						});
						resultsPanel.displayInvoices(invoices);
					}
				}
			}
		});

		final Button dateButton = new Button();
		searchTitlePanel.add(dateButton);
		dateButton.setWidth(DATE_WIDTH);
		dateButton.setText("Date");
		dateButton.addClickListener(new ClickListener() {
			public void onClick(final Widget arg0) {
				if (invoices != null && !invoices.isEmpty()) {
					if (sortOrders[3]) {
						sortOrders[3] = false;
						Collections.sort(invoices, new Comparator<Invoice>() {
							public int compare(Invoice i1, Invoice i2) {
								return i2.getInvoiceDate().compareTo(
										i1.getInvoiceDate());
							}
						});
						resultsPanel.displayInvoices(invoices);
					} else {
						sortOrders[3] = true;
						Collections.sort(invoices, new Comparator<Invoice>() {
							public int compare(Invoice i1, Invoice i2) {
								return i1.getInvoiceDate().compareTo(
										i2.getInvoiceDate());
							}
						});
						resultsPanel.displayInvoices(invoices);
					}
				}
			}
		});

		final Label rptdLabel = new Label("RPTD");
		rptdLabel.setWidth(RPTD_WIDTH);
		rptdLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		searchTitlePanel.add(rptdLabel);

		final Label csvLabel = new Label("CSV");
		csvLabel.setWidth(CSV_WIDTH);
		csvLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		searchTitlePanel.add(csvLabel);

		final Label invoiceLabel = new Label("INVOICE");
		invoiceLabel.setWidth(INVOICE_LINKWIDTH);
		invoiceLabel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		searchTitlePanel.add(invoiceLabel);

		vPanel.add(scrollPanel);
		scrollPanel.setSize("100%", "500");
		//vPanel.setCellWidth(scrollPanel, "100%");

		// resultsPanel.setSize(TITLE_WIDTH, RESULTS_HEIGHT);
		resultsPanel.setBorderWidth(1);
		scrollPanel.add(resultsPanel);
		resultsPanel.setWidth("100%");
	}

	/**
	 * The class for constructing the search result panel
	 * 
	 * @author Xiaoyi Sheng
	 * 
	 */
	private class ResultsPanel extends VerticalPanel {
		/**
		 * The list of invoices to display
		 * 
		 * @param invoices
		 */
		void displayInvoices(ArrayList<Invoice> invoices) {
			this.clear();
			int number = 0;
			if (invoices != null && !invoices.isEmpty()) {
				for (int i = 0; i < invoices.size(); i++) {
					this.add(new SearchResult(invoices.get(i)));
					number++;
				}
				this.setHeight(SearchResult.HEIGHT * number + "");
			}
		}
	}

	private class SearchResult extends HorizontalPanel {

		final static int HEIGHT = 20;

		// TODO set these parameters
		SearchResult(Invoice invoice) {
			final Label psLabel = new Label(invoice.getInvoiceType()
					.equalsIgnoreCase("PURCHASE") ? "P" : "S");
			psLabel.setWidth(PS_WIDTH);
			final Label invoiceLabel = new Label(invoice.getInvoiceNo() + "_v"
					+ invoice.getVersionNo());
			invoiceLabel.setWidth(INVOICE_WIDTH);
			final Label counterPartyLabel = new Label(invoice.getEpaPartner()
					.getFullName());
			counterPartyLabel.setWidth(COUNTER_PARTY_WIDTH);
			final Label dateLabel = new Label(invoice.getInvoiceDate());
			dateLabel.setWidth(DATE_WIDTH);
			final HTML rptdLink = new HTML();

			final String rptdURL = "<a href=\"" + URL + invoice.getRptdPath()
					+ "\" target=\"_blank\">RPTD</a>";
			rptdLink.setHTML(rptdURL);
			rptdLink.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			
			rptdLink.setWidth(RPTD_WIDTH);
			final HTML csvLink = new HTML();
			csvLink.setWidth(CSV_WIDTH);

			final String csvURL = "<a href=\"" + URL + invoice.getCsvPath()
					+ "\" target=\"_blank\">CSV</a>";
			csvLink.setHTML(csvURL);
			csvLink.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			
			
			final HTML invoiceLink = new HTML();
			invoiceLink.setWidth(INVOICE_LINKWIDTH);

			final String invoiceURL = "<a href=\"" + URL
					+ invoice.getInvoicePath()
					+ "\" target=\"_blank\">INVOICE</a>";
			invoiceLink.setHTML(invoiceURL);
			invoiceLink.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

			this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			this.add(psLabel);
			this.add(invoiceLabel);
			this.add(counterPartyLabel);
			this.add(dateLabel);
			this.add(rptdLink);
			this.add(csvLink);
			if (invoice.getInvoiceType().equals("PURCHASE")) {
				this.add(invoiceLink);
			}
			this.setHeight(HEIGHT + "px");
		}
	}

	/**
	 * Calculates the sum of an int array
	 * 
	 * @param value
	 *            the int array
	 * @return sum
	 */
	public static int sumOf(int[] value) {
		int total = 0;
		for (int i = 0; i < value.length; i++) {
			total += value[i];
		}
		return total;
	}
}
