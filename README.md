# Java Swing To-Do List App

A simple yet feature-rich To-Do List application built using Java and Swing. This project demonstrates the use of core Swing components for building desktop applications and supports flexible input handling and persistent task management.

## Features

- Add tasks with optional category and due date
  - Supports free-form due date input such as:
    - "today"
    - "tomorrow"
    - "next Monday"
    - "24th August"
    - or any custom text
- Mark tasks as completed
- Delete one or multiple selected tasks
- Export tasks to a text file
- Clean and scrollable user interface with task preview
- Persist tasks locally using a text file (tasks are saved between sessions)
- Supports custom categories for organizing tasks
- Dynamic task count: shows total and completed tasks
- Sorts tasks by creation order

## How to Run

Ensure that you have Java (JDK 8 or higher) installed.

### Compile

```bash
javac ToDoApp.java Task.java TaskRenderer.java
```

### Run

```bash
java ToDoApp
```

## Optional Notes

- If you previously used an external library (e.g., Natty) for date parsing, this version does not require it.
- Task data is stored in `tasks.txt` in the same directory as the application.

