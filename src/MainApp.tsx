import React, {useEffect} from 'react';
import {Button, NativeModules, Text, View} from 'react-native';
import messaging from '@react-native-firebase/messaging';
import {requestNotifications} from 'react-native-permissions';

const {BubbleModule} = NativeModules;

const MainApp = () => {


  const requestPermission = async () => {
    try {
      await requestNotifications(['alert', 'badge', 'sound']);
    } catch (e) {
      console.log('e ', e);
    }
    await messaging().requestPermission();
  }

  useEffect(() => {
    requestPermission()
    messaging()
      .subscribeToTopic('testFcm')
      .then(() => console.log('Subscribed to topic "testFcm"'));
  }, []);

  useEffect(() => {
    const unsubscribe = messaging().onMessage(async remoteMessage => {
      console.log("A new FCM message arrived MainApp!", remoteMessage)
    });

    const unsubscribeOnNotificationOpenedApp = messaging().onNotificationOpenedApp(message => {
      console.log("🚀 ~ unsubscribeOnNotificationOpenedApp in MainApp:", message)
      });

      messaging()
      .getInitialNotification()
      .then(value => {
        console.log("🚀 ~ getInitialNotification in MainApp:", value)
      });

    return () => {
        unsubscribe();
        unsubscribeOnNotificationOpenedApp()
    }
  }, []);


  return (
    <View style={{flex: 1, justifyContent: 'center', alignItems: 'center'}}>
      <Text>This is Main App</Text>

      <Button
        title="open Bubble setting"
        onPress={() => BubbleModule?.openBubblePermissionSettings()}
      />
    </View>
  );
};

export default MainApp;
