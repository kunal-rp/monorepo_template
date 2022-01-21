# template Service

All services regarding generation of schedules and evaludations of tasks. 

## API

 - GenerateScheduleEntries:
  * For a given time slot generates task entries based on task templates
  * INPUT : (int) slot unix start time  , (int) slot unix end time  
  * OUTPUT : List<TaskEnty> 