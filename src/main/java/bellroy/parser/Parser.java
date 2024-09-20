package bellroy.parser;

import bellroy.storage.Storage;
import bellroy.task.TaskList;
import bellroy.GUI.Ui;
import bellroy.task.Event;
import bellroy.task.Task;
import bellroy.task.Todo;
import bellroy.task.Deadline;
import javafx.application.Platform;

import java.io.IOException;
import java.time.format.DateTimeParseException;

/**
 * The parser class encapsulates the logic of parsing user commands. Depending on the user input, the parser will
 * perform the necessary logic.
 */
public class Parser {

    /**
     * main method containing the logic to parse user command
     * @param userInput the input by the user
     * @param taskList the tasklist to perform operations on
     * @param ui the Ui to print the correct messages
     * @param storage the storage which contains the tasklist's data
     * @return the relevant Ui message
     */
    public String parse(String userInput, TaskList taskList, Ui ui, Storage storage) {
        String[] input;
        if (userInput.startsWith("find ") || userInput.startsWith("tag ") || userInput.startsWith("filter ")) {
            input = userInput.split(" ", 2);
        } else {
            input = userInput.split(" /", 2);
        }

        String type = input[0].split(" ")[0].toLowerCase();
        String description = input[0].substring(type.length());
        try {
            switch (type) {
                case("bye"):
                    Platform.exit();
                    return Ui.byeMessage();
                case("list"):
                    return Ui.printTaskList(taskList);
                case("mark"):
                    int position = Integer.parseInt(userInput.split(" ")[1]);
                    return markTask(taskList, storage, position);
                case("unmark"):
                    int pos = Integer.parseInt(userInput.split(" ")[1]);
                    return unmarkTask(taskList, storage, pos);
                case("todo"):
                    return createTodo(taskList, storage, description);
                case("deadline"):
                    return createDeadline(taskList, storage, input, description);
                case("event"):
                    return createEvent(taskList, storage, input, description);
                case("delete"):
                    int target = Integer.parseInt(userInput.split(" ")[1]);
                    Task taskToDelete = taskList.get(target - 1);
                    taskList.removeTask(target - 1);
                    storage.save(taskList);
                    return Ui.taskDeleted(taskToDelete, taskList.size());
                case("find"):
                    String keyword = input[1].trim();
                    TaskList output = taskList.findTask(keyword);
                    return Ui.findTask(output);
                case("tag"):
                    String[] tagInput = input[1].split(" ", 2);
                    int tagPos = Integer.parseInt(tagInput[0]);
                    String association = tagInput[1];
                    taskList.get(tagPos - 1).setAssociation(association);
                    return Ui.associationMessage(taskList.get(tagPos - 1), association);
                case("filter"):
                    String taskAssociation = input[1].trim();
                    TaskList result = taskList.filterAssociation(taskAssociation);
                    return Ui.filterTask(result);
                default:
                    return ("ERROR: Invalid Input!");
            }
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    /**
     * creates an event task to add to the tasklist
     * @param taskList tasklist to find the task
     * @param storage storage to save the change
     * @param description name of the task
     * @param input user input to get the start and end
     * @return task created string
     * @throws IOException
     */
    private static String createEvent(TaskList taskList, Storage storage, String[] input, String description) throws IOException {
        try {
            String startTime = input[1].split(" /", 2)[0].split(" ", 2)[1].trim();
            String endTime = input[1].split(" /", 2)[1].split(" ", 2)[1].trim();
            Task event = new Event(description, startTime, endTime);
            taskList.addTask(event);
            storage.save(taskList);
            return Ui.taskAddedMessage(event, taskList.size());
        } catch (ArrayIndexOutOfBoundsException e) {
            return Ui.eventFormatError();
        }
    }

    /**
     * creates a deadline task to add to the tasklist
     * @param taskList tasklist to find the task
     * @param storage storage to save the change
     * @param description name of the task
     * @param input user input to get dueDate
     * @return task created string
     * @throws IOException
     */
    private static String createDeadline(TaskList taskList, Storage storage, String[] input, String description) throws IOException {
        try {
            String dueDate = input[1].split(" ", 2)[1].trim();
            Task deadline = new Deadline(description, dueDate);
            taskList.addTask(deadline);
            storage.save(taskList);
            return Ui.taskAddedMessage(deadline, taskList.size());
        } catch (DateTimeParseException e) {
            return Ui.deadlineDateError();
        } catch (ArrayIndexOutOfBoundsException e) {
            return Ui.deadlineFormatError();
        }
    }

    /**
     * creates a todo task to add to the tasklist
     * @param taskList tasklist to find the task
     * @param storage storage to save the change
     * @param description name of the task
     * @return task created string
     * @throws IOException
     */
    private static String createTodo(TaskList taskList, Storage storage, String description) throws IOException {
        Task todo = new Todo(description);
        taskList.addTask(todo);
        storage.save(taskList);
        return Ui.taskAddedMessage(todo, taskList.size());
    }

    /**
     * unmarks a task in the tasklist
     * @param taskList tasklist to find the task
     * @param storage storage to save the change
     * @param pos position of the task
     * @return the String message to print
     * @throws IOException
     */
    private static String unmarkTask(TaskList taskList, Storage storage, int pos) throws IOException {
        taskList.get(pos - 1).undo();
        storage.save(taskList);
        return Ui.markedUndone(taskList.get(pos - 1));
    }

    /**
     * marks a task as done
     * @param taskList tasklist to find the task
     * @param storage storage to save the change
     * @param position position of the task
     * @return the String message to print
     * @throws IOException
     */
    private static String markTask(TaskList taskList, Storage storage, int position) throws IOException {
        taskList.get(position - 1).markDone();
        storage.save(taskList);
        return Ui.markedDone(taskList.get(position - 1));
    }
}
