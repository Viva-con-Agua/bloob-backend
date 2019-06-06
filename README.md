# bloob-backend

## email access rights db

### a record 
<pre>
EmailAccessRight(id: Long, roleName: String, crewName: String, email: String)
</pre>
### available REST calls
<pre>
GET     /all
return all records in db

POST    /create
expects a record (EmailAccessRight), the actual id will be given when inserting into the db

POST    /get
given an EmailARRequest(roleName: Array[String],crewName: String)
returns all email addresses available to the given role and crew

POST    /delete
given an id: Long
deletes the record with the matching id
</pre>