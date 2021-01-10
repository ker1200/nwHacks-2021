package main.ui;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.view.CalendarView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.model.Class;
import main.model.ClassList;
import main.model.GradedItem;
import main.model.ToDoList;
import main.persistence.JsonReader;
import main.persistence.JsonWriter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;

public class UI extends Application {
    private ClassList classlist;
    private ListView<String> list;
    private ObservableList<String> tasksDateSorted;
    private ObservableList<String> items;
    private LocalDate dateIn;
    private final static int MAX_IMPORTANCE = 10;
    private CalendarView calendarView;
    private JsonWriter writer;
    private JsonReader reader;

    private void setup() {
        classlist = new ClassList();
        list = new ListView<String>();
        tasksDateSorted = FXCollections.observableArrayList();
        items = FXCollections.observableArrayList();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        setup();

        primaryStage.setTitle("The Scheduler");

        Group todoGroup = new Group();
        Group menuGroup = new Group();

        final Random rng = new Random();
        VBox taskBox = new VBox(5);
        VBox toDoBox = new VBox(5);

        ListView<String> taskList = new ListView<String>();

        ScrollPane toDoList = new ScrollPane(toDoBox);
        //toDoList.setFitToWidth(true);

        createCalendar();

        Button addButton = new Button("Add");
        addButton.setOnAction(e -> {
            AnchorPane anchorPane = new AnchorPane();
            String style = String.format("-fx-background: rgb(%d, %d, %d);" + "-fx-background-color: -fx-background;",
                    rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));

            anchorPane.setStyle(style);
            Label label = new Label("Pane "+(taskBox.getChildren().size()+1));
            AnchorPane.setLeftAnchor(label, 5.0);
            AnchorPane.setTopAnchor(label, 5.0);
            Button button = new Button("Remove");
            button.setOnAction(evt -> taskBox.getChildren().remove(anchorPane));
            AnchorPane.setRightAnchor(button, 5.0);
            AnchorPane.setTopAnchor(button, 5.0);
            AnchorPane.setBottomAnchor(button, 5.0);
            anchorPane.getChildren().addAll(label, button);
            taskBox.getChildren().add(anchorPane);
        });

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            writer =  new JsonWriter();
            writer.writeClassListToJson(classlist);
        });

        Button loadButton = new Button("Load");
        loadButton.setOnAction(e -> {
            reader = new JsonReader();
            reader.readClassListFromJson();
        });

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> {
            taskList.getItems().clear();
            for (Class c : classlist.getClasslist()) {
                List<GradedItem> tasks = c.getTasks();
                for (GradedItem i : tasks) {
                    tasksDateSorted.add(i.getName() + "\t" + i.getDate().toString());
                }
            }
            taskList.setItems(tasksDateSorted);
        });

        Button refreshToDo = new Button("ToDo");
        refreshToDo.setOnAction(e -> {
            toDoBox.getChildren().clear();
            List<GradedItem> toDo = ToDoList.getToDoList(classlist);
            AnchorPane anchorPane = new AnchorPane();
            String style = String.format("-fx-background: rgb(%d, %d, %d);" + "-fx-background-color: -fx-background;",
                    173, 173, 173);
            anchorPane.setStyle(style);
            for (int i = 0; i < toDo.size(); i++){
                Label label = new Label(toDo.get(i).getName() + "\t" + toDo.get(i).getDate());
                //change font or something
                AnchorPane.setLeftAnchor(label, 5.0);
                AnchorPane.setTopAnchor(label, 5.0 + 20*i);
                anchorPane.getChildren().addAll(label);
            }
            toDoBox.getChildren().add(anchorPane);
        });

        Button addClass = new Button();
        addClass.setText("Add Class");
        addClass.setOnAction(actionEvent -> {
            GridPane grid = new GridPane();
            grid.setAlignment(Pos.TOP_LEFT);
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(25, 25, 25, 25));

            Label codePrompt = new Label("Class Code:");
            grid.add(codePrompt, 0, 0, 20, 20);

            TextField codeIn = new TextField();
            grid.add(codeIn, 15, 0, 20, 20);

            Label importancePrompt = new Label("Importance:");
            grid.add(importancePrompt, 0, 5, 20, 20);

            TextField importanceIn = new TextField();
            grid.add(importanceIn, 15, 5, 20, 20);

            Button submit = new Button();
            submit.setText(" Submit ");
            grid.add(submit, 28, 10, 20, 20);

            Scene secondScene = new Scene(grid, 400, 300);

            // New window (Stage)
            Stage newWindow = new Stage();
            newWindow.setTitle("Add Class");
            newWindow.setScene(secondScene);

            // Specifies the modality for new window.
            newWindow.initModality(Modality.WINDOW_MODAL);

            // Specifies the owner Window (parent) for new window
            newWindow.initOwner(primaryStage);

            // Set position of second window, related to primary window.
            newWindow.setX(primaryStage.getX() + 200);
            newWindow.setY(primaryStage.getY() + 100);

            newWindow.show();

            submit.setOnAction(actionEvent1 -> {
                if (!classlist.contains(codeIn.getCharacters().toString())) {
                    classlist.addClass(new Class(codeIn.getCharacters().toString(),
                            (Integer.parseInt(importanceIn.getCharacters().toString()) < 0) ? 0 :
                                    Math.min(Integer.parseInt(importanceIn.getCharacters().toString()), MAX_IMPORTANCE)));

                    AnchorPane anchorPane = new AnchorPane();
                    String style = String.format("-fx-background: rgb(%d, %d, %d);" + "-fx-background-color: -fx-background;",
                            rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));

                    anchorPane.setStyle(style);
                    Label label = new Label(classlist.getClass(taskBox.getChildren().size()).getClassCode());
                    AnchorPane.setLeftAnchor(label, 5.0);
                    AnchorPane.setTopAnchor(label, 5.0);

                    Button removeButton = new Button("Remove");
                    removeButton.setOnAction(evt -> taskBox.getChildren().remove(anchorPane));
                    AnchorPane.setRightAnchor(removeButton, 5.0);
                    AnchorPane.setTopAnchor(removeButton, 5.0);
                    AnchorPane.setBottomAnchor(removeButton, 5.0);

                    Button addAssignment = createAssignmentButton(primaryStage);

                    AnchorPane.setRightAnchor(addAssignment, 75.0);
                    AnchorPane.setTopAnchor(addAssignment, 5.0);
                    AnchorPane.setBottomAnchor(addAssignment, 5.0);
                    anchorPane.getChildren().addAll(label, removeButton, addAssignment);
                    taskBox.getChildren().add(anchorPane);
                } else {
                    createOkayWindow(primaryStage, "That class already exists!");
                }

                newWindow.close();
            });

        });

        GridPane buttons = new GridPane();
        buttons.add(addClass,0,0,3,3);
        buttons.add(refreshToDo, 0,3,3,3);

        buttons.add(saveButton, 0, 9, 3, 3);
        buttons.add(loadButton, 0, 12, 3, 3);
      
        buttons.add(refreshButton, 0, 6, 3, 3);
        GridPane rootPane = new GridPane();
        GridPane.setConstraints(buttons, 0, 0);
        GridPane.setConstraints(taskBox, 1, 0);
        GridPane.setConstraints(toDoBox, 6, 0);
        GridPane.setConstraints(taskList, 9, 0);
        ColumnConstraints column1 = new ColumnConstraints();
        rootPane.getColumnConstraints().add(new ColumnConstraints(100)); // column 0 is 100 wide
        rootPane.getColumnConstraints().add(new ColumnConstraints(800)); // column 1 is 800 wide

        column1.setPercentWidth(50);

        taskList.setItems(tasksDateSorted);

        rootPane.getChildren().addAll(buttons, taskBox, toDoBox, taskList);


        //when the scene is created, it should just render all the groups

        Scene scene = new Scene(rootPane, 1600, 900);

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void createCalendar() {
        calendarView = new CalendarView();

        Calendar taskCalendar = new Calendar("Tasks");

        taskCalendar.setStyle(Calendar.Style.STYLE1);

        CalendarSource myCalendarSource = new CalendarSource("My Calendars");
        myCalendarSource.getCalendars().add(taskCalendar);

        calendarView.getCalendarSources().add(myCalendarSource);

        calendarView.setRequestedTime(LocalTime.now());

        Thread updateTimeThread = new Thread("Calendar: Update Time Thread") {
            @Override
            public void run() {
                while (true) {
                    Platform.runLater(() -> {
                        calendarView.setToday(LocalDate.now());
                        calendarView.setTime(LocalTime.now());
                    });

                    try {
                        // update every 10 seconds
                        sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            };
        };

        updateTimeThread.setPriority(Thread.MIN_PRIORITY);
        updateTimeThread.setDaemon(true);
        updateTimeThread.start();
    }

    private Button createAssignmentButton(Stage primaryStage) {
        Button addAssignment = new Button("Add Assignment");
        addAssignment.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                GridPane grid = new GridPane();
                grid.setAlignment(Pos.TOP_LEFT);
                grid.setHgap(5);
                grid.setVgap(5);
                grid.setPadding(new Insets(25, 25, 25, 25));

                Label namePrompt = new Label("Assignment Name:");
                grid.add(namePrompt, 0, 0, 20, 20);

                TextField nameIn = new TextField();
                grid.add(nameIn, 35, 0,20,20);

                Label weightPrompt = new Label("Weight (0-100): ");
                grid.add(weightPrompt, 0, 5, 20, 20);

                TextField weightIn = new TextField();
                grid.add(weightIn, 35, 5, 20, 20);

                Label datePrompt = new Label("Due Date: ");
                grid.add(datePrompt, 0, 10, 20, 20);

                final DatePicker datePicker = new DatePicker();
                datePicker.setOnAction(new EventHandler() {
                    public void handle(Event t) {
                        dateIn = datePicker.getValue();
                    }
                });
                grid.add(datePicker, 35, 5, 20, 30);

                Label timePrompt = new Label("Due Time:");
                grid.add(timePrompt, 0, 20, 20, 20);

                Label timeSpec = new Label("24-HOUR, HHMM");
                grid.add(timeSpec, 0, 25, 20, 20);

                TextField timeIn = new TextField();
                grid.add(timeIn, 35, 20, 20, 20);

                Button submitAssignment = new Button();
                submitAssignment.setText(" Submit ");
                grid.add(submitAssignment, 50,40,20,20);

                Scene secondScene = new Scene(grid, 400, 300);

                // New window (Stage)
                Stage newWindow = new Stage();
                newWindow.setTitle("Add Class");
                newWindow.setScene(secondScene);

                // Specifies the modality for new window.
                newWindow.initModality(Modality.WINDOW_MODAL);

                // Specifies the owner Window (parent) for new window
                newWindow.initOwner(primaryStage);

                // Set position of second window, related to primary window.
                newWindow.setX(primaryStage.getX() + 200);
                newWindow.setY(primaryStage.getY() + 100);

                newWindow.show();

                submitAssignment.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        //should add a check if weight field is actually a parseable double.
                        String hour = timeIn.getCharacters().toString().substring(0,1);
                        String min = timeIn.getCharacters().toString().substring(2,3);
                        int hourInt = Integer.parseInt(hour);
                        int minInt = Integer.parseInt(min);

                        classlist.getClass(0).addTask(
                                new GradedItem(nameIn.getCharacters().toString(), dateIn.atTime(hourInt, minInt),
                                        Double.parseDouble(weightIn.getCharacters().toString())));
                        newWindow.close();
                    }
                } );
            }
        } );
        return addAssignment;
    }

    private void createOkayWindow (Stage primaryStage, String text) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label namePrompt = new Label(text);
        grid.add(namePrompt, 0, 0, 20, 20);

        Button okay = new Button();
        okay.setText(" OK ");
        grid.add(okay, 28,10,20,20);

        Scene okScene = new Scene(grid, 400, 300);

        // Okay window (Stage)
        Stage okWindow = new Stage();
        okWindow.setTitle("Add Class");
        okWindow.setScene(okScene);

        // Specifies the modality for new window.
        okWindow.initModality(Modality.WINDOW_MODAL);

        // Specifies the owner Window (parent) for new window
        okWindow.initOwner(primaryStage);

        // Set position of second window, related to primary window.
        okWindow.setX(primaryStage.getX() + 200);
        okWindow.setY(primaryStage.getY() + 100);

        okWindow.show();

        okay.setOnAction(e -> {
            okWindow.close();
        });
    }

    private Button refreshToDoList(Stage primaryStage){
        return null;
    }

}
