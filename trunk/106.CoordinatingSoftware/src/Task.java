import java.util.Random;

/**
 * Class to create tasks
 * @author Xiaoyi Sheng
 * @version Mar 6th, 2008
 */

public class Task {
	private final int UPPER_LIMIT = 20;
	private final int LOWER_LIMIT = 4;
	private int taskLength;
	private int taskOrder;
	private int finishTime;
	private String programmerName;
	Random random = new Random();
	
	/**
	 * Constructs a task with a random length within a range of 4 to 20 hours
	 */
	public Task(){
		taskLength = random.nextInt(UPPER_LIMIT-LOWER_LIMIT+1) + LOWER_LIMIT;
		taskOrder++;
	}
	/**
	 * Setter for the name of the programmer performing this task
	 * @param name Name of the programmer performing the task.
	 */
	public void setProgrammerName(String name){
		programmerName = name;
	}
	
	/**
	 * Getter for the name of the programmer performing this task
	 * @return Name of the programmer performing the task.
	 */
	public String getProgrammerName(){
		return programmerName;
	}
	
	/**
	 * Getter for the length of the task
	 * @return The length of the task
	 */
	public int getLength(){
		return taskLength;
	}
	
	/**
	 * Setter for the order the task was completed.
	 * @param order The order the task was completed.
	 */
	public void setTaskOrder(int order){
		taskOrder = order;
	}
	
	/**
	 * Getter for the order the task was completed.
	 * @return The order the task was completed.
	 */
	public int getTaskOrder(){
		return taskOrder;
	}
	
	/**
	 * Setter for the time the task was completed.
	 * @param time The time the task was completed.
	 */
	public void setFinishTime(int time){
		finishTime = time;
	}
	
	/**
	 * Getter for the time the task was completed.
	 * @return The time the task was completed.
	 */
	public int getFinishTime(){
		return finishTime;
	}
}
