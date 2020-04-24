# Datamanagement "Distributed Data Structures"

## Einführung
Komplexe und aufteilbare Tasks müssen mit Parametern ausgestattet werden und von
entsprechenden Koordinatoren gestartet bzw. die erhaltenen Daten wieder zusammengefasst
werden. Diese Art von verteilter Programmierung findet in vielen Anwendungsgebieten rege
Verwendung (AI Daten Analyse, Lastverteilung, etc.). Hierbei kommt das Prinzip des
Master/Worker Patterns (Master-Slave oder Map-Reduce Pattern) zum Einsatz.

## Ziele
Finden Sie eine Lösung, die in einer Cloud-Umgebung ausrollbar (deployable) ist. Die einzelnen
Worker sollen unabhängig voneinander bestehen können und mit Input-Parametern gestartet
werden. Die berechneten Daten müssen an den Master bzw. als Zwischenberechnung an andere
Worker weitergegeben werden können. Die einzelnen Worker sollen unabhängig von der
Umgebung gestartet werden können (unterschiedliche Servereinheiten).

## Vorausetzung
* Grundverständnis von Python oder Java
* Lesen und Umsetzen von APIs
* Fähigkeit komplexe Programmier-Aufgaben zu implementieren und zu verteilen

## Vergleich

## Implementierung
### Atmoix

Um eine Node zu definieren braucht man eine Adresse und eine ID.

```java
String masterId = "master";
Address masterAddress = new Address("localhost", 18000);
Node master = Node.builder()
    .withId(masterId)
    .withAddress(masterAddress)
    .build();
```

Um eine Atomix Instanz zu konfigurieren braucht man einen `AtomixBuilder`

```java
AtomixBuilder builder = Atomix.builder();
```

Diesen kann man dann konfigurieren mit der ID und der Adresse

```java
builder.withMemberId(StaticVars.masterId)
    .withAddress(StaticVars.masterAddress)
    .build();
```

Danach definiert man welche anderen Nodes für den Cluster gebraucht werden

```java
builder.withMembershipProvider(BootstrapDiscoveryProvider.builder()
    .withNodes(StaticVars.master, StaticVars.client1, StaticVars.client2)
    .build());
```

Warten bis der Cluster geformt wurde:

```java
Atomix atomix = builder.build();
    atomix.start().join();
```

Auf dem Master frage ich jede Sekunde nach der Nächst höheren Fibonacci Nummer.
Ein `AtomicInteger` wird verwendet um ihn in der Lambda Ausdruck zu verwenden.
Im `send` Befehl geben wir ein Topic und einen Wert. Der Wert gibt an welche Fibonacci Nummer berechnet werden soll. 

```java
AtomicInteger i = new AtomicInteger();
while (true) {
    atomix.getEventService().send(StaticVars.topicFibonacci, i.get()).thenAccept(response -> {
        System.out.println("Fib " + i.getAndIncrement() + ": " + response);
    });
    Thread.sleep(1000);
}
```

Der Client meldet sich an anfragen über diese Topic zu bearbeiten.

```java
atomix.getEventService().subscribe(
    StaticVars.topicFibonacci,
    message -> {
        System.out.println("Client" + client + ": " + message);
        return CompletableFuture.completedFuture(fibonacci((Integer) message));
    }
);
```

### Ergebnisse

Master

```
Fib 0: 0
Fib 1: 1
Fib 2: 1
Fib 3: 2
Fib 4: 3
Fib 5: 5
Fib 6: 8
Fib 7: 13
Fib 8: 21
Fib 9: 34
Fib 10: 55
Fib 11: 89
```

Client 1:

```
Client1: 0
Client1: 1
Client1: 2
Client1: 4
Client1: 6
Client1: 8
Client1: 10
```

Client 2:

```
Client2: 3
Client2: 5
Client2: 7
Client2: 9
Client2: 11
```

Hier kann man sehen das eine Aufgabe eine Fibonacci Nummer zu finden einem Client zugeteilt wird. Man sieht außerdem, dass der zweite Client am Anfang noch nicht gestartet war und deswegen die ersten drei Aufgaben an Client 1 gesendet wurden.