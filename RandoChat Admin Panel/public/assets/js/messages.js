
firebase.auth().onAuthStateChanged(function (user) {
    if (user) {
        // loads data
    } else {
      window.location.href = "login.html"; 
        
    }
  });


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


// get current url and idgenre
var params = getUrlParams(window.location.href);
var idfrom = params.idfrom;
var idto = params.idto;

var dbMessages = firebase.database().ref('Chat/'+params.idfrom+'/'+params.idto);
var dbusers = firebase.database().ref('Users');

dbMessages.orderByChild("message").once('value', function(snapshot) {
    snapshot.forEach(function(childSnapshot) {
      var childKey = childSnapshot.key;
      var childData = childSnapshot.val();
        designItems(childData,childKey);
    });
  });
  
  function designItems(values,childKey){

    if(values.from==params.idfrom){
      
      dbusers.child(params.idfrom).once('value', function(snapshot) {
          var childDatauser = snapshot.val();

          document.getElementById("chatbox").innerHTML+=`
          <li class="left clearfix">
      
          <div class="chat-body">                                        
                  <strong>${childDatauser.username}</strong>
                                                    
              <p>
              ${values.message}
              </p>
          </div>
      </li>`    
      });

    
    }
    if(values.from==params.idto){

         
      dbusers.child(params.idto).once('value', function(snapshot) {
        var childDatauser = snapshot.val();

        document.getElementById("chatbox").innerHTML+=`
        <li class="right clearfix">
                                   
                                    <div class="chat-body clearfix">
                                      
                                            
                                            <strong class="pull-right">${childDatauser.username}</strong>
                                       
                                        <p>
                                        ${values.message}
                                        </p>
                                    </div>
                                </li>`    
    });

    }
    
  }

