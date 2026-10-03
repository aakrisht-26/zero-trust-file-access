# zero-trust-file-access

Token-based file access over plain Java sockets. An auth server hands out short-lived signed tokens, and a file server checks one on every request before it opens the file. It only needs the JDK, no other dependencies.

## How it works

There are three programs.

`AuthServer` listens on port 5000. It takes a username and a password, and if they match it replies with a token.

`FileServer` is on port 6000 and expects a file name and a token. It keeps no sessions and never talks to the auth server. For each request it verifies the signature on the token with the public key, checks the expiry, and then looks for the file.

`Client` asks for a username and password, logs in, prints the token it got back, and then asks which file you want.

```
client                 auth server :5000          file server :6000
  |  LOGIN / user / pass     |                          |
  |------------------------->|                          |
  |  TOKEN / <token>         |                          |
  |<-------------------------|                          |
  |                                                     |
  |  REQUEST_FILE / name / <token>                      |
  |---------------------------------------------------->|
  |  OK / <contents>    or    DENIED / <reason>         |
  |<----------------------------------------------------|
```

Everything on the wire is plain text, one field per line. A failed login gets `ERROR` and a reason back instead of `TOKEN`.

### Tokens

A token is two Base64 strings with a dot between them:

```
base64(username|role|issuedAt|expiresAt|nonce).base64(signature)
```

The two times are milliseconds since the epoch, and a token is good for five minutes. The signature is SHA256withRSA over the `username|role|...` string, made with a 2048-bit key.

The key pair is generated the first time one of the servers needs it and saved under `keys/`. Those two files are in `.gitignore`, so every clone ends up with its own pair. To rotate, delete them and restart both servers. Tokens signed with the old key stop verifying.

### Passwords

The two users are hardcoded in `UserStore.java`:

```
alice / alice123    role USER
bob   / bob123      role ADMIN
```

When the auth server loads them, each password goes through PBKDF2 (HMAC-SHA256, 65536 iterations, a random 16-byte salt per user) and the user table only holds the salt and the hash.

## Running it

You need a JDK, version 8 or newer. Compile from the root of the repo:

```
javac authserver/*.java client/*.java fileserver/*.java shared/*.java
```

Then start each program in its own terminal. Run them from the root as well: the class names include the package, and the `files/` and `keys/` paths are relative to wherever the server was started.

```
java authserver.AuthServer
java fileserver.FileServer
java client.Client
```

A session looks like this:

```
Username: alice
Password: alice123

Login successful.
Access token received.

TOKEN (for demo):
YWxpY2V8VVNFUnwxNzkxMDUzMjg4ODkwfDE3OTEwNTM1ODg4OTB8...
Enter file name to access: secret.txt

File content:

This is a highly confidential file.
```

A wrong password ends with `Login failed. Exiting.` and a file that doesn't exist gives `Access denied: File not found`.

`client/` also has two test clients. `TestAuthClient` logs in as alice and prints the token. `TestFileClient` sends whatever token is pasted into its source. The one in there expired long ago, so right now all it gets back is `Access denied: Invalid or expired token`. Paste in a fresh token and recompile to see it go through.

## Layout

```
authserver/   AuthServer, TokenUtil (builds and verifies tokens), UserStore
fileserver/   FileServer
client/       Client and the two test clients
shared/       CryptoUtils (PBKDF2, RSA signing, nonces) and KeyManager
files/        what the file server serves
keys/         generated key pair, not committed
```

