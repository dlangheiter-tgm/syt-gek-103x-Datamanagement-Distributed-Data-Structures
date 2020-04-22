import io.atomix.cluster.discovery.BootstrapDiscoveryProvider;
import io.atomix.core.Atomix;
import io.atomix.core.AtomixBuilder;
import io.atomix.core.profile.Profile;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomixMain {

    public static void main(String[] args) throws InterruptedException {
        AtomixBuilder builder = Atomix.builder();

        builder.withMemberId(StaticVars.masterId)
                .withAddress(StaticVars.masterAddress)
                .build();

        builder.withMembershipProvider(BootstrapDiscoveryProvider.builder()
                .withNodes(StaticVars.master, StaticVars.client1, StaticVars.client2)
                .build());

        builder.addProfile(Profile.dataGrid());

        Atomix atomix = builder.build();
        atomix.start().join();
        System.out.println("Cluster formed");

        AtomicInteger i = new AtomicInteger();
        while (true) {
            atomix.getEventService().send(StaticVars.topicFibonacci, i.get()).thenAccept(response -> {
                System.out.println("Fib " + i.getAndIncrement() + ": " + response);
            });
            Thread.sleep(1000);
        }
    }

}
