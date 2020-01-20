package utils;

import java.util.HashMap;
import java.util.Map;


/**
 * This is simple Map container for saving and sharing data through program.
 * If you need to store some data and  use it later in another place - you may use this Singleton Map.
 *
 * Usage:
 *
 * To save data :
 * DataProvider.getInstance().saveData("some_your_key", data_to_save_object);
 *
 * To get stored data by key: (@NullPointerException may be thrown if no data presented on defined key)
 * DataProvider.getInstance().getByKey("some_your_key");
 *
 * To get all stored data:
 * DataProvider.getInstance().getMap();
 */

public class DataProvider {

    private static DataProvider ourInstance = new DataProvider();

    public static DataProvider getInstance() {
        return ourInstance;
    }

    private Map<String, Object> map;

    private DataProvider() {
        map = new HashMap<>();
    }

    public void saveData(String key, Object value) {
        map.put(key, value);
    }

    public Object getByKey(String key){
        return map.get(key);
    }

    public Map<String, Object> getMap() {
        return map;
    }
}
