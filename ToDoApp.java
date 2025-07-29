import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class ToDoApp extends JFrame {
    private DefaultListModel<Task> allTasksModel = new DefaultListModel<>();
    private DefaultListModel<Task> filteredTasksModel = new DefaultListModel<>();
    private JList<Task> taskList;
    private JTextField taskField, dateField;
    private JComboBox<String> categoryCombo, filterCombo;
    private JButton addButton, deleteButton, exportButton;
    private JLabel statusLabel;
    private static final String SAVE_FILE = "tasks.dat";

    public ToDoApp() {
        super("To-Do List App");
        setSize(520, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10,10));

        // Top panel: inputs + filter
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JPanel inputPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        taskField = new JTextField();
        dateField = new JTextField(); // Blank for free text
        categoryCombo = new JComboBox<>(new String[]{"Work", "Personal", "Urgent", "Other"});
        addButton = new JButton("Add Task");

        JPanel row1 = new JPanel(new BorderLayout(5, 5));
        row1.add(new JLabel("Task:"), BorderLayout.WEST);
        row1.add(taskField, BorderLayout.CENTER);

        JPanel row2 = new JPanel(new BorderLayout(5, 5));
        row2.add(new JLabel("Due Date:"), BorderLayout.WEST);
        row2.add(dateField, BorderLayout.CENTER);

        JPanel row3 = new JPanel(new BorderLayout(5, 5));
        row3.add(new JLabel("Category:"), BorderLayout.WEST);
        row3.add(categoryCombo, BorderLayout.CENTER);
        row3.add(addButton, BorderLayout.EAST);

        inputPanel.add(row1);
        inputPanel.add(row2);
        inputPanel.add(row3);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter:"));
        filterCombo = new JComboBox<>(new String[]{"All", "Completed", "Incomplete"});
        filterPanel.add(filterCombo);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        topPanel.add(inputPanel);
        topPanel.add(filterPanel);

        add(topPanel, BorderLayout.NORTH);

        // Task list
        taskList = new JList<>(filteredTasksModel);
        taskList.setCellRenderer(new TaskRenderer());
        taskList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with buttons and status
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        deleteButton = new JButton("Delete Selected");
        exportButton = new JButton("Export Tasks");
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(exportButton);

        statusLabel = new JLabel(" ");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        bottomPanel.add(buttonsPanel, BorderLayout.NORTH);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(bottomPanel, BorderLayout.SOUTH);

        // Load tasks and initialize UI
        loadTasksFromFile();
        filterTasks();
        updateStatusLabel();

        // Listeners
        addButton.addActionListener(e -> addTask());
        deleteButton.addActionListener(e -> deleteSelectedTasks());
        exportButton.addActionListener(e -> exportTasksToFile());

        taskList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int index = taskList.locationToIndex(e.getPoint());
                if (index != -1) {
                    Task task = filteredTasksModel.get(index);
                    task.toggleDone();
                    taskList.repaint();
                    updateStatusLabel();
                }
            }
        });

        filterCombo.addActionListener(e -> {
            filterTasks();
            updateStatusLabel();
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                saveTasksToFile();
            }
        });

        setVisible(true);
    }

    private void addTask() {
        String taskText = taskField.getText().trim();
        String dateText = dateField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();

        if (taskText.isEmpty() || dateText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Task and due date cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Task task = new Task(taskText, false, dateText, category);
        allTasksModel.addElement(task);
        filterTasks();

        taskField.setText("");
        dateField.setText("");
        categoryCombo.setSelectedIndex(0);
        updateStatusLabel();
    }

    private void deleteSelectedTasks() {
        int[] selectedIndices = taskList.getSelectedIndices();
        if (selectedIndices.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select one or more tasks to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Remove from allTasksModel all selected tasks
        for (int i = selectedIndices.length - 1; i >= 0; i--) {
            Task taskToRemove = filteredTasksModel.get(selectedIndices[i]);
            allTasksModel.removeElement(taskToRemove);
        }
        filterTasks();
        updateStatusLabel();
    }

    private void filterTasks() {
        filteredTasksModel.clear();
        String filter = (String) filterCombo.getSelectedItem();
        for (int i = 0; i < allTasksModel.size(); i++) {
            Task t = allTasksModel.get(i);
            boolean include;
            switch (filter) {
                case "Completed": include = t.isDone(); break;
                case "Incomplete": include = !t.isDone(); break;
                default: include = true;
            }
            if (include) filteredTasksModel.addElement(t);
        }
    }

    private void updateStatusLabel() {
        int done = 0, total = allTasksModel.size();
        for (int i = 0; i < total; i++) {
            if (allTasksModel.get(i).isDone()) done++;
        }
        statusLabel.setText(done + " of " + total + " tasks completed");
    }

    private void exportTasksToFile() {
        try (PrintWriter writer = new PrintWriter("exported_tasks.txt")) {
            for (int i = 0; i < filteredTasksModel.size(); i++) {
                Task task = filteredTasksModel.get(i);
                writer.println((task.isDone() ? "[X]" : "[ ]") + " " + task.getName()
                        + " | Due: " + task.getDueDate() + " | Category: " + task.getCategory());
            }
            JOptionPane.showMessageDialog(this, "Tasks exported to 'exported_tasks.txt'");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to export tasks", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveTasksToFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            java.util.List<Task> tasks = Collections.list(allTasksModel.elements());
            out.writeObject(tasks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTasksFromFile() {
        File file = new File(SAVE_FILE);
        if (file.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                java.util.List<Task> tasks = (java.util.List<Task>) in.readObject();
                for (Task task : tasks) {
                    allTasksModel.addElement(task);
                }
            } catch (Exception e) {
                e.printStackTrace();
                file.delete();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ToDoApp::new);
    }
}
