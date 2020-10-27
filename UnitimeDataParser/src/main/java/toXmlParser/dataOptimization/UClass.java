package toXmlParser.dataOptimization;

import com.jamesmurty.utils.XMLBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UClass {
    String type = null;
    List<UClass> children = new ArrayList<>();
    int capacity = 0;
    int voor_id;
    List<Integer> group_id = new ArrayList<>();

    public static UClass makeChild(String type, int voor_id, List<Integer> groip_id) {
        UClass uClass = new UClass();
        uClass.type = type;
        uClass.voor_id = voor_id;
        uClass.group_id = new ArrayList<>(groip_id);
        return uClass;
    }

    public boolean setdataInUnitimeFormat(String type, String strCapacity) {
        HashMap<String,String> types = new HashMap<>();
        types.put("loeng","Lec");
        types.put("praktikum","Lab");
        types.put("harjutus","Rec");
        String[] classes = type.split("/+");
        String className=null;
        for(String uclass:classes) {
            className = types.get(uclass);
            if (className == null) return false;
            if (type == null) type=className;
            else children.add(makeChild(type,voor_id,group_id));
        }
        return setCapacity(strCapacity);
    }

    private boolean setCapacity(String strCapacity) {
        try {
            capacity = Integer.parseInt(strCapacity);
        } catch (NumberFormatException num) {
            return false;
        }
        return true;
    }

    public UClass(String type, String strCapacity,int voor_id) {
        this.voor_id = voor_id;
        if (!setdataInUnitimeFormat(type,strCapacity)) throw new IllegalArgumentException();
    }
    private UClass(){}

    public XMLBuilder toCourseOfferingXML() {
      //  return new XMLBuilder().element()
        return null;
    };
}
