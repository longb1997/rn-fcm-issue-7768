This is mini-repo for reproduce bug from [this issue](https://github.com/invertase/react-native-firebase/issues/7768).

Run `yarn` && `yarn android` to build

GO TO REPO, DECOMPRESS `fcm-test-c8726-firebase-adminsdk-qfr7g-8c38ab32bd.json.zip` file (because Github Protect xD)

How to reproduce: 
Step 1: 
Open the app, accept notification permission

Step 2: Press "OPEN BUBBLE SETTING" button to open Bubble Settings => Select "All conversation can bubble"

Step 3: Now quit/background the MainApp, run command `yarn sendFcm`, new Bubble will be appear (in quit state, will take a little time to showing bubble)

Step 4: Press to Bubble, will launch an Bubble Screen

Step 5: Now hide (not close) the Bubble Screen, go to the MainApp

Step 6: Run command `yarn sendFcm` => check the log

Thanks for helping!
