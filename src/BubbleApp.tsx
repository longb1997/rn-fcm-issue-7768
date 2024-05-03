import messaging from '@react-native-firebase/messaging';
import React, { useEffect } from 'react'
import { Text, View } from 'react-native'

const BubbleApp = () => {

    useEffect(() => {
        const unsubscribe = messaging().onMessage(async remoteMessage => {
          console.log("A new FCM message arrived Bubble!", remoteMessage)
        });
    
        const unsubscribeOnNotificationOpenedApp = messaging().onNotificationOpenedApp(message => {
          console.log("🚀 ~ unsubscribeOnNotificationOpenedApp in Bubble:", message)
          });
    
          messaging()
          .getInitialNotification()
          .then(value => {
            console.log("🚀 ~ getInitialNotification in Bubble:", value)
          });
    
        return () => {
            unsubscribe();
            unsubscribeOnNotificationOpenedApp()
        }
      }, []);

    return (
        <View style={{flex: 1, justifyContent: 'center', alignItems:'center'}}>
            <Text>This is Bubble App</Text>
        </View>
    )
}

export default BubbleApp;