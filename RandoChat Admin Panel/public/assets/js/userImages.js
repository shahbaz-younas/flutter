firebase.auth().onAuthStateChanged(function (user) {
    if (user) {
        // loads data
    } else {
      window.location.href = "login.html"; 
        
    }
  });
 
  // get current url and idgenre
var params = getUrlParams(window.location.href);

// function for get id 
function getUrlParams(url) 
{
  var params = {};
  (url + '?').split('?')[1].split('&').forEach(
    function (pair) 
    {
       pair = (pair + '=').split('=').map(decodeURIComponent);
       if (pair[0].length) 
       {
         params[pair[0]] = pair[1];
       }
  });

  return params;
};

  
var dbUsersReviewsImages = firebase.database().ref('images_reviews');
var dbUsers = firebase.database().ref('Users');

dbUsersReviewsImages.orderByChild("time").limitToFirst(6).once('value', function(snapshot) {
    snapshot.forEach(function(childSnapshot) {
      var childKey = childSnapshot.key;
      var childData = childSnapshot.val();
        designItems(childData,childKey);
    });
  });

function designItems(values,childKey){
    if(values.image!="none"){
      
    document.getElementById("rowImagesUser").innerHTML+=`
                         <div class="col-md-3 col-sm-6 col-xs-6">           
             <img src="${values.image}" style="height: 250px;width: 200px; margin-top:10px;">
                  
             <div class="icon-bar" style="margin-top:10px;">
             <button class="btn btn-warning"  onclick="onclickDeletImageUser('${childKey}');return false;"><i class="fa fa-trash-alt" style="margin-right:5px;"></i>Delete</button>
            
             </div>
              </div>
                      `
    }
}

function onclickDeletImageUser(childKeyk){
    

    dbUsersReviewsImages.orderByChild("userID").equalTo(params.iduser).once('value', function(snapshot) {
        snapshot.forEach(function(childSnapshot) {
          var childKey = childSnapshot.key;
          var childdata = childSnapshot.val();

          dbUsers.child(childdata.userID).update({
            image : "default"
        
        }, function(error) {
            if (error) {
                // The write failed...
                alert("Deleted data failed");
            } else {
                // Data saved successfully!
                alert("Deleted data successfully");
                location.reload();
            }
        });

         
      });

      dbUsersReviewsImages.child(childKeyk).remove()
          .then(function() {
            alert("Remove succeeded.");
            location.reload();
          })
          .catch(function(error) {
            alert("Remove failed: " + error.message);
          });
        });
        
}

        
    
