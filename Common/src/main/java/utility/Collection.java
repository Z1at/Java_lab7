package utility;

import data.*;

import java.util.*;
import java.util.LinkedHashMap;

public class Collection {
    public final LinkedHashMap<String, City> collection;
    public final TreeMap<String, Vector<String>> creators;
    public final String initTime;
    public int id = 0;

    public Collection() {
        collection = new LinkedHashMap<>();
        initTime = new Date().toString();
        creators = new TreeMap<>();
    }
}
