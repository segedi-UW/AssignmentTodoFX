# Welcome to the AssignmentTodo Webpage

This is my most comprehensive project to date - it is a standalone GUI Application created using javafx. It fufills my need for a task manager that opens links, reminds me when assignments are close to being due (including a display of the subsequent next 5 days) that dynamically changes each day - the program can be left running overnight if desired and will automatically shift the days etc (great for Night Owls).

## Technologies
* Javafx - FXML & CSS
* Git
* Maven

## Features
As for the project itself... Here is what I have done so far
* Task Creation - including a required summary and date/time and optional category (that you can create), link, and description
* Preferences - display in military time vs. standard time, if you want to load the default file automatically, notification sounds etc.
* Persistent Storage - Can save and load assignment files - saved as a .tdo ("t-do" ~ todo)
* Automatic update system - The Application checks the version file if internet connection is available - if it is outdated, it prompts the user to update and restart
* Reminders - Manually set and automatic reminders for when tasks (assignments) are due or close to being due - this also plays at application start if they are overdue at that time
* Open files or webpages using the link function - links can be any valid url or local accessable path
* Automatically keeps track of time with Updater daemon threads that do not remain after application close
