package Bellroy;

import java.io.IOException;
import java.util.List;

public class Parser {

    public void parse(String userInput, TaskList taskList, Ui ui, Storage storage) {
        String[] input;
        if (userInput.startsWith("find ")) {
            input = userInput.split(" ", 2);
        } else {
            input = userInput.split(" /", 2);
        }
        String type = input[0].split(" ")[0].toLowerCase();
        String description = input[0].substring(type.length());
        try {
            switch (type) {
                case("bye"):
                    ui.printByeMessage();
                    System.exit(0);
                    break;
                case("list"):
                    ui.printTaskList(taskList);
                    break;
                case("mark"):
                    int position = Integer.parseInt(userInput.split(" ")[1]);
                    taskList.get(position - 1).markDone();
                    ui.markDone(taskList.get(position - 1));
                    storage.save(taskList);
                    break;
                case("unmark"):
                    int pos = Integer.parseInt(userInput.split(" ")[1]);
                    taskList.get(pos - 1).undo();
                    ui.markUndone(taskList.get(pos - 1));
                    storage.save(taskList);
                    break;
                case("todo"):
                    Task todo = new Todo(description);
                    taskList.addTask(todo);
                    ui.printTaskAddedMessage(todo, taskList.size());
                    storage.save(taskList);
                    break;
                case("deadline"):
                    String dueDate = input[1].split(" ", 2)[1].trim();
                    Task deadline = new deadline(description, dueDate);
                    taskList.addTask(deadline);
                    ui.printTaskAddedMessage(deadline, taskList.size());
                    storage.save(taskList);
                    break;
                case("event"):
                    String startTime = input[1].split(" /", 2)[0].split(" ", 2)[1].trim();
                    String endTime = input[1].split(" /", 2)[1].split(" ", 2)[1].trim();
                    Task event = new Event(description, startTime, endTime);
                    taskList.addTask(event);
                    ui.printTaskAddedMessage(event,taskList.size());
                    storage.save(taskList);
                    break;
                case("delete"):
                    int target = Integer.parseInt(userInput.split(" ")[1]);
                    Task temp = taskList.get(target - 1);
                    taskList.removeTask(target - 1);
                    ui.printTaskDeleted(temp, taskList.size());
                    storage.save(taskList);
                    break;
                case("find"):
                    String keyword = input[1].trim();
                    List<Task> output = taskList.findTask(keyword);
                    ui.findTask(output);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
