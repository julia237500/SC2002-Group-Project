package dto;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import config.FlatType;

/**
 * This class encapsulates all relevant information about a BTO housing project,
 * including its name, location, flat details, application dates, and officer limit.
 */

public class BTOProjectDTO {
    private String name;
    private String neighborhood;
    
    private Map<FlatType, Integer> flatNum = new HashMap<>();
    private Map<FlatType, Integer> flatPrice = new HashMap<>();

    private LocalDate openingDate;
    private LocalDate closingDate;
    private int HDBOfficerLimit;

    /**
     * Constructs a new BTOProjectDTO with the specified details.
     *
     * @param name the name of the BTO project
     * @param neighborhood the neighborhood where the project is located
     * @param flatNum a map of flat types to their quantities available in this project
     * @param flatPrice a map of flat types to their respective prices
     * @param openingDate the date when applications for this project open
     * @param closingDate the date when applications for this project close
     * @param HDBOfficerLimit the available HDB Officer Slots (max 10)
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
}
