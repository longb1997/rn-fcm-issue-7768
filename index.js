/**
 * @format
 */

import {AppRegistry, NativeModules} from 'react-native';
import MainApp from './src/MainApp';
import {name as appName} from './app.json';
import messaging from '@react-native-firebase/messaging';

const {BubbleModule} = NativeModules;

messaging().setBackgroundMessageHandler(async remoteMessage => {
    console.log('Message handled in the background!', remoteMessage);
    if (Platform.OS === 'android') {
        BubbleModule.showBubble()
      }
});

AppRegistry.registerComponent(appName, () => MainApp);
