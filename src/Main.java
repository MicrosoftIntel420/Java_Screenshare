import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        Node node;

        if(args.length == 5 && args[0].equals("-c") && args[1].equals("-a") && args[3].equals("-p")){
            node = createClient(args[2],stringToInt(args[4]));
        }
        else if(args.length == 3 && args[0].equals("-s") && args[1].equals("-p")){
            node = createServer(stringToInt(args[2]));
        }
        else{
            System.out.println("Wrong Input!");
            return;
        }
        try {
            assert node != null;
            node.run();
        }
        catch (InterruptedException | IOException e){
            e.printStackTrace();
        }
    }

    public static Client createClient(String address, int port){
        try{
            return new Client(address, port);
        }
        catch(IOException | AWTException e){
            e.printStackTrace();
            return null;
        }
    }

    public static Server createServer(int port){
        try{
            return new Server(port);
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int stringToInt(String stringNumber){
        int decimal = stringNumber.length()-1;
        int result = 0;
        for(char i : stringNumber.toCharArray()){
            result += (i - '0') * Math.pow(10, decimal--);
        }
        return  result;
    }

    public static String bytearrayToString(byte[] bytearray){
        StringBuilder result = new StringBuilder();
        int count = 0;
        while(count < bytearray.length && bytearray[count] != 0){
            result.append((char)bytearray[count++]);
        }
        return result.toString();
    }


    public static byte[] parseStringIntoBuffer(String from, int bufferSize){
        byte[] to = new byte[bufferSize];
        int count = 0;
        byte[] fromByte = from.getBytes(StandardCharsets.UTF_8);
        while(count < fromByte.length && count < to.length){
            to[count] = fromByte[count];
            count++;
        }
        return to;
    }
}
