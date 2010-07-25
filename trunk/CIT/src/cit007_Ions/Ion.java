package cit007_Ions;
/**
 * creates ions
 */

public class Ion {
	private double x;
	private double y;

	/**
	 * constructs a new ion, with its x and y coordinate position
	 */	
	Ion(double x,double y){
		this.x=x;
		this.y=y;
	}

	/**
	 * sets the value of x
	 * @param x The value of x
	 */

	void setX(double x){
		this.x=x;
	}

	/**
	 * sets the value of y
	 * @param y The value of y
	 */

	void setY(double y){
		this.y=y;
	}

	/**
	 * sets the value of both x and y 
	 * @param x The value of x
	 * @param y The value of y
	 */

	void setXandY(double x, double y){
		this.x=x;
		this.y=y;
	}

	/**
	 * gets the value of x 
	 * @return x The value of x
	 */

	double getX(){
		return x;
	}

	/**
	 * gets the value of x 
	 * @return y The value of y
	 */

	double getY(){
		return y;
	}

}
