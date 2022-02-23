firebase.auth().onAuthStateChanged(function (user) {
    if (user) {
        // loads data
    } else {
      window.location.href = "login.html"; 
        
    }
  });
  var keyuselast;
  var timelast;
  var dbUsers = firebase.database().ref('Users');
  
  var dbSearchSearch = firebase.database().ref('Search');
  var dbSearch = firebase.database().ref('Search')
          .orderByChild("time")
          .limitToFirst(15);
  
        
  
  dbSearch.once('value', function(snapshot) {
      snapshot.forEach(function(childSnapshot) {
        var childKey = childSnapshot.key;
        var childData = childSnapshot.val();
        
         console.log(childKey);

          designItemsSearch(childData,childKey);
      });
    });
  
  function designItemsSearch(values,idSearch){
  
  var date = new Date(values.time*-1);
  
  dbUsers.child(idSearch).once('value', function(snapshot) {
      var childDataUser = snapshot.val();
      
        document.getElementById("tbodys_search").innerHTML+=`<tr>
        <td>
        <button style="margin-top:10px;" class="btn btn-danger" onclick="onclickDeleteSearch('${idSearch}');return false;"><i class="fa fa-trash-alt"></i></button>
        <a href="editUser.html?iduser=${idSearch}"><button style="margin-top:10px;" class="btn btn-primary"><i class="fa fa-edit"></i></button></a>
        
        </td>
        <td><img src="${childDataUser.image}" class="user-image img-responsive" style="width:64px;height:64px;margin: 0;"/>
        <td>${childDataUser.username}</td>
        <td>${childDataUser.sex}</td>
        <td>${values.gender}</td>    
        <td>${date.toLocaleString()}</td>
        </td>
        </tr>`   

  });
    

    
    keyuselast= idSearch;
    timelast= values.time;
  
  }
  
  function onclickSearchSearch(){
  
      var seartext = document.getElementById("textSearchUser").value;
      if(seartext==""){
        alert("Search text is empty!!!!");
      }else{
       document.getElementById("tbodys_search").innerHTML="";
  
        dbSearchSearch.orderByKey().on("child_added", function(snapshot) {
              
          var childKey = snapshot.key;
          var childData = snapshot.val();
        
            if(seartext==childKey){
              designItemsSearch(childData,childKey);
            }  
          });
      }
    
  }
  
  function onclickNext15Search(){
  
    
       document.getElementById("tbodys_search").innerHTML=``;  
   
       var next = firebase.database().ref('Search')
         .orderByChild("time").limitToFirst(15).startAt(timelast,keyuselast);
  
         next.once('value', function(snapshot) {
           snapshot.forEach(function(childSnapshot) {
             var childKey = childSnapshot.key;
             var childData = childSnapshot.val();
          
               designItemsSearch(childData,childKey);
           });
         });
  
  }
  
  
  function onclickLastest50Search(){
  
    document.getElementById("tbodys_search").innerHTML=``;  
  
    var next50 = firebase.database().ref('Search')
      .orderByChild("time").limitToFirst(50);
  
      next50.once('value', function(snapshot) {
        snapshot.forEach(function(childSnapshot) {
          var childKey = childSnapshot.key;
          var childData = childSnapshot.val();
  
          designItemsSearch(childData,childKey);
        });
      });
  
  }
  
  function onclickFirst50Search(){
  
    document.getElementById("tbodys_search").innerHTML=``;  
  
    var first50 = firebase.database().ref('Search')
      .orderByChild("time").limitToLast(50);
  
      first50.once('value', function(snapshot) {
        snapshot.forEach(function(childSnapshot) {
          var childKey = childSnapshot.key;
          var childData = childSnapshot.val();
  
          designItemsSearch(childData,childKey);
        });
      });
  
  }
  
  
  function onclickDeleteSearch(keyuser){
    dbSearchSearch.child(keyuser).remove()
      .then(function() {
        alert("Remove succeeded.");
        location.reload();
      })
      .catch(function(error) {
        alert("Remove failed: " + error.message);
      });
  }
  
  
  
  
  
  
  