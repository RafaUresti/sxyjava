import java.util.ArrayList;

/**
 * This program simulates 4 programmers working on a software project.
 * At each weekly meeting they decide on a number of tasks to be performed by each person.
 * Each task takes a certain amount of time, and each person does their tasks in order. 
 * All tasks are finished in time for the next meeting. It takes 4 weeks to complete the 
 * entire project.
 * @author Xiaoyi Sheng
 * @version Mar 6th, 2008
 */
public class Project {
	ArrayList<Programmer>  programmerList;
	ArrayList<Thread> threadList;
	private Programmer alice, bob, carol, ted;
	static ArrayList<Task> finishedTaskList;
	static Queue<Task> weeklyQueue;
	private int taskOrder = 0;
	
	public static void main(String[] args) {
		new Project().run();
	}
	/**
	 * Sets up the whole project by creating programmers, having meetings
	 * and have the programmers perform tasks and make reports, and summarizes
	 * the process of task completion.
	 */
	private void run() {
		finishedTaskList = new ArrayList<Task>();
		System.out.printf("                   Output\n");
		printLine();
		for(int i = 0; i < 4; i++){
			createProgrammers();
			haveMeeting(i+1);
			performTasks();
			printReport();
		}
		printSummary();
	}

	/**
	 * Creates programmers Bob, Carol, Ted and Alice.
	 */
	private void createProgrammers() {
		programmerList = new ArrayList<Programmer>();
		threadList = new ArrayList<Thread>();
		alice = new Programmer("Alice");
		bob = new Programmer("Bob");
		carol = new Programmer("Carol");
		ted = new Programmer("Ted");
		programmerList.add(bob);
		programmerList.add(carol);
		programmerList.add(ted);
		programmerList.add(alice);
		Thread a = new Thread(alice);
		Thread b = new Thread(bob);
		Thread c = new Thread(carol);
		Thread t = new Thread(ted);
		threadList.add(b);
		threadList.add(c);
		threadList.add(t);
		threadList.add(a);
	}

	/**
	 * Holds a project meeting to assign tasks to programmers. 
	 * @param i The meeting number.
	 */
	private void haveMeeting(int i) {
		System.out.println("Meeting #"+i);
		for (Programmer pg: programmerList){
			pg.newTaskList();
			while(!pg.enoughTasks()){
				Task task = new Task();
				taskOrder++;
				task.setTaskOrder(taskOrder);
				task.setProgrammerName(pg.getProgrammerName());
				if (!pg.addTask(task))
					taskOrder--;
			}
			pg.printTasks();
		}
		printLine();
	}

	/**
	 * Have the programmers start performing the tasks.
	 */
	private void performTasks() {
		weeklyQueue = new Queue<Task>();
		for (Thread td: threadList){
			td.start();
		}
		for (Thread td: threadList){
			try{
				td.join();
			}
			catch (InterruptedException e){}
		}
	}

	/**
	 * Prints the weekly report of programmer task completion.
	 */
	private void printReport() {
		Task tk = new Task();
		while((tk = weeklyQueue.get())!= null)
			System.out.printf("    %-5s finishes task number %d after %d hours.\n",
					tk.getProgrammerName(), tk.getTaskOrder(), tk.getFinishTime());
		printLine();
	}
	
	/**
	 * Prints the summary of task completion in the order of completion time.
	 */
	private void printSummary() {
		System.out.println("Project completed!\n");
		System.out.println("Order in which tasks were completed:");
		for (int i = 0; i < finishedTaskList.size(); i++){
			System.out.printf(" %2d ",finishedTaskList.get(i).getTaskOrder());
			if(((i+1)%12)==0) System.out.println();
		}
	}

	/**
	 * Prints a separating line.
	 */
	private void printLine(){
		System.out.println("--------------------------------------------------");
	}

}
