# SC2002-Group-Project

/**
 * Default implementation of the {@link OfficerRegistrationController} interface.
 * Handles user interaction and delegates officer registration logic to services and views.
 * For example, in the addOfficerRegistration() method of this class, it calls on the addOfficerRegistration() method of the officerRegistrationService class
 * This follows:
 * 1. Separation of Concerns (SoC):
 * - Controller: handles coordination.
 * - Service: contains business logic.
 * - View: handles presentation/output.
 * - Model: represents the data.
 * The controller’s main job is to coordinate — it's like the traffic cop or orchestra conductor 
 * that directs the flow between the user interface (view) and the business logic (service).
 */

/**
     * Instead of doing this for our constructor:
     * public class DefaultOfficerRegistrationController {
     * private OfficerRegistrationService officerRegistrationService = new OfficerRegistrationService(); // tightly coupled
     * private OfficerRegistrationView officerRegistrationView = new OfficerRegistrationView(); },
     * We do:
     * public DefaultOfficerRegistrationController(OfficerRegistrationService officerRegistrationService, OfficerRegistrationView officerRegistrationView, SessionManager sessionManager, MenuManager menuManager, MessageView messageView){
        super(messageView);
     * This follows Inversion of Control (IoC) — the control of creating objects is moved outside of this class.
     * This is dependency injection (DI) because we’re passing in services and views via the constructor
     */

    /**
     * Adds a new officer registration for the currently logged-in officer to a specified BTO project.
     *
     * @param btoProject the BTO project to register to
     */
=======
## Title: BTO System

This project simulates a Build-To-Order (BTO) system, similar to the real-world HDB BTO process. It allows **applicants** to apply for housing projects, and provides **HDB officers** and **managers** tools to manage these projects.  
The system is written entirely in **plain Java**, without the use of any external libraries, frameworks, or databases.

---

## Key Design Features

1. **Adherence to SOLID Principles**  
   The system is structured according to the five SOLID object-oriented design principles to ensure maintainability, scalability, and clarity.

2. **MVC Architecture**  
   Implements the Model-View-Controller pattern to separate application logic, user interface, and data handling responsibilities.

3. **Command Pattern**  
   Encapsulates requests as objects, allowing flexible command processing, queuing, and undo operations if needed.

4. **Manual Dependency Injection**  
   A custom dependency injector is implemented to handle object instantiation and wiring without relying on external frameworks.

5. **In-Memory Data Manager**  
   Acts like a lightweight database—data is managed in memory during runtime and persisted to files for loading and saving.

---

## How to Run

1. Ensure you are using **Java 9 or later**.  
2. Clone the repository:
   ```
   git clone https://github.com/julia237500/SC2002-Group-Project
   ```
3. Navigate to the project folder and compile & run `Main.java`:
   ```
   javac Main.java
   java Main
   ```

---

## License

This project is licensed under the [MIT License](LICENSE).

---

## Authors

- Feng Yuhao  
- Htoo Myat Noe  
- Lim Yi Xuan  
- Ong Jason (Wang Juncheng)  
- Venugopal Prabhu Harshini  
- Yong Chee Seng  