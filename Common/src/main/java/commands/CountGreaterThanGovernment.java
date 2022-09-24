package commands;

import data.Government;
import utility.Collection;
import utility.TextFormatting;

public class CountGreaterThanGovernment {
    public int countGreaterThanGovernment(Government government, Collection collection) {
        int counter = 0;
        for (String key : collection.collection.keySet()) {
            if (collection.collection.get(key).getGovernment().compareTo(government) > 0) {
                counter++;
            }
        }
        return counter;
    }
}
