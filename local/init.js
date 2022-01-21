var db = connect('127.0.0.1/localSlot');

db.users.insert({
   'user_id' : 11111,
   'name': "Kunal Sample",
   'email': "krp@sample.com" });

db.refresh.insert({
   'user_id' : 11111,
   'token': "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMTExMSIsImlhdCI6MTY0MDMzNzcxNn0.DnBBwTC2z1fv6VItgFnt7kS5_KEwt0LmPxaW0VtAq7I"});

db.users.createIndex({user_id:1}, {unique: true});
