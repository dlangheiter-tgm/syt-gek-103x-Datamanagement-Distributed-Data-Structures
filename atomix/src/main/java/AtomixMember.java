import io.atomix.cluster.discovery.BootstrapDiscoveryProvider;
import io.atomix.core.Atomix;
import io.atomix.core.AtomixBuilder;
import io.atomix.core.profile.Profile;

import java.util.concurrent.CompletableFuture;

public class AtomixMember {

    public static void main(String[] args) {
        int client = args.length > 0 ? Integer.parseInt(args[0]) : 1;
        
        AtomixBuilder builder = Atomix.builder();

        if(client == 1) {
            builder.withMemberId(StaticVars.client1Id)
                    .withAddress(StaticVars.client1Address)
                    .build();

            builder.withMembershipProvider(
                    BootstrapDiscoveryProvider.builder()
                            .withNodes(StaticVars.master, StaticVars.client1)
                            .build());
        } else if (client == 2) {
            builder.withMemberId(StaticVars.client2Id)
                    .withAddress(StaticVars.client2Address)
                    .build();

            builder.withMembershipProvider(
                    BootstrapDiscoveryProvider.builder()
                            .withNodes(StaticVars.master, StaticVars.client2)
                            .build());
        } else {
            throw new RuntimeException("Could not start client with id " + client);
        }

        builder.addProfile(Profile.dataGrid());

        Atomix atomix = builder.build();
        
        atomix.getEventService().subscribe(
                StaticVars.topicFibonacci,
                message -> {
                    System.out.println("Client" + client + ": " + message);
                    return CompletableFuture.completedFuture(fibonacci((Integer) message));
                }
        );

        atomix.start().join();
        System.out.println("All members joined");
    }

    public static long fibonacci(int n) {
        if (n <= 1) return n;
        else return fibonacci(n - 1) + fibonacci(n - 2);
    }

}
