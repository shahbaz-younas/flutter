firebase.auth().onAuthStateChanged(function (user) {
  if (user) {
      // loads data
  } else {
    window.location.href = "login.html"; 
      
  }
});

// video search
var rootRefSearchVideo = firebase.database().ref('Search');

//get count video search
rootRefSearchVideo.on("value", function(snapshot) {
  document.getElementById('videosearchtext').innerHTML=snapshot.numChildren();
})
//

// guys search
var rootRefSearchVoice = firebase.database().ref('Search');

//get count guys search
rootRefSearchVoice.orderByChild("genderMeet").equalTo("guy").on("value", function(snapshot) {
  document.getElementById('guyssearchtext').innerHTML=snapshot.numChildren();
})

// girls search
var rootRefSearchVoice = firebase.database().ref('Search');

//get count girls search
rootRefSearchVoice.orderByChild("genderMeet").equalTo("girl").on("value", function(snapshot) {
  document.getElementById('girlssearchtext').innerHTML=snapshot.numChildren();
})

// chat
var rootRefMovies = firebase.database().ref('Chat');

//get count chat
rootRefMovies.on("value", function(snapshot) {
  document.getElementById('chatstext').innerHTML=snapshot.numChildren();
})
//

//Spam
var rootRefSeasons = firebase.database().ref('ReportAbuse');

//get count Spam
rootRefSeasons.on("value", function(snapshot) {
  document.getElementById('spamtext').innerHTML=snapshot.numChildren();
})


//Users
var rootRefUsers = firebase.database().ref('Users');

//get count Users
rootRefUsers.on("value", function(snapshot) {
  document.getElementById('userstext').innerHTML=snapshot.numChildren();
})


//search
var rootRefEpisodes = firebase.database().ref('Search');

//get count search
rootRefEpisodes.on("value", function(snapshot) {
  document.getElementById('searchtext').innerHTML=snapshot.numChildren();
})


//images_reviews
var rootRefSliders = firebase.database().ref('images_reviews');

//get count images_reviews
rootRefSliders.on("value", function(snapshot) {
  document.getElementById('images_reviewstext').innerHTML=snapshot.numChildren();
})

//images_reviews
var rootRefMessages = firebase.database().ref('messages_reviews');

//get count images_reviews
rootRefMessages.on("value", function(snapshot) {
  document.getElementById('messagestext').innerHTML=snapshot.numChildren();
})

