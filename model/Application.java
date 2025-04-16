package model;

import java.time.LocalDateTime;
import java.util.UUID;

import config.ApplicationStatus;
import config.FlatType;
import exception.DataModelException;

/**
 * A model class in Java is a class that represents data — usually real-world objects or database records. 
 * It's part of the MVC pattern (Model-View-Controller).
 * In our application,
 * Application, User, BTOProject are all model classes because they represent entities in our system.
 * They store data: e.g., String uuid, FlatType flatType
 * They are not responsible for displaying info (view) or controlling the program flow (controller)
 * They mainly only have getters (and setters if needed)
 * They may have validation logic, like checking flat eligibility 
 */

/**
 * Represents a BTO flat application submitted by a user.
 * Each application contains information about the applicant, the project,
 * the flat type, application status, and its creation timestamp.
 */
public class Application implements DataModel{
    /**
     * @CSVField is an annotation that is custom and used for mapping the fields of the object to columns in a CSV file 
     * (like Excel but in plain text).
     * So, @CSVField(index = 0) means: “This field (uuid) should be at the first column when saving/loading the object as a CSV row.”
     */
    @CSVField(index = 0)
    private String uuid;

    /**
     * In model classes, a foreign key is a reference to another object.
     * A foreign key is a field in a data model that links to another model — it forms a relationship between two entities.
     * Separation of Concerns:
     * - Using foreign keys ensures that:
     * - Application doesn’t mix in logic for how users or projects work
     * - It just links to those other concerns
     * - User, BTOProject, and Application are decoupled, and can evolve independently
     * - This keeps the code modular and easy to maintain.
     * Single Responsibility Principle:
     * - We're keeping Application focused on its job:
     * - It doesn't store raw data like user names or project names
     * - It stores object references, leaving the User and BTOProject classes to manage their own data
     * - Each class does only one thing and is responsible for its own logic and data.
     */
    @CSVField(index = 1, foreignKey = true)
    private User applicant;

    @CSVField(index = 2, foreignKey = true)
    private BTOProject btoProject;

    @CSVField(index = 3)
    private FlatType flatType;

    @CSVField(index = 4)
    private ApplicationStatus applicationStatus;

    @CSVField(index = 5)
    private boolean isWithdrawing;

    @CSVField(index = 6)
    private LocalDateTime createdAt;

    /**
     * Default no-argument constructor for reflective instantiation.
     * Useful in other classes like CSVDataManager (@see CSVDataManager) where we use reflection 
     * to instantiate model classes (like User, Application, BTOProject etc.) 
     * from CSV data without hardcoding how each object is created.
     * annotated with @SuppressWarnings("unused") — tells the compiler “yes I know it’s unused, please don’t complain”
     */
    @SuppressWarnings("unused")
    private Application(){}

    /**
     * Constructs a new {@code Application} with the specified applicant,
     * BTO project, and flat type. Performs eligibility checks.
     *
     * @param applicant the user applying for a flat
     * @param btoProject the BTO project applied for
     * @param flatType the type of flat applied for
     */
    public Application(User applicant, BTOProject btoProject, FlatType flatType){
        checkFlatTypeEligibility(applicant, btoProject, flatType);

        this.applicant = applicant;
        this.btoProject = btoProject;
        this.flatType = flatType;

        UUID uuid = UUID.randomUUID();
        this.uuid = uuid.toString();

        this.applicationStatus = ApplicationStatus.PENDING;
        this.isWithdrawing = false;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Returns the primary key (UUID) of the application.
     * A UUID stands for Universally Unique Identifier.
     * It's a 128-bit number that is guaranteed to be unique, often used as a primary key (getPK() in this class).
     * In our application, UUID is used as the ID for the application 
     * so it can be stored, retrieved, or referenced without confusion.
     * @return the UUID as a string
     */
    @Override
    public String getPK() {
        return uuid;
    }

    /**
     * Gets the applicant (user) who submitted the application.
     *
     * @return the applicant
     */
    public User getApplicant() {
        return applicant;
    }

    /**
     * Gets the BTO project applied for.
     *
     * @return the BTO project
     */
    public BTOProject getBtoProject() {
        return btoProject;
    }

    
    /**
     * Gets the flat type applied for.
     *
     * @return the flat type
     */
    public FlatType getFlatType() {
        return flatType;
    }

    /**
     * Gets the current status of the application.
     *
     * @return the application status
     */
    public ApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    /**
     * Gets the timestamp of when the application was created.
     *
     * @return the creation date and time
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Checks if the user is eligible to apply for the given flat type in the BTO project.
     *
     * @param applicant the applicant user
     * @param btoProject the BTO project
     * @param flatType the flat type
     * @throws DataModelException if the flat type is unavailable or the user is not eligible
     */
    private void checkFlatTypeEligibility(User applicant, BTOProject btoProject, FlatType flatType) {
        if(!btoProject.hasAvailableFlats(flatType)){
            throw new DataModelException("%s is not available for the project %s".formatted(flatType.getStoredString(), btoProject.getName()));
        }

        if(!flatType.isEligible(applicant)){
            throw new DataModelException("You are not eligible to apply for %s".formatted(flatType.getStoredString()));
        }
    }
}

/**
 * Here are the following design principles for this class:
 * 1. Single Responsibility Principle (SRP)
 * - The Application class is focused only on application-related data and rules:
 * - Storing the applicant and their choices
 * - Validating eligibility
 * - Managing the application status
 * - It doesn't handle user interface, persistence, or command logic — just the domain rules of applying for a BTO flat.
 * 2. Encapsulation: "Hide internal logic and expose only what’s necessary."
 * - The fields are private; exposing data only through getters
 * The UUID is auto-generated — users can’t (and shouldn’t) manually assign it
 * Business logic like checkFlatTypeEligibility is internal, not exposed externally
 * This keeps the object safe and predictable.
 * 3. Separation of Concerns (part of MVC):
 * - The Application class is in the Model layer — its job is to:
 * - Represent application data
 * - Contain business logic (e.g., eligibility checking)
 * - It doesn’t deal with: How data is saved (CSVDataManager handles that), How users input data (View/Controller handle that), Menu or command logic
 * - We are cleanly separating what the data is from how it’s used.
 * 4. Law of Demeter (Principle of Least Knowledge):
 * - The Application class isn’t directly calling other unrelated classes. It interacts with: User (applicant), BTOProject, FlatType
 * - It doesn't go off accessing unrelated services or utilities.
 * 5. Robust Domain Modeling:
 * - The class also uses:
 * - Enums (ApplicationStatus, FlatType) to limit possible values
 * - UUID for unique identification
 * - Validations to ensure only correct applications are created
 * - This makes the class self-validating and hard to misuse.
 */