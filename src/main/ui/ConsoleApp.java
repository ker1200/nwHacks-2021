package main.ui;

import main.model.Class;
import main.model.ClassList;
import main.model.GradedItem;
import main.model.ToDoList;
import main.persistence.JsonWriter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ConsoleApp {
    private ClassList classlist;
    private ArrayList<GradedItem> todolist;
    private Scanner input;
    private String user;


    public ConsoleApp() {
        runConsoleApp();
    }

    private void setup() {
        classlist = new ClassList();
        input = new Scanner(System.in);

        System.out.println("\nGreetings new user! Thanks for choosing to use this program.");
        System.out.println("\nTo get started, please enter your username: ");

        user = input.next();
    }

    public void runConsoleApp() {
        boolean keepRunning = true;
        String command;

        setup();

        while (keepRunning) {
            displayMainMenu();
            command = input.next();
            command = command.toLowerCase();
            input.nextLine();

            if (command.equals("exit")) {
                keepRunning = false;
            } else {
                processMainCommand(command);
            }
        }
    }

    public void processMainCommand(String command) {
        switch (command) {
            case "add":
                doAdd();
                break;

            case "remove":
                doRemove();
                break;

            case "edit":
                doEdit();
                break;

            case "view":
                doView(classlist);
                break;

            case "sort":
                doSort(classlist);
                break;

            case "view to-do-list":
                doViewToDo(todolist);
                break;

            case "save":
                doSave();
        }
    }

    public void displayMainMenu() {
        System.out.println("\n Welcome, " + user);
        System.out.println("\t add - add new class");
        System.out.println("\t remove - remove a class");
        System.out.println("\t edit - edit a class");
        System.out.println("\t view - view classes");
        System.out.println("\t save - save classes");
        System.out.println("\t load - load classes");
        System.out.println("\t exit - quit app");
    }

    public void doAdd() {
        System.out.println("Enter class code: ");
        String code = input.next();
        System.out.println("Enter importance of class (0 to 10): ");
        int importance = input.nextInt();

        classlist.addClass(new Class(code, importance));
    }

    public void doRemove() {
        System.out.println("Enter class code: ");
        String code = input.next();

        classlist.removeClass(code);
    }

    public void doEdit() {
        System.out.println("Enter class: ");
        String code = input.next();
        System.out.println("Enter assignment/exam: ");
        String name = input.next();
        System.out.println("Enter due date + time (YYYY-MM-DD HH:MM): ");
        String due = input.next();
        System.out.println("Enter weight: ");
        int weight = Integer.parseInt(input.next());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(due, formatter);

        Class selected = classlist.findClass(code);
        selected.addTask(new GradedItem(name, dateTime, weight));
    }

    public void doView(ClassList list) {
        for (Class c : list.getClasslist()) {
            System.out.println("\nClass code: " + c.getClassCode());
            System.out.println("\tTasks: " + c.getTasks());
            System.out.println("\tImportance: " + c.getImp());
            System.out.println("\n");
        }
    }

    public void doSort(ClassList list) {
        ArrayList<GradedItem> sorted = ToDoList.getToDoList(list);
        sorted.get(0);
    }

    public void doViewToDo(ArrayList<GradedItem> list) {
        for (GradedItem gi : list) {
            System.out.println("\n" + gi.getName());
            System.out.println("\tDue date: " + gi.getDate());
        }
    }

    public void doSave() {
        JsonWriter saver = new JsonWriter();

        saver.writeClassListToJson(classlist);
    }

    public static void main(String[] args) {
        new ConsoleApp();
    }
}
