const admin = require('firebase-admin');

// You must replace the require below with the path to your own service account file
const serviceAccount = require('../fcm-test-c8726-firebase-adminsdk-qfr7g-8c38ab32bd.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

const message = {
  notification: {
    title: '`$FooCorp` up 1.43% on the day',
    body: 'FooCorp gained 11.80 points to close at 835.67, up 1.43% on the day.'
  },
  data: {
    stock: 'GOOG',
    open: '829.62',
    close: '635.67',
    
  },
  android: {
    priority: 'high',
    notification: {
      icon: 'stock_ticker_update',
      color: '#7e55c3'
    }
  },
  topic: 'testFcm',
  
};

// Send a message to the device corresponding to the provided registration token.
admin.messaging().send(message)
  .then((response) => {
    // Response is a message ID string.
    console.log('Successfully sent message:', response);
  })
  .catch((error) => {
    console.log('Error sending message:', error);
  });
