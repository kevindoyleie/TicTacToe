# Tic Tac Toe

A secured web app to play Tic Tac Toe against a dummy computer opponent, forked from https://github.com/randomvlad/TicTacToe.git and modified to provide some small interview coding challenges.

## Features & Notes
* Play a game on a 3x3 board with an option to go first or after the computer opponent.
* Computer opponent's AI chooses random squares, except when going first in which case the center tile is always picked.
* User game data is persisted to an in-memory database. As long as the server is not restarted, a player can leave and return to finish an in-progress game.  
* App is secured with a username & password login. Database is seeded with one username `horatio` with password `hertz`.

## Tech Stack
| | Technology |
|---|---|
| __Language__ | Java 11 |
| __Framework__ | Spring Boot (v2.5) |
| __Data Layer__ | H2 Database, JPA & Hibernate | 
| __UI Layer__ | HTML, CSS, Javascript, jQuery (v3.6), [Bootstrap](https://getbootstrap.com/) (v5), [Thymeleaf](http://www.thymeleaf.org/) |
| __Testing__ | JUnit 5, Mockito, AssertJ |
| __Build Tool__ | Gradle (v7.2) |


# Challenge tasks

We want to do a few things with this repo, which is forked from a working tic tac toe Spring Boot application.

## 1. Getting familiar with the application

### 1.1 Run via the gradle wrapper
Run the application from a terminal using `./gradlew bootRun` and navigate to the home page [http://localhost:8080/tictactoe/](http://localhost:8080/tictactoe/).

### 1.2 Log in
Use the username "horatio" and password "hertz".

### 1.3 Try to play a game
It will fail as some code isn't yet written. We'll fix that later.

### 1.4 Stop the application
Just stop the application from running.

## 2. Add a new user

This repo defines a default user that you just logged in with named "horatio".

Please add an additional user using the same pattern.

_Tip: Simple implementation is fine, copy the implementation that added Horatio._

## 3. Fix the run.sh script

There is a `run.sh` and `test.sh` script in the top directory of the repo.

### 3.1 Fix the script
It isn't working for some reason when we call `./run.sh` from a normal terminal, but is working when we call `bash run.sh` can you fix it?

_Hint: Is there a problem with the file permissions?_

## 4. Fix the code & pass the tests

The run script is fixed, but it is obvious that there are failing tests.

_Tip: The tests themselves are correct and don't need to change._

_Tip: Read the javadoc and the tests._

### 4.1 Implement the BoardUtil.getAllLines(...) method
Run the BoardUtilTest tests, they should fail

The getAllLines method is not complete, once complete the BoardUtilTest tests will pass.

### 4.2 Implement the GameService.evaluateGameState(...) method
Run the GameServiceTest tests, they should fail

This method is needed for the GameServiceTest to pass.

_Evaluate the state of the board, has anyone won? Is it a draw? Is it in progress?_

## 5. Play the working game
Do the steps from step 1 again, the game is working and you can play against the computer.

Play a few rounds and see if you can win, lose, draw. Is the computer playing well?

## 6. Improve the Computer players algorithm

The computer makes random moves against the player as it stands. That results in a poor opponent.

### 6.1 Enable the Computer player tests

In `ComputerPlayerServiceTest` enable the commented out tests, run them, some should fail.

### 6.2 The computer tries to block

The computer should try to play a move that blocks the player if they place two moves in a row.

e.g. in the below game, if O goes next, they should not randomly pick, but should block X.

```
[X, X, _]
[O, _, _]
[O, _, _]
```

Update the computer players algorithm to prioritise blocking 2 in a row.

### 6.3 The computer tries to win

The above improvement tries to block the other player winning, but what if we could win instead?

e.g. In the below game, if O goes next, because of the previous improvment it might try to block X, but it could win outright by making 3 in a row.

```
[X, X, _]
[O, O, _]
[_, _, _]
```

Update the computer players algorithm to prioritize winning over blocking.

## 7 Play again

Try to beat the computer now!

## 8 (Optional) Are there any refactors or other improvements you could suggest?

No need to implement, just identify some improvements you might suggest.
