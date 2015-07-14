package Debug;

public class DebugString {
    private String log, name;

    DebugString(){
        this.log = "Log String";
        this.name = "LogName";
    }

    public void setLogData(String logString, String logName){
        this.log = logString;
        this.name = logName;
    }

    public String getLog(){
        return log;
    }
    public String getName(){
        return name;
    }
}
