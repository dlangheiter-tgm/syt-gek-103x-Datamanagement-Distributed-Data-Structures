import io.atomix.cluster.Node;
import io.atomix.cluster.discovery.BootstrapDiscoveryProvider;
import io.atomix.core.Atomix;
import io.atomix.core.AtomixBuilder;
import io.atomix.core.profile.Profile;
import io.atomix.utils.net.Address;

import java.util.ArrayList;
import java.util.List;

public class AtomixMain {

    public static void main(String[] args) {
        AtomixBuilder builder = Atomix.builder();

        List<Address> addresses = new ArrayList<>();
        addresses.add(new Address("localhost", 18000));
        addresses.add(new Address("localhost", 18001));
        addresses.add(new Address("localhost", 18002));

        builder.withMemberId("member1")
                .withAddress(addresses.get(0))
                .build();

        builder.withMembershipProvider(BootstrapDiscoveryProvider.builder()
                .withNodes(
                        Node.builder()
                                .withId("member1")
                                .withAddress(addresses.get(1))
                                .build(),
                        Node.builder()
                                .withId("member2")
                                .withAddress(addresses.get(2))
                                .build())
                .build());

        builder.addProfile(Profile.dataGrid());

        Atomix atomix = builder.build();
        atomix.start().join();

    }

}
