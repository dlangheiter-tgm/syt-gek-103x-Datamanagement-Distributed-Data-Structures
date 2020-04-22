import io.atomix.cluster.Node;
import io.atomix.utils.net.Address;

public class StaticVars {
    
    public static String topicFibonacci = "fibonacci";
    
    public static String masterId = "master";
    public static String client1Id = "client1";
    public static String client2Id = "client2";

    public static Address masterAddress = new Address("localhost", 18000);
    public static Address client1Address = new Address("localhost", 18010);
    public static Address client2Address = new Address("localhost", 18020);
    
    public static Node master;
    public static Node client1;
    public static Node client2;
    
    static {
        master = Node.builder()
                .withId(masterId)
                .withAddress(masterAddress)
                .build();
        
        client1 = Node.builder()
                .withId(client1Id)
                .withAddress(client1Address)
                .build();

        client2 = Node.builder()
                .withId(client2Id)
                .withAddress(client2Address)
                .build();
    }

}
