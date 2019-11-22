package broker;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import core.messages.MessageSellOrBuy;

public class BrokerLogic {
    int validation;

    BrokerLogic() {
    }

    public void brokerLoop() throws IOException {
        MessageSellOrBuy message;
        Scanner scan = new Scanner(System.in);
        String command;
        int marketId = 0;
        int validation = 0;
        while (validation == 0) {
            printMenue();
            command = scan.nextLine();
            switch (command) {
            case "1":
                System.out.println("Please input a market ID:");
                try{marketId = scan.nextInt();}catch(Exception ex){
                    System.out.println("Invalid Market ID");
                    System.out.println("Press Any Key To Continue...");
                    System.in.read();
                    continue;}
                message = new MessageSellOrBuy("Buy Message", "BUY", (marketId), 1212, randInstrument(), randQuantity(), randPrice());
                System.out.println("Buy command signaled -> ["+marketId+"]");
                System.out.println(message.toString());
                System.out.println("Press Any Key To Continue...");
                try{System.in.read();}catch(Exception ex){ex.printStackTrace();}
                break;
            case "2":
                System.out.println("Please input a market ID:");
                try{marketId = scan.nextInt();}catch(Exception ex){
                    System.out.println("Invalid Market ID");
                    System.out.println("Press Any Key To Continue...");
                    System.in.read();
                    continue;}
                message = new MessageSellOrBuy("Sell Message", "SELL", (marketId), 1212, randInstrument(), randQuantity(), randPrice());
                System.out.println("Sell command signaled to market -> ["+marketId+"]");
                System.out.println(message.toString());
                System.out.println("Press Any Key To Continue...");
                try{System.in.read();}catch(Exception ex){ex.printStackTrace();}
                break;
            case "3":
                validation = 1;
                System.out.println("Exit command signaled");
                System.out.println("Press Any Key To Continue...");
                scan.nextLine();
                break;
            }
            // Write and Flush goes here
            System.out.println("Write and Flush goes here");
        }
        System.out.println("Broker Loop Exited");
        scan.close();
    }

    private static void printMenue() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println("    ______ _                  __  __        ____            _    ");
        System.out.println("   |  ____(_)                |  \\/  |      |  _ \\          | |");
        System.out.println("   | |__   ___  ___  ______  | \\  / | ___  | |_) |_ __ ___ | | _____ _ __ ");
        System.out.println("   |  __| | \\ \\/ /  |______| | |\\/| |/ _ \\ |  _ <| '__/ _ \\| |/ / _ \\ '__|");
        System.out.println("   | |    | |>  <            | |  | |  __/ | |_) | | | (_) |   <  __/ |   ");
        System.out.println("   |_|    |_/_/\\_\\           |_|  |_|\\___| |____/|_|  \\___/|_|\\_\\___|_|");
        System.out.println("   ________________________________________________________________________");
        System.out.println("  | Welcome to the Fix-Me Broker.                                          |");
        System.out.println("  | The following commands are available, please use numbers 1, 2 or 3.    |");
        System.out.println("  |------------------------------------------------------------------------|");
        System.out.println("  |  1. | BUY a comodity from the market.                                  |");
        System.out.println("  |  2. | SELL a comodity to the market.                                   |");
        System.out.println("  |  3. | EXIT the Fix-Me Broker.                                          |");
        System.out.println("  |________________________________________________________________________|");
    }

    private static int randPrice(){
        Random rand = new Random();
        return(rand.nextInt(100));
    }

    private static String randInstrument(){
        String[] arr = {"GOLD", "OIl", "AVOCADOS", "DIAMONDS", "COFFEE"};
        Random rand = new Random();
        return(arr[rand.nextInt(4)]);
    }
    private static int randQuantity(){
        Random rand = new Random();
        return(rand.nextInt(100));
    }
}

//  ______ _                  __  __        ____            _             
// |  ____(_)                |  \/  |      |  _ \          | |            
// | |__   ___  ___  ______  | \  / | ___  | |_) |_ __ ___ | | _____ _ __ 
// |  __| | \ \/ /  |______| | |\/| |/ _ \ |  _ <| '__/ _ \| |/ / _ \ '__| 
// | |    | |>  <            | |  | |  __/ | |_) | | | (_) |   <  __/ |   
// |_|    |_/_/\_\           |_|  |_|\___| |____/|_|  \___/|_|\_\___|_|   
                                                                  
                                                           
                                                                 