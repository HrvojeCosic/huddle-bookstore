## Overview
- There are 3 endpoints, following a Controller-Service-Repository architecture with a reactive style. The non-blocking nature of the server is ensured by using BlockHound.
- A more interesting of the endpoints is the book purchasing endpoint, which is designed with modularity in mind, following various design patterns in the service layer.
- There are integration and unit tests covering basic functionality and different possible input cases.
- There is basic error handling for invalid requests.
- Project wasn't written with security in mind, hard-coding things like environment variables. There also isn't any customer authentication or authorization.

## Usage
1. Create the image with external dependencies using `sudo docker build -t huddle_bookstore_image .`
2. Run the container using `sudo docker run --name huddle_bookstore_container -p 5432:5432 -d huddle_bookstore_image`
3. Run the tests using `mvn test` OR the server using `mvn spring-boot:run`
5. Clean up the container using `sudo docker rm -f huddle_bookstore_container`
