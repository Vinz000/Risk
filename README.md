# Risk by Team7

Our implementation of the war/strategy game, Risk.

### Running our game

1. Clone the repository  
`$ git clone https://github.com/Vinz000/Risk.git`
2. Navigate to jar files  
`$ cd Risk/out/artifacts/Team7_Sprint_4`
4. Run  
`$ java -Dprism.order=sw -jar ./Team7.jar`

> **Note**: MacOS users should leave out the JVM option  
> **Warning**: Java version must be 8 to run the jar file

### Tools

- Java 8
- JavaFX

### JVM Options

1. `-Dprism.order=sw`

### Trello Board
Access here: https://trello.com/b/8cQ5i8zq

### Test Plan
First, we manually tested by running the bot against itself. This helped us find many small bugs. 
Following our manual testing, we created unit tests for our helper methods. If the bot behaviour changes
in any way, this should result in a failure of (at least) one of the unit tests.

<hr />   

> **Note**: Programmed bot to be fun.

### Bot
[<img src="https://nerdist.com/wp-content/uploads/2020/07/maxresdefault.jpg" alt="Our bot" />](a "Our bot")

###### File structure
There are three files related to our bot:

1. The bot class itself: `Team7.java`
2. A class full of helper functions that the bot uses: `Team7HelperFunctions.java`
3. Test class: `Team7Test.java`

These three classes have been zipped into `Team7Bot.zip` which is in [`out/artifacts/Team7_Sprint_5`](https://github.com/UCD-COMP20050/Team7/tree/build/sprint-five/out/artifacts/Team7_Sprint_5).

###### Strategy
We put together a [Google Doc](https://docs.google.com/document/d/1V_Xft5sh-KlkORygbpsXae7RZDSRUVW2e5sFA8_75_o/edit?usp=sharing) which details the strategy of our bot - we simply implemented this logic in code.
