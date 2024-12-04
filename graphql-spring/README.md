## Spring Graphql
Repository that hosts Spring for GraphQL implementation.

### How to test with GraphiQL
It uses GraphiQL as a web based GraphiQL client
GraphiQL url - http://localhost:8080/graphiql
Set headers
```json
{
  "user_id":"XX",
  "password":"XX",
  "user_roles":"XX"
}
```

### How to test with Postman
URI = http://localhost:8080/graphql

Set headers
```json
{
  "user_id":"XX",
  "password":"XX",
  "user_roles":"XX"
}
```

**Mutation AddBook**
```json
mutation AddBook(
	$bookName: String! 
	$pages: NonNegativeInt! = 435
	$firstNameAuthor: String
	$lastNameAuthor: String
	$ageAuthor: NonNegativeInt
) {
	addBook(
		bookName: $bookName
		pages: $pages
		category: COMEDY
		author: {
			firstNameAuthor: $firstNameAuthor
			lastNameAuthor: $lastNameAuthor
			ageAuthor: $ageAuthor
			}
	) {
		id
		name
	}
}
```

**Variables AddBook**
```json
{
	"bookName": "Pride and Prejudice",
	"firstNameAuthor": "Jane",
	"lastNameAuthor": "Austen",
	"ageAuthor": 45
}
```

**Mutation AddAuthor**

```json
mutation AddAuthor(
	$firstNameAuthor: String
	$lastNameAuthor: String
	$ageAuthor: NonNegativeInt
) {
	addAuthor(
		author: {
			firstNameAuthor: $firstNameAuthor
			lastNameAuthor: $lastNameAuthor
			ageAuthor: $ageAuthor
			}
		) 
	{
		id
		firstName
		lastName
		age
	}
}
```

**Variables AddAuthor**
```json
{
	"firstNameAuthor": "Jane",
	"lastNameAuthor": "Austen",
	"ageAuthor": 45
}
```

**Query**
```json
query getBookById {
	getBookById(id: 1) {
	id
	name
	pages
	publishedAt
	category
	author {
		id
		firstName
		lastName
		age
		}
	}
}
```

**Subscription**
Note: Subscriptions uses WebSocket (no http headers) thus we need to implement additional strategies to protect those endpoints
Any time a book entity is added an event is emitted to a topic and clients can subscribe it by calling:
```json
subscription NotifyNewBooks {
    notifyNewBooks {
          id
          name
          pages
          publishedAt
          category
          author {
              id
              firstName
              lastName
              age
            }
    }
}
```
