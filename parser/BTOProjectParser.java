package parser;

import java.time.LocalDate;
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

        return new BTOProject(HDBManager, name, neighbourhood, flatNums, flatPrices, openingDate, closingDate, HDBOfficerLimit);
    }

    private static List<String> splitList(String s){
        s = s.substring(1, s.length()-1);
        String[] values = s.split(",");
        return Arrays.asList(values);
    }
}
