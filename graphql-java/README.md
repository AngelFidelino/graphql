## Spring Graphql
Repository that hosts GraphQL + Java implementation.

### How to test with GraphiQL
It uses GraphiQL as a web based GraphiQL client
GraphiQL url - http://localhost:8080/graphiql


### How to test with Postman
URI = http://localhost:8080/graphql

**Mutation AddBook**
```json
mutation CreateBook(
    $bookName: String!
    $pages: Int! = 435
    $firstNameAuthor: String
    $lastNameAuthor: String
    $ageAuthor: Int) {
        createBook(
            bookName: $bookName
            pages: $pages
            category: COMEDY
            firstNameAuthor: $firstNameAuthor
            lastNameAuthor: $lastNameAuthor
            ageAuthor: $ageAuthor
        )
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
**Query**
```json
query GetBook {
  getBook(id: 1) {
      id
      name
      pages
      category
    }
}
```

```json
query GetBooks {
    getBooks {
        id
        name
        pages
        category
    }
}

```
