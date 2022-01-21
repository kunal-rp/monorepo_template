# Backend

gRPC servers mainly written in Java

## General Design

I would like to incorperate generation into task managment, specifically for current/future tasks.

This goes hand in hand with Univeral Rule, prioritizing to get accurate logs. Also will help with managment of tasks when viewing schedules for future dates

Task Templates are stored w/ information about what recurring task details are; what is due date/pattern, what data is required(seperate db); this DB will be used to generate the tasks entries for a schedule in the future. 

Task entries are actual task objects dispalyed in schedules.Rcurring tasks will generate these per schedule and one time tasks will create these directly. 

Per day(or time interval), a cron job will parse and run said templates to generate the recurring tasks entries that were made to be due in the next 12 hours, call this the timeslot, for modifications; creating them, inputting them in entries db, and marking them as due/not-completed.To determine tasks that must immediatly go through completion flow, just need to parse for non completed tasks w/ start times in the past.


Altering Timings/Data of Task:
    * get the original start time said task 
    * get corresponding task by template id and originoal start time
    * update values

Upon user completed a task and inputting data would updating the existing task entry w/ flag and data. 


## DB's

There will be two db's for tasks, Templates and Entries. 

Task Templates: 
- template_id : int
- creation time : int ( unix)
- title : string
- duration : int
- description : string
- project_id : int
- reoccuring pattern : string 
- due date

Per design, task should either have reoccuring pattern or due date. 

Task Data Collection ( one to many): 
- template_id : int
- data_label : string
- data_value_type : string [INT, STRING, BOOLEAN]

Task Entry: 
- task_id : int
- template_id : int ( optional ,not needed for one time)
- start_time : int (unix)
- duration : int
- start_time_alterations :
- duration_alterations : int
- completed : boolean
- json_data : string

json_data would be stringified values inputted by user matching ones set per templates in Task Data DB.

origional_start_time will be immutable after initial creation, start time is mutable per user altering schedule


post completion of entry for one time tasks, linked task template and data collection entries should be deleted 





