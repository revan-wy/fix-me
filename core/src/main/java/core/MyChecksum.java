package core;

public class MyChecksum {

    public static String myChecksum(StringBuilder buffer) { 
        int checksum = 0; 
        for (int i = 0; i < buffer.length(); i++) { 
            checksum += buffer.charAt(i); 
        } 
        return String.valueOf(checksum % 256); 
    } 

}
