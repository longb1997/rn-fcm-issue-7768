Run `yarn` && `yarn android` to build

How to reproduce: 

Step 1: 
Open the app, allow notification permission

Step 2: Press "OPEN BUBBLE SETTING" button to open Bubble Settings => Select "All conversation can bubble"

Step 3: Now quit/background the MainApp, run command `yarn sendFcm`, new Bubble will appear (in quit state, will take a little time to showing bubble)

Step 4: Press to Bubble, which will launch an Bubble Screen

Step 5: Now hide (don't close) the Bubble Screen, go to the MainApp

Step 6: Run command `yarn sendFcm` => check the log

Thanks for helping!