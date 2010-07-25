package cit007_Ions;
import java.util.*;

/**
 * Creates a number of ions in a two-dimensional rectangular box.
 * The ions repel each other with a force inversely proportional 
 * to the square of their distance. Every ion is affected 
 * by every other ion. After a while, the ions should settle down 
 * into a more-or-less "stable" position, and not move very much more. 
 * 
 * @author Ganggang Hu
 * @author sxycode
 */

public class IonBox {
	private double width;
	private double height;
	private double locationX=0;
	private double locationY=0;
	private static Scanner scanner = new Scanner(System.in);
	ArrayList<Boolean> fixedOrNot = new ArrayList<Boolean>();
	ArrayList<Ion> myIonList = new ArrayList<Ion>();
	
	/**
	 * constructs IonBox with width and height
	 * Legal coordinates for x will be between 0.0 and width; 
	 * legal coordinates for y will be between 0.0 and height.
	 */

	IonBox(double width, double height){
		this.width=width;
		this.height=height;
	}

	/**
	 * puts an ion in a particular starting location. 
	 * When this is called, all the ions will move around 
	 * to reach a new stable position.
	 */

	void putIon(Ion ion){
		if (checkExistingIon(ion)) return;
		myIonList.add(ion);
		fixedOrNot.add(false);
		ionMove();
	}

	/**
	 * Check if a particular location is already occupied
	 * by an ion
	 * @param ion the ion to put there
	 */
	private boolean checkExistingIon(Ion ion) {
		for(int i=0;i<myIonList.size();i++) {
			if(Math.abs(myIonList.get(i).getX()-ion.getX())< 0.0001 &&
					Math.abs(myIonList.get(i).getY()-ion.getY()) < 0.0001) {
				System.out.println("\nThere is already an ion in this position!\n");
				return true;
			}
		}
		return false;
	}

	/**
	 * puts an fixed ion in a particular location, and "glue" it there. 
	 * When this is called, all the other non-fixed ions will move around 
	 * to reach a new stable position.
	 * @param x the position of x-coordinate
	 * @param y the position of y-coordinate
	 */
	void putFixedIon(Ion ion){
		if (checkExistingIon(ion)) return;
		myIonList.add(ion);
		fixedOrNot.add(true);
		ionMove();
	}

	/**
	 * remove a particular ion. Again, this will cause the other 
	 * ions to move around until they reach a new, stable position.
	 * @param ion the ion that is removed
	 */

	void removeIon(Ion ion){
		for(int i=0;i<myIonList.size();i++) {
			if(Math.abs(myIonList.get(i).getX()-ion.getX())< 0.0001 &&
					Math.abs(myIonList.get(i).getY()-ion.getY()) < 0.0001) {//locate the ion
				myIonList.remove(i);
				fixedOrNot.remove(i);
				ionMove();
				break;
			}
			else System.out.println("There is no ion in the specified location!");
		}
	}

	/**
	 * Moves all the non-fixed ion in the box. 
	 * The fixed ions will remain their positions
	 */
	private void ionMove(){
		double totalForceX = 1;//sum of all forces between ions in absolute value on X axis
		double totalForceY = 1;//sum of all forces between ions in absolute value on Y axis
		double minForce = 1.0/(width*width+height*height);
		while(totalForceX > 1e-3*minForce || totalForceY > 1e-3*minForce){//when there is any force 
			ArrayList<Double> sumForceX = new ArrayList<Double>();//net force on X axis direction on ion i
			ArrayList<Double> sumForceY = new ArrayList<Double>();//net force on Y axis direction on ion i
			totalForceX = totalForceY = 0.0;
			for(int i=0;i<myIonList.size();i++) {
				double x = myIonList.get(i).getX();
				double y = myIonList.get(i).getY();
				if (fixedOrNot.get(i)== true) {
					sumForceX.add(0.0);
					sumForceY.add(0.0);
					continue;
				}
				double ionSumForceX=0.0;//the net force of all ions on i on X axis directions
				double ionSumForceY=0.0;//the net force of all ions on i on Y axis directions
				for(int j=0;j<myIonList.size();j++) {
					if(i == j) continue;//skip if i and j are the same
					double m = myIonList.get(j).getX();
					double n = myIonList.get(j).getY();
					double a=(x-m)*(x-m);
					double b=(y-n)*(y-n);
					double forceX=0.0;//the force from ion j to i on X axis
					double forceY=0.0;//the force from ion j to i on Y axis
					double directX;//the direction of the force on X axis, positive to be right, negative to be left
					if (a < 1e-8) directX = 0; 
					else directX = (x-m)/Math.abs(x-m); 
					double directY;//the direction of the force on Y axis, positive to be up, negative to be down
					if (b < 1e-8) directY = 0; 
					else directY = (y-n)/Math.abs(y-n);
					forceX = 1.0/(a+b)*Math.cos(Math.atan(Math.abs((y-n)/(x-m))));
					forceY = 1.0/(a+b)*Math.sin(Math.atan(Math.abs((y-n)/(x-m))));
					ionSumForceX += forceX*directX;
					ionSumForceY += forceY*directY;
				}
				if (myIonList.get(i).getX()< 1e-8 || width-myIonList.get(i).getX()<1e-8) 
					ionSumForceX = 0.0;//if they are on edges (x=0 or x=width), no force on x axis
				if (myIonList.get(i).getY()< 1e-8 || height-myIonList.get(i).getY()< 1e-8)
					ionSumForceY = 0.0;//if they are on edges (y=0 or y=height), no force on y axis
				totalForceX += Math.abs(ionSumForceX);//all net force values on X and Y axis 
				totalForceY += Math.abs(ionSumForceY);//added together to determine equilibrium
				sumForceX.add(ionSumForceX);
				sumForceY.add(ionSumForceY);
			}
			for (int i = 0; i < myIonList.size(); i++) {
				moveIon(i, sumForceX.get(i),sumForceY.get(i)); //move all the ions according to the force exerted on them
			}
		}
	}

	/**
	 * Moves the particular ion in the box proportional to the
	 * force exerted on it.
	 * @param listIndex: tells which ion it is
	 * @param ionSumForceX: the net force exerted on the ion from other ions
	 * @param ionSumForceY: the net force exerted on the ion from other ions
	 */
	private void moveIon(int listIndex, double ionSumForceX, double ionSumForceY) {
		if (fixedOrNot.get(listIndex) == true) return;//if fixed ion, do not move
		double moveX = ionSumForceX*width*1e-2;//the move is proportional to the 
		double moveY = ionSumForceY*height*1e-2;//dimensions of the ion box
		if (moveX > width/100.0) moveX = width/100.0;//prevent ions from moving too fast
		if (moveY > height/100.0) moveY = height/100.0;//if they are very close to each other
		double x = myIonList.get(listIndex).getX();
		double y = myIonList.get(listIndex).getY();
		double newX = x+moveX;
		double newY = y+moveY;
		if (newX < 0.0) newX = 0.0;
		if (newX > width) newX = width;
		if (newY < 0.0) newY = 0.0;
		if (newY > height) newY = height;
		myIonList.get(listIndex).setX(newX);
		myIonList.get(listIndex).setY(newY);
	}
	/**
	 * Calculates the minimum, maximum, and average distance 
	 * from each ion to its nearest neighbor and prints
	 * the results
	 */
	private void minMaxAvgNearestDistance()
	{
		double sum=0.0;
		double avgNearest=0.0;
		double minNearest=0.0;
		double maxNearest=0.0;
		ArrayList<Double> minNeighborDis = new ArrayList<Double>();//list of nearest neighbor distances
																	//for each ion.
		for(int i=0;i<myIonList.size();i++) {
			double min = 0.0;
			for(int j=0;j<myIonList.size();j++) {
				if(i == j) continue;
				double a=Math.pow(myIonList.get(i).getX()-myIonList.get(j).getX(),2.0);
				double b=Math.pow(myIonList.get(i).getY()-myIonList.get(j).getY(),2.0);
				double distance=Math.sqrt(a+b);
				if (min <1e-8 || distance < min) min = distance;
			}
			minNeighborDis.add(min);
		}
		for (int i=0; i< myIonList.size(); i++) {
			if (minNearest <1e-8 || minNeighborDis.get(i) < minNearest) 
				minNearest = minNeighborDis.get(i);
			if (maxNearest < minNeighborDis.get(i))
				maxNearest = minNeighborDis.get(i);
			sum += minNeighborDis.get(i);
		}
		avgNearest=sum/(myIonList.size());
		System.out.println("The minimum distance from each ion to its nearest neighbor is: "+minNearest);
		System.out.println("The maximum distance from each ion to its nearest neighbor is: "+maxNearest);
		System.out.println("The average distance from each ion to its nearest neighbor is: "+avgNearest);
	}

	/**
	 * allows the user to create an IonBox, place and remove Ions in it, 
	 * and find out where they end up. After each change, it should 
	 * also display the minimum, maximum, 
	 * and average distance from each ion to its nearest neighbor. 
	 */
	public static void main(String[] args) {
		System.out.println("Welcome to Ion Dance!");
		System.out.println("Please specify the width and height(width,height) " +
		"of a two-dimensional rectangular box to put ions in.");
		System.out.println("Please specify the width:");
		doubleInput();

		double width = input();//input() verifies if the input > 0
		System.out.println("Please specify the height:");
		doubleInput();
		double height = input();
		new IonBox(width, height).run();
	}

	/**
	 * Asks the user whether to put an regular or fixed ion into the box
	 * or to remove an ion from the box and perform accordingly. Then 
	 * the relevant information of all the ions in the box are printed
	 */
	private void run() {
		boolean quitIon=false;
		while(!quitIon) {
			System.out.println("\nIf you want to put an ion into the box, press 1.");
			System.out.println("If you want to put an fixed ion into the box, press 2.");
			System.out.println("If you want to remove an ion into the box, press 3.");
			System.out.println("If you want to quit, press 4.");
			intInput(); // verifies that the input is an int
			int option = selectionInput();// selectionInput() verfies the input is 1,2,3 or 4
			switch (option) {
			case 1:
				System.out.println("Please specify the (x,y) location of the ion you want to put into the box:");
				inputIon();
				Ion newIon = new Ion(locationX,locationY);
				putIon(newIon);
				printIons();
				break;
			case 2:
				System.out.println("Please specify the (x,y) location of the fixed ion you want to put into the box:");
				inputIon();
				Ion newFixedIon = new Ion(locationX,locationY);
				putFixedIon(newFixedIon);
				printIons();
				break;
			case 3:
				if(myIonList.size()==0)
					System.out.println("There is no ion in the box.");
				else {
					System.out.println("Please specify the (x,y) location of the ion you want to remove from the box:");
					System.out.println("Please specify the x axis location:");
					doubleInput();
					locationX = input(width);
					System.out.println("Please specify the y axis location:");
					doubleInput();
					locationY = input(height);
					Ion ion = new Ion(locationX,locationY);
					removeIon(ion);
					printIons();
				}
				break;
			case 4:
				quitIon = true;
				System.out.println("Good Bye!");
				break;
			}
		}
	}

	/**
	 * Asks the user to input the location of the ion to be placed at
	 */
	private void inputIon() {
		System.out.println("The (x,y) for bottom left location of the ion box is (0,0)");
		System.out.println("Please specify the x axis location:");
		doubleInput();
		locationX = input(width);
		System.out.println("Please specify the y axis location:");
		doubleInput();
		locationY = input(height);
	}

	/**
	 * Prints the locations of all the ions present in the box.
	 * Also prints out the min, max and average distance
	 * from each ion to its nearest neighbor.
	 */
	private void printIons() {
		System.out.println("The final state of all ions are as follows:");
		if (myIonList.size() == 0)
			System.out.println("There is no ion in the box!");
		else {for(int i=0;i<myIonList.size();i++)
			System.out.println("("+myIonList.get(i).getX()+","+myIonList.get(i).getY()+")");
		}
		minMaxAvgNearestDistance();
	}

	/**
	 * verifies that the input is a double
	 */
	private static void doubleInput() {		
		while (!scanner.hasNextDouble()){
			scanner.nextLine ();
			inputError();
		}
	}

	/**
	 * prints out an input error message
	 */
	private static void inputError() {
		System.out.print("Error! Please enter a valid number: ");
	}
	/**
	 * verifies the input is a value between 0 and range
	 * @param range: the upper limit of the value
	 * @return returns the input if it's valid
	 */
	private static double input(double range){
		double input=scanner.nextDouble();
		while (input<0 || input>range){	//check if the number is 1,2,3 or 4
			scanner.nextLine ();
			inputError();
			doubleInput();//checks if the next input is a double
			input = scanner.nextDouble();
		}
		return input;
	}

	/**
	 * verifies that the input is a double
	 * @return the input if it's valid
	 */
	private static double input() {
		double input=scanner.nextDouble();
		while (input<0){	//check if the number is 1,2 or 3
			scanner.nextLine ();
			inputError();
			doubleInput();//checks if the next input is a double
			input = scanner.nextDouble();
		}
		return input;
	}

	/**
	 * verifies that the input is a valid int
	 */
	private void intInput() {
		while (!scanner.hasNextInt()){
			scanner.nextLine ();
			inputError();
		}
	}

	/**
	 * Verifies that the input is an integer 1, 2, 3 or 4
	 * @return the integer input if it's valid
	 */
	private int selectionInput() {
		int input=scanner.nextInt();
		while (input<1 || input >4){//check if the number is 1,2 or 3
			scanner.nextLine ();
			inputError();
			intInput();//checks if the next input is a double
			input = scanner.nextInt();
		}
		return input;
	}
}

