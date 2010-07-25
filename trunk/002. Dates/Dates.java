import java.util.Scanner;
/**
 * Assignment 2 for CIT591, Fall 2007.
 * @author Dave Matuszek
 * @author Xiaoyi Sheng
 * @author Jonathan Wang
 * @version Sep 11, 2007
 */
public class Dates{
    /**
     * Computes day of the week for specific dates, and the number
     * of days between two dates.
     *
     * @param args Not used.
     */
    public static void main(String[] args) {
        new Dates().run(); //what's the purpose of this line?
    }
    /**
     * Asks the user whether to find and print the day of the week,
     * find and print the number of days from one date to the next,
     * or quit.
     */
    private void run() { 
        boolean quitting = false;
        while (!quitting) {
            // Ask the user whether to: find and print the day of the week,
            // find and print the number of days from one date to the next, 
            // or quit. If quitting, set the boolean variable 'quitting'
            // to true.
            int choice;
            Scanner sc = new Scanner(System.in); 
          
            System.out.println("To find and print Day of the Week, press '1'");
            System.out.println("To find and print Number of days from one date to the next, press '2'"); 
            System.out.println("To quit, press '3'");
          
            choice = sc.nextInt();
          
            if (choice==1||choice==2){
                if (choice==1) {
                    findAndPrintDayOfWeek();
                }
                if (choice==2) {
                    findAndPrintDaysApart();
                }
                System.out.println ("\nCalculation is done. To quit, press '3'\nTo do it again, press '1'");
                choice = sc.nextInt();
            }
            if (choice==3){
                quitting=true; 
            }//if choosing other than 1,2,3, quitting==false and while loop repeats.
        }
    }
    /**
     * Asks the user for a specific date, determines what day of
     * the week it falls on, and prints that day. 
     */
    public void findAndPrintDayOfWeek() {
        // Ask the user for a date (year, month, day)
        // Call findDayOfWeek to find what day of the week it is
        // Print the result
        int[] date = dateInput(); 
        int year = date[0];
        int month = date[1];
        int day = date[2];
        String dayOfWeek;

        // Prints the day of the week for the date supplied
        dayOfWeek = findDayOfWeek(year, month, day); 
        System.out.println(month + "/" + day + "/" + year + " is a " + 
                            dayOfWeek + ".");
    }
    /**
     * Determines what day of the week a given date falls on. 
     *
     * @param year The year.
     * @param month The month (January = 1, December = 12).
     * @param day The day of the month.
     * @return One of the Strings "Sunday", "Monday", "Tuesday", 
     *      "Wednesday", "Thursday", "Friday", or "Saturday".
     */
    public String findDayOfWeek(int year, int month, int day) {
        
        // Month code designations 
        int[] monthCode = {0, 3, 3, 6, 1, 4, 6, 2, 5, 0, 3, 5};
        if (isLeapYear(year)){
        monthCode[0] = 6;
        monthCode[1] = 2;
        }
        
        // findDayOfWeek Algorithm: 
        int total = year + year / 4;
        total = total - year / 100;
        total = total + year / 400;
        total = total + day;
        total = total + monthCode[month - 1];
        total = total - 1; 
        int remainder = total % 7; 
        String[] daysOfWeek = {"Sunday", "Monday", "Tuesday",
                "Wednesday", "Thursday", "Friday", "Saturday"}; 
        String dayOfWeek = daysOfWeek[remainder];
        // 0 = Sunday, 1 = Monday, .. 6 = Saturday
        return dayOfWeek;
    }

    /**
     * Asks the user for two dates, determines how far apart 
     * (in days) those dates are, and prints the result.
     */
    public void findAndPrintDaysApart() {
        // Ask the user for 2 dates (year, month, day)
        // Call findDaysApart to find the number of days apart 
        // Print the result

        // Asks for first date
        System.out.println("1st Date...");
        int[] date = dateInput();;
        int year1 = date[0];
        int month1 = date[1]; 
        int day1 = date[2];

        // Asks for 2nd date
        System.out.println("2nd Date...");
        date = dateInput();
        int year2 = date[0];
        int month2 = date[1];
        int day2 = date[2];

        // Finds the days apart for the dates supplied
        int daysApart = findDaysApart(year1, month1, day1,
                year2, month2, day2);
        // Prints the days apart 
        System.out.println(month1 + "/" + day1 + "/" + year1 + " and " +
                month2 + "/" + day2 + "/" + year2 + " are " +
                daysApart + " days apart."); 
    }
 
    /**
     * Determines how far apart (in days) two given dates are.
     * If the two dates are the same, the result is zero; if
     * the second date occurs before the first date, the result 
     * is negative.
     *
     * @param year1 The year of the first date.
     * @param month1 The month of the first date.
     * @param day1 The day of the month of the first date.
     * @param year2 The year of the second date. 
     * @param month2 The month of the second date
     * @param day2 The day of the month of the second date.
     */
    public int findDaysApart(int year1, int month1, int day1,
            int year2, int month2, int day2) { 
        
        int daysApart;
        
        // findDaysApart Algorithm
        // 1. Make sure the first date is earlier than or equal to the 
        //       second date.
        boolean isDay2Later = true; 
        if (year2 < year1){isDay2Later = false;}
        if (year2 == year1){
            if (month2 < month1){isDay2Later = false;}
            if (month2 == month1){
                if (day2 < day1){isDay2Later = false;} 
            }
        }
    
        // If this isn't the case, rearrange the dates and remember that 
        // you did so.
        if (!isDay2Later){
            // Hold the values temporarily 
            int day = day1;
            int month = month1;
            int year = year1;
            // reset earlier date
            day1 = day2;
            month1 = month2;
            year1 = year2; 
            // reset later date
            day2 = day;
            month2 = month;
            year2 = year;
        }

        // 2. If the two dates are in the same year, 
        if (year1 == year2){ 
            // Find the day of the year (1..366) of each date, and subtract.
            daysApart = findDayOfYear(year2, month2, day2) 
                        - findDayOfYear(year1, month1, day1);
        } 
        
        // 3. If the two dates are in different years,
        else {
            // 1. Find the number of days remaining in the first year (number 
            //      of days in year, minus day of the year). 
            int dayOfYear1 = findDayOfYear(year1, month1, day1);
            int daysRemaining = findDayOfYear(year1, 12, 31) 
                                - dayOfYear1;
                        
            // 2. Add the day of the year of the second year. 
            int dayOfYear2 = findDayOfYear(year2, month2, day2);
            
            // 3. Add the number of days in each intervening year (365 
            //      or 366, depending on the year).
            int year = year1 + 1; 
            int daysInBetween = 0;
            while(year < year2){
                daysInBetween = daysInBetween 
                                + findDayOfYear(year, 12, 31);
                year = year + 1; 
            }
            
            // Sum all the days up
            daysApart = daysRemaining + dayOfYear2 + daysInBetween;
        }
        return daysApart;
    }
  
    /**
     * Called upon by findAndPrintDayOfWeek() and
     * findAndPrintDaysApart() to ask the user for a date input.
     * 
     * @return A matrix of integers corresponding to the year, the month, 
     * and the date inputed by the user 
     */
    public int[] dateInput(){
        Scanner askDate;
        int year;
        int month;
        int day;

        // Asks for year input
        askDate = new Scanner( System.in);
        System.out.println("Enter Year:");
        year = askDate.nextInt();

        // Asks for month input
        System.out.println("Enter Month (1,..,12) :");
        month = askDate.nextInt();
        //            Checks for input error, if month is invalid
        while (month < 1 || month > 12){
            System.out.println("ERROR: Please enter again a Month between " + 
                                "1 and 12:");
            month = askDate.nextInt();
        }

        // Asks for day input
        System.out.println("Enter Day of Month:");
        day = askDate.nextInt();
        // Sets the number of days in each month. Accounts for leap year.
        int[] daysInMonth = {31,28,31,30,31,30,31,31,30,31,30,31};
        if (isLeapYear(year)) {daysInMonth[1] = 29;} 
                // Checks for error, if day of month is invalid
        while (day < 1 || day > daysInMonth[month - 1]){
            System.out.println("ERROR: Day doesn't exist in this month. Please enter again:"); 
            day = askDate.nextInt();
        }
        
        // Returns {year, month, day}
        int[] date = {year, month, day};
        return date;
    }
  
    /**
     * Checks if the year is a leap year.
     * 
     * @param year The year to be tested
     * @return true or false 
     */
    public boolean isLeapYear(int year){
        // Algorithm to check for leap year 
        boolean isLeapYear = year % 4 == 0;                // a leap year is a year 
                                                        // divisible by 4
        if (isLeapYear && year % 100 == 0) {            // but not by 100... 
            if (year % 400 == 0) {isLeapYear = true;}     // unless also divisible 
                                                        // by 400
            else {isLeapYear = false;}
            }
        return isLeapYear; 
    }
  
    /**
     * Finds day of the year for a given Date.
     * 
     * @param year
     * @param month
     * @param day
     * @return Integer corresponding to Day of the year 
     */
    public int findDayOfYear(int year, int month, int day){
        // Sets the number of days in each month. 
        // Days in February are corrected for leap year.
        int[] daysInMonth = {31,28,31,30,31,30,31,31,30,31,30,31}; 
        if (isLeapYear(year)) {daysInMonth[1] = 29;}
        
        // Adds up all the days to find the day of year
        int dayOfYear = 0;
        // Add the number of days in preceding months
        int monthNumber = 1; 
        while (monthNumber < month){
            dayOfYear = dayOfYear + daysInMonth[monthNumber - 1];
            monthNumber = monthNumber + 1;
        }
        // Add the number of days in the current month 
        dayOfYear = dayOfYear + day;
        
        return dayOfYear;
    }
}
