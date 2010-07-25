package com.kemplerEnergy.model;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

public class CounterParty extends BaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5393474755721992267L;
	
	private static final String[] Catergory = {"BROKER", "STORAGE", "TRANSPORT", "EPA_PARTNER" };
	protected String fullName;
	protected String name;
	protected String phone;
	protected String type;
	
	// foreign key constraint
	protected List<Address> addresses = new ArrayList<Address>();
	// private Set<PaymentTerms> paymentTerms = new HashSet<PaymentTerms>();  
	
	// Non mapping attribute
	// private Address defaultAddress;
	
	public CounterParty(){}


	public void setId(int id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public List<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public Address returnDefaultAddress() {
		if (addresses.isEmpty())
			throw new IllegalArgumentException("No address available");
		for (Address a: addresses) {
			// TODO: find the default type
			return a;
		}
		throw new MissingResourceException("Default address not found", "com.kemplerEnergy.model.Address", "type");
	}
	
	public void addAddress(Address address) {
		if (address == null)
			throw new IllegalArgumentException("Address can't be null");
		if (address.getCounterParty() != null)
			throw new IllegalArgumentException("This address already associated with other Counter Party");
		address.setCounterParty(this);
		addresses.add(address);
	}
	/* so far it seems we don't need this
	private void addContract(Contract contract) {
		if (contract == null) 
			throw new IllegalArgumentException("Null contract added to the counter party!");

		if (PhysicalContract.class.isInstance(contract)) {
			if (((PhysicalContract)contract).getCounterParty() != null)
				((PhysicalContract)contract).getCounterParty().getContracts().remove(contract);
			((PhysicalContract)contract).setCounterParty(this);
		} else if (NonTTContract.class.isInstance(contract)){
			if (((NonTTContract)contract).getCounterParty() != null)
				((NonTTContract)contract).getCounterParty().getContracts().remove(contract);
			((NonTTContract)contract).setCounterParty(this);
		} else throw new IllegalArgumentException(contract.toString() + " is not suppose to have counter party");
		
		contracts.add(contract);

	}
	*/

	public void setDefault() {
		name = "Dummy from JPA";
		fullName = "Full Name is still Dummy";
		type = "EPA_PARTNER";
		phone = "19174026778";
	}
	
	public static void main(String[] args) {
		// new CounterParty().test();
	}
	
/*	private void test() {
		
		Address a = new Address();
		CounterParty c = new CounterParty();
		a.setDefault();
		c.setDefault();
		c.addAddress(a);
		
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("kempler");

		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		em.persist(c);
		
		tx.commit();
		em.close();
		emf.close();
	} */
}
