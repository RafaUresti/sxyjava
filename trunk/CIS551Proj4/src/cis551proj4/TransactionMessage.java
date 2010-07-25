package cis551proj4;

public class TransactionMessage implements Message{
		/**
	 * 
	 */
	private static final long serialVersionUID = 7217543974935076889L;
		/**
		 * Request can be: "Deposit", "Withdraw", "Balance" from ATM side. 
		 * Reply message from Bank side.
		 */
		String request;
		double amount;
		String accountNumber;
		String atmNumber;
		long time;
		public TransactionMessage (String request, double amount, String accountNumber, String atmNumber, long time){
			this.request = request;
			this.amount = amount;
			this.accountNumber = accountNumber;
			this.atmNumber = atmNumber;
			this.time = time;
		}
		@Override
		public String toString(){
			return ("For ATM #: "+ atmNumber + "; Account #: "+ accountNumber + " at time: "+ time + "; Message: " + request + "; Amount: " + " amount"); 
		}
}
