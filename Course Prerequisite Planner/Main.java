
import java.util.*;
import java.io.*;

public class Main {

    static Scanner sc = new Scanner(System.in);

    // Graph to store courses and prerequisites
    static Map<String, List<String>> graph = new LinkedHashMap<>();

    static final String FILE_NAME = "prerequisites.txt";

    // Add a new course
    static void addCourse(String course) {

        if (graph.containsKey(course)) {
            System.out.println("Course already exists!");
            return;
        }

        graph.put(course, new ArrayList<>());
        System.out.println("Course added successfully!");
    }

    // Add prerequisite edge A -> B
    static void addPrerequisite(String a, String b) {

        if (!graph.containsKey(a) || !graph.containsKey(b)) {
            System.out.println("Invalid course! Please add both courses first.");
            return;
        }

        if (a.equals(b)) {
            System.out.println("A course cannot be its own prerequisite!");
            return;
        }

        if (graph.get(a).contains(b)) {
            System.out.println("Prerequisite already exists!");
            return;
        }

        graph.get(a).add(b);
        System.out.println("Prerequisite added: " + a + " -> " + b);
    }

    // DFS for cycle detection and topological sorting
    static boolean dfs(String course,
                       Map<String, Integer> visited,
                       Stack<String> stack) {

        visited.put(course, 1);

        for (String next : graph.get(course)) {

            int state = visited.getOrDefault(next, 0);

            // Back edge means cycle detected
            if (state == 1) {
                return false;
            }

            if (state == 0) {
                if (!dfs(next, visited, stack)) {
                    return false;
                }
            }
        }

        visited.put(course, 2);

        // Push course after visiting dependencies
        stack.push(course);

        return true;
    }

    // Generate valid course order
    static void courseOrder() {

        Map<String, Integer> visited = new HashMap<>();
        Stack<String> stack = new Stack<>();

        // Initialize all courses as unvisited
        for (String course : graph.keySet()) {
            visited.put(course, 0);
        }

        // Perform DFS
        for (String course : graph.keySet()) {

            if (visited.get(course) == 0) {

                if (!dfs(course, visited, stack)) {
                    System.out.println("\nCycle detected!");
                    System.out.println("No valid course order exists.");
                    return;
                }
            }
        }

        if (graph.isEmpty()) {
            System.out.println("No courses available.");
            return;
        }

        // Pop stack to get topological order
        System.out.println("\nValid Course Order:");

        while (!stack.isEmpty()) {
            System.out.print(stack.pop());

            if (!stack.isEmpty()) {
                System.out.print(" -> ");
            }
        }

        System.out.println();
    }

    // Display all courses and dependencies
    static void displayCourses() {

        if (graph.isEmpty()) {
            System.out.println("No courses available.");
            return;
        }

        System.out.println("\nCourse Prerequisites:");

        for (String course : graph.keySet()) {
            System.out.println(course + " -> " + graph.get(course));
        }
    }

    // Load data from file
    static void loadData() {

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            System.out.println("No previous data found. Starting fresh.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = br.readLine()) != null) {

                String[] parts = line.trim().split("\\s+");

                if (parts.length == 2 && parts[0].equals("COURSE")) {
                    graph.putIfAbsent(parts[1], new ArrayList<>());
                }

                else if (parts.length == 3 && parts[0].equals("EDGE")) {

                    String a = parts[1];
                    String b = parts[2];

                    graph.putIfAbsent(a, new ArrayList<>());
                    graph.putIfAbsent(b, new ArrayList<>());

                    if (!graph.get(a).contains(b)) {
                        graph.get(a).add(b);
                    }
                }
            }

            System.out.println("Data loaded successfully!");

        } catch (IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }

    // Save data to file
    static void saveData() {

        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(FILE_NAME))) {

            for (String course : graph.keySet()) {
                bw.write("COURSE " + course);
                bw.newLine();
            }

            for (String course : graph.keySet()) {

                for (String next : graph.get(course)) {

                    bw.write("EDGE " + course + " " + next);
                    bw.newLine();
                }
            }

            System.out.println("Data saved successfully!");

        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    // Main method
    public static void main(String[] args) {

        loadData();

        while (true) {

            System.out.println("\n===== COURSE PREREQUISITE PLANNER =====");

            System.out.println("1. Add Course");
            System.out.println("2. Add Prerequisite");
            System.out.println("3. Display Courses");
            System.out.println("4. Generate Course Order");
            System.out.println("5. Save Data");
            System.out.println("6. Exit");

            System.out.print("\nEnter your choice: ");

            int choice;

            try {
                choice = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number!");
                sc.nextLine();
                continue;
            }

            sc.nextLine();

            switch (choice) {

                case 1:
                    System.out.print("Enter course name: ");
                    String course = sc.nextLine().trim();

                    if (course.isEmpty() || course.contains(" ")) {
                        System.out.println("Invalid course name!");
                    } else {
                        addCourse(course);
                    }
                    break;

                case 2:
                    System.out.print("Enter prerequisite course (A): ");
                    String a = sc.nextLine().trim();

                    System.out.print("Enter dependent course (B): ");
                    String b = sc.nextLine().trim();

                    addPrerequisite(a, b);
                    break;

                case 3:
                    displayCourses();
                    break;

                case 4:
                    courseOrder();
                    break;

                case 5:
                    saveData();
                    break;

                case 6:
                    saveData();
                    System.out.println("Thank you for using Course Planner!");
                    return;

                default:
                    System.out.println("Invalid choice! Try again.");
            }
        }
    }
}
