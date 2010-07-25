import java.util.ArrayList;

/**
 * Class to create programmers
 * @author sxycode
 * @version Mar 6th, 2008
 */
public class Programmer implements Runnable {
	private String name;
	private ArrayList<Task> taskList;
	private final int UPPER_LIMIT = 40, LOWER_LIMIT = 32;

	/**
	 * Creates a new programmer.
	 * @param name The name of the programmer.
	 */
	public Programmer(String name){
		this.name = name;
		newTaskList();
	}

	/**
	 * Have the programmer perform tasks and reports the finishing time of each task.
	 */
	@Override
	public void run(){
		int finishTime = 0;
		for(Task task: taskList){
			try{
				Thread.sleep(20*task.getLength());
			}
			catch (InterruptedException e){}
			finishTime += task.getLength(); 
			task.setFinishTime(finishTime);
			synchronized (Project.finishedTaskList) {            
				Project.finishedTaskList.add(task);
				Project.finishedTaskList.notifyAll();
			}
			synchronized (Project.weeklyQueue) {            
				Project.weeklyQueue.put(task);
				Project.weeklyQueue.notifyAll();
			}
		}
	}

	/**
	 * Adds a task to the programmer's task list.
	 * @param task The task to add
	 * @return <code>true</code> if the task was successfully added.
	 */
	public boolean addTask(Task task){
		if ( !enoughTasks()&& this.totalHours()+task.getLength()<=UPPER_LIMIT){
			taskList.add(task);
			return true;
		}
		return false;
	}

	/**
	 * Computes how many hours of task the programmer has been assigned.
	 * @return Total number of hours of assigned task.
	 */
	public int totalHours(){
		int hours = 0;
		for(Task task: taskList)
			hours += task.getLength();
		return hours;
	}

	/**
	 * Initializes the programmer's task list.
	 */
	public void newTaskList(){
		taskList = new ArrayList<Task>();
	}

	/**
	 * Getter for the programmer's taskList
	 * @return The programmer's taskList
	 */
	public ArrayList<Task> getTaskList(){
		return taskList;
	}

	/**
	 * Check if the programmer has taken enough tasks.
	 * @return <code>true</code> if the programmer has taken enough tasks
	 */
	public boolean enoughTasks(){
		return this.totalHours()>LOWER_LIMIT;
	}

	/**
	 * Prints the lengths of tasks the programmer has been assigned.
	 */
	public void printTasks(){
		System.out.printf("%-5s has tasks requiring ", name);
		for(int i = 0; i < taskList.size(); i++){
			System.out.print(taskList.get(i).getLength());
			if (i+1 < taskList.size())
				System.out.print(", ");
		}
		System.out.println(" hours.");
	}

	/**
	 * Getter of the name of the programmer.
	 * @return Name of the programmer
	 */
	public String getProgrammerName(){
		return name;
	}
}
