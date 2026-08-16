# Distributed File Sharing System (Java RMI)

A client–server file sharing system built on Java RMI, with token-based authentication, role separation and department-scoped storage.

**Author:** [Obadah AboEssa](https://www.linkedin.com/in/obadah-abo-essa) · **Stack:** Java · Java RMI · Swing

---

## Overview

A central **coordinator** exports a remote interface over RMI. Clients bind to it through the RMI registry and operate on a shared file store — but only on the department directory their account is entitled to, and only after exchanging credentials for a session token.

```
┌──────────────┐        RMI (port 1099)        ┌──────────────────────┐
│  ClientGUI   │ ─────────────────────────────▶│  CoordinatorImpl     │
│  (Swing)     │   login / upload / download   │  session tokens      │
└──────────────┘   list / delete / overwrite   │  role checks         │
                                               └──────────┬───────────┘
                                                          │
                                         ┌────────────────▼─────────────────┐
                                         │  data/development  data/graphic  │
                                         │  data/qa                         │
                                         └──────────────────────────────────┘
```

## Remote interface

```java
public interface CoordinatorInterface extends Remote {
    String       login(String username, String password);
    boolean      uploadFile(String token, String filename, byte[] data);
    byte[]       downloadFile(String token, String filename);
    List<String> listFiles(String token);
    boolean      deleteFile(String token, String filename);
    boolean      overwriteFile(String token, String filename, byte[] data);
}
```

Every operation except `login` takes a session token — the coordinator resolves it to a user, and rejects the call if that user's role or department doesn't permit the action.

## Roles

`MANAGER` and `EMPLOYEE`, defined in `common/Role.java`. Managers and employees see different capabilities over the same store.

## Department stores

Files are partitioned per department under `data/` — `development`, `graphic`, `qa` — so a client is scoped to its own directory rather than the whole filesystem.

## Layout

```
Main.java                     Starts the RMI registry on 1099 and binds the coordinator
coordinator/
  CoordinatorInterface.java   The exported Remote interface
  CoordinatorImpl.java        Sessions, role checks, file operations
client/
  Client.java                 RMI lookup and remote calls
  ClientGUI.java              Swing front end
common/
  User.java, Role.java        Shared model, serialized across the wire
data/                         Department-scoped file store
```

## Running

Requires JDK 8+.

```bash
# compile
javac -d out $(find . -name "*.java")

# start the coordinator (creates the registry on port 1099)
java -cp out Main

# in a second terminal, start a client
java -cp out client.ClientGUI
```

## Notes

Built as a university distributed systems project. It demonstrates RMI remote interfaces, stub/skeleton communication, serialization of shared model classes, and server-side session and authorization handling.

## License

MIT
