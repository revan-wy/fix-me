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

// TODO fix client / router cross importing
// TODO globally change message, order, request, response names
// TODO follow flow of setchecksum, reading and writing checksum in decoders and encoders
// TODO extractify
// TODO rethink project structure
// TODO format