Assumptions
1. Assuming only one train is there and it has two sections(A and B). Each section has 50 seats.
2. If the ticket not found, It will throw Exception
3. If the Seat is not available, It will throw Exception.

To run an application please follow the below steps,
`./gradlew build
./gradlew test
./gradlew bootRun`

To Create a new ticket - 
`
curl --location --request POST 'http://localhost:8080/v1/tickets/purchase' \
--header 'Content-Type: application/json' \
--data-raw '{
"from": "london",
"to": "europe",
"user": {
"firstName": "ar",
"lastName": "pr",
"email": "abcd@gmail.com"
},
"price": 51.0
}'
`

To list a ticket -
`
curl --location --request GET 'http://localhost:8080/v1/tickets/receipt/abc1@gmail.com'
`

To modify a section -
`
curl --location --request PUT 'http://localhost:8080/v1/tickets/modify/abc1@gmail.com?newSection=A5'
`

To view all users in a section -
`
curl --location --request GET 'http://localhost:8080/v1/tickets/users?section=A'
`

Task details:

Assessment Requirements:
Code must be published in Github with a link we can access (use public repo).
Code must compile with some effort on unit tests, doesn't have to be 100%, but it shouldn't be 0%.
Please code this using Java
Adding a persistence layer will be cumbersome, so just store the data in your current session/in memory.
If a requirement is ambiguous or unclear, use your best judgment to interpret it and make what you believe are reasonable assumptions
When the assignment is ready, send the console output from your app-server and app-client. You can also include your reasonable assumptions and known limitations.
App to be coded

I want to board a train from London to France. The train ticket will cost $5.

Create an API where you can submit a purchase for a ticket. Details included in the receipt are:
a) From, To, User , price paid.
(i) User should include first and last name, email address
The user is allocated a seat in the train. Assume the train has only 2 sections, section A and section B.
An API that shows the details of the receipt for the user
An API that lets you view the users and seat they are allocated by the requested section
An API to remove a user from the train
An API to modify a user's seat