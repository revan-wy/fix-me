package broker;

import core.Client;

public class Broker {
    public static void main(String[] args) {
        Client client = new Client(Client.Type.BROKER);
        Thread clientThread = new Thread(client);
        clientThread.start();
        try {
            clientThread.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
	}
}

// TODO format