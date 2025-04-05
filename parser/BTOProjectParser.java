package parser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.FlatType;
import exception.EnumParsingException;
import exception.ModelParsingException;
import model.BTOProject;
import model.User;
import repository.interfaces.UserRepository;

public class BTOProjectParser {
    public static BTOProject parseBTOProject(List<String> line, UserRepository userRepository){
        String name = line.get(0);
        String neighbourhood = line.get(1);

        List<String> flatTypeString = splitList(line.get(2));
        List<String> flatNumString = splitList(line.get(3));
        List<String> flatPriceString = splitList(line.get(4));

        Map<FlatType, Integer> flatNums = new HashMap<>();
        Map<FlatType, Integer> flatPrices = new HashMap<>();

        try {
            for(int i=0; i<flatTypeString.size(); i++){
                FlatType flatType = FlatType.parseFlatType(flatTypeString.get(i));

                int flatNum = Integer.parseInt(flatNumString.get(i));
                int flatPrice = Integer.parseInt(flatPriceString.get(i));

                flatNums.put(flatType, flatNum);
                flatPrices.put(flatType, flatPrice);
            }
        } catch (EnumParsingException e) {
            throw new ModelParsingException("Invalid flatType: %s".formatted(line.get(2)));
        } catch (Exception e) {
            throw new ModelParsingException("Invalid flatNum: %s or flatPrice: %s. %s".formatted(
                line.get(3), line.get(4), e.getMessage()
            ));
        }

        LocalDate openingDate = null;
        try {
            openingDate = LocalDate.parse(line.get(5));
        } catch (Exception e) {
            throw new ModelParsingException("Invalid Opening Date: %s".formatted(line.get(5)));
        }

        LocalDate closingDate = null;
        try {
            closingDate = LocalDate.parse(line.get(6));
        } catch (Exception e) {
            throw new ModelParsingException("Invalid Closing Date: %s".formatted(line.get(6)));
        }

        String HDBManagerNRIC = line.get(7);
        User HDBManager = null;
        try {
            HDBManager = userRepository.getByNRIC(HDBManagerNRIC);
        } catch (Exception e) {
            throw new ModelParsingException("Internal error: %s".formatted(e.getMessage()));
        }
        if(HDBManager == null) throw new ModelParsingException("HDB Manager not found. NRIC: %s".formatted(line.get(7)));

        int HDBOfficerLimit = 0;
        try {
            HDBOfficerLimit = Integer.parseInt(line.get(8));
        } catch (Exception e) {
            throw new ModelParsingException("Invalid HDB Officer Limit: %s".formatted(line.get(8)));
        }

        BTOProject btoProject = new BTOProject(HDBManager, name, neighbourhood, flatNums, flatPrices, openingDate, closingDate, HDBOfficerLimit);        
        List<String> HDBOfficerNRICs = splitList(line.get(9));

        for(String NRIC:HDBOfficerNRICs){
            try {
                User HDBOfficer = userRepository.getByNRIC(NRIC);
                btoProject.addHDBOfficer(HDBOfficer);
            } catch (Exception e) {
                throw new ModelParsingException("Internal error: %s".formatted(e.getMessage()));
            }
        }

        return btoProject;
    }

    private static List<String> splitList(String s){
        s = s.substring(1, s.length()-1);

        if(s.length() == 0) return List.of();

        String[] values = s.split(",");
        return Arrays.asList(values);
    }

    public static List<String> toListOfString(BTOProject btoProject){
        List<String> list = new ArrayList<>();

        list.add(btoProject.getName());
        list.add(btoProject.getNeighborhood());

        StringBuilder flatTypeString = new StringBuilder("[");
        StringBuilder flatNumString = new StringBuilder("[");
        StringBuilder flatPriceString = new StringBuilder("[");

        for(FlatType flatType:FlatType.values()){
            flatTypeString.append(flatType.getStoredString() + ",");
            flatNumString.append(btoProject.getFlatNum(flatType) + ",");
            flatPriceString.append(btoProject.getFlatPrice(flatType) + ",");
        }   
        
        flatTypeString.setCharAt(flatTypeString.length() - 1, ']');
        flatNumString.setCharAt(flatNumString.length() - 1, ']');
        flatPriceString.setCharAt(flatPriceString.length() - 1, ']');

        list.add(flatTypeString.toString());
        list.add(flatNumString.toString());
        list.add(flatPriceString.toString());

        list.add(btoProject.getOpeningDate().toString());
        list.add(btoProject.getClosingDate().toString());
        
        list.add(btoProject.getHDBManager().getNRIC());
        list.add(Integer.toString(btoProject.getHDBOfficerLimit()));

        StringBuilder HDBOfficerNRICs = new StringBuilder("[");
        for(User HDBOfficer:btoProject.getHDBOfficers()){
            HDBOfficerNRICs.append(HDBOfficer.getNRIC() + ",");
        }
        if(HDBOfficerNRICs.length() != 1){
            HDBOfficerNRICs.setLength(HDBOfficerNRICs.length() - 1);
        }
        HDBOfficerNRICs.append(']');
        
        list.add(HDBOfficerNRICs.toString());
        
        return list;
    }
}
