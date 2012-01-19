import java.util.HashMap;

public class KeyGen {
    HashMap<String, String> hm;
    String[]                keyArray;
    String                  keys;

    public KeyGen(String keys) {
        this.keys = keys;
    }

    public HashMap<String, String> toHashMap() {
        keyArray = keys.split(";");
        hm       = new HashMap<String, String>();

        int i = 0;

        do {
            hm.put(keyArray[i], keyArray[i + 1]);
            i = i + 2;
        } while (i < keyArray.length - 1);

        return hm;
    }
}

