package dto;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import config.FlatType;
import config.FormField;
import form.FieldData;

/**
 * A Data Transfer Object (DTO) representing a {@code BTOProject}.
 * <p>
 * This class facilitates communication between the Controller, Service, and Model layers.
 * Changes in the underlying model structure require modifications only to this DTO, improving maintainability.
 * <p>
 * Contains project name, location, flat details, application dates, and officer limit.
 * <p>
 * It includes a factory method {@link #fromFormData(Map)} to create a DTO instance directly from form input.
 */
public class BTOProjectDTO {
    private final String name;
    private final String neighborhood;
    
    private final Map<FlatType, Integer> flatNum;
    private final Map<FlatType, Integer> flatPrice;

    private final LocalDate openingDate;
    private final LocalDate closingDate;
    private final int HDBOfficerLimit;

    /**
     * Constructs a new BTOProjectDTO with the specified details.
     *
     * @param name the name of the BTO project
     * @param neighborhood the neighborhood where the project is located
     * @param flatNum a map of {@code FlatType} to their quantities in this project
     * @param flatPrice a map of {@code FlatType} to their respective prices
     * @param openingDate the date when applications for this project open
     * @param closingDate the date when applications for this project close
     * @param HDBOfficerLimit the available HDB Officer Slots
     * 
     * @see FlatType
     */

    public BTOProjectDTO(String name, String neighborhood, Map<FlatType, Integer> flatNum, Map<FlatType, Integer> flatPrice, LocalDate openingDate, LocalDate closingDate, int HDBOfficerLimit){
        this.name = name;
        this.neighborhood = neighborhood;
        this.flatNum = flatNum;
        this.flatPrice = flatPrice;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.HDBOfficerLimit = HDBOfficerLimit;
    }

    /**
     * Returns the name of the BTO project.
     *
     * @return the project name
     */

    public String getName() {
        return name;
    }

    /**
     * Returns the neighborhood where the BTO project is located.
     *
     * @return the neighborhood name
     */
    public String getNeighborhood() {
        return neighborhood;
    }

    /**
     * Returns a map of flat types to their quantities available in this project.
     *
     * @return a map containing flat type quantities
     */
    public Map<FlatType, Integer> getFlatNum() {
        return flatNum;
    }

    /**
     * Returns a map of flat types to their respective prices in this project.
     *
     * @return a map containing flat type prices
     */
    public Map<FlatType, Integer> getFlatPrice() {
        return flatPrice;
    }

    /**
     * Returns the opening date for applications to this BTO project.
     *
     * @return the application opening date
     */
    public LocalDate getOpeningDate() {
        return openingDate;
    }

    /**
     * Returns the closing date for applications to this BTO project.
     *
     * @return the application closing date
     */
    public LocalDate getClosingDate() {
        return closingDate;
    }

    /**
     * Returns the maximum number of HDB officer slots.
     *
     * @return the HDB officer limit
     */
    public int getHDBOfficerLimit() {
        return HDBOfficerLimit;
    }

    /**
     * Creates a {@code BTOProjectDTO} from the provided form data.
     * <p>
     * This method extracts relevant fields such as project name, neighborhood,
     * flat numbers, flat prices, application period, and HDB officer limit
     * from the form input map, and uses them to populate the DTO.
     *
     * @param data a map of {@code FormField} to {@code FieldData}, representing the user-submitted form values
     * @return a populated {@code BTOProjectDTO} instance
     *
     * @throws ClassCastException if any form data is of unexpected type
     * 
     * @see Form
     * @see FormField
     * @see FieldData
     */
    public static BTOProjectDTO fromFormData(Map<FormField, FieldData<?>> data){
        String name = (String) data.get(FormField.NAME).getData();
        String neighbourhood = (String) data.get(FormField.NEIGHBORHOOD).getData();

        Map<FlatType, Integer> flatNums = new HashMap<>();
        Map<FlatType, Integer> flatPrices = new HashMap<>();

        for(FlatType flatType:FlatType.values()){
            int flatNum = (Integer) data.get(flatType.getNumFormField()).getData();
            flatNums.put(flatType, flatNum);

            int flatPrice = (Integer) data.get(flatType.getPriceFormField()).getData();
            flatPrices.put(flatType, flatPrice);
        }

        LocalDate openingDate = (LocalDate) data.get(FormField.OPENING_DATE).getData();
        LocalDate closingDate = (LocalDate) data.get(FormField.CLOSING_DATE).getData();
        int HDBOfficerLimit = (Integer) data.get(FormField.HBD_OFFICER_LIMIT).getData();

        return new BTOProjectDTO(name, neighbourhood, flatNums, flatPrices, openingDate, closingDate, HDBOfficerLimit);
    }
}
