package Debug;

import java.util.HashMap;

public class Debug {
    HashMap<String, DebugString> logs;

    public Debug() {
        this.logs = new HashMap<String, DebugString>();
    }

    public void setLog(String id, String str){
        if(!logs.containsKey(id)){
            logs.put(id, new DebugString());
        }
        logs.get(id).setLogData(str, id);
    }

    public String getLog(String id){
        String result;

        if(logs.containsKey(id)){
            result = logs.get(id).getName() + ": " + logs.get(id).getLog();
        }else{
            result = "Нет такого лога.";
        }
        return result;
    }

    public HashMap<String, DebugString> getLogs(){
        return logs;
    }
}
