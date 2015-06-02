#POO's third project

##The implemented library supports the commands below:<br>
###(Command format: command \<post-command argument\> [options for the post-command argument])<br><br>

**add <type> [book] [note] [student] [teacher] [community]**<br>
*Adds an instance of the selected type to the library.*<br><br>

**rent file**<br>
*Request to rent a file from the library to an user.*<br>
(aditional information to complete the command will be asked later)<br><br>

**refund file**<br>
*Request to refund a file from an user to the library.*<br>
(aditional information to complete the command will be asked later)<br><br>

**show <type> [users] [files] [rents] [refunds] [users added] [files added]**<br>
*The first four arguments show respectively the information about the current registered users, files, rents and refunds.*<br>
*The last two arguments show the users added or the files added in the current day that the library is working on.*<br><br>

**show [filename] [username] <name>**<br>
*Shows all the files or all the usernames with names equal to the argument **name**.*<br>
*The name can be typed without accent, and the command is not case sensitive*<br>
*(Usefull command to show files code or users id, that are necessary to rent or refund files.)*<br><br>

**set date**<br>
*Sets the system date to the date that will be given by the user*<br>
*User can give dates in the past, to view files added and users added in the desired day, but cannot make any alterations at the library.*<br>
*If the date given is after the current day, others runs of the program shall tell the user what is the atual date for the system to operate correctly (e.g If today is 02/06/2015, and we input 05/06/2015, then we shutdown the program  and restart it the library will not make any alterations until the date is set to 05/06/2015 or after, again)*<br>
(aditional information to complete the command will be asked later)<br><br>

**help**<br>
*Shows all the available commands and the formats for them.*<br><br>

**reset**<br>
*Resets the library completly. The user will be asked sure if he/she want to perform the operation.*<br><br>

###Further tips will be shown at run time !<br><br>

#Using the library<br>

* Simply double click the **.jar**, and the program shall be executed in the terminal. Make sure that the folder *data* is in the same directory as the **.jar** file, because it's needed for the program to save the library information.<br>
* The files shown with the command **show files** have the field **file code** with the code needed to rent or refund that file. The same happens to the command **show users**.<br>
* To simplify the use of the program, the library is already populated with some files, but not users, as they might be added accordingly to each library.
* To use the command *show files added* and *show users added*, the library needs to be closed and opened again. To see users added in a same execution, use *show users* or *show files*.
