package distributed.systems.starterapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class StarterappApplication {
	/*  Backend of a TODO application, with a REST API and a database connection. The frontend is implemented in React and can be found in the "frontend" folder.
		Basic implementation to interact with TODO items, with the following endpoints:
		- GET /todos: Get all TODO items
		- POST /todos: Create a new TODO item
		- GET /todos/{id}: Get a TODO item by ID
		- PUT /todos/{id}: Update a TODO item by ID
		- DELETE /todos/{id}: Delete a TODO item by ID
		Start with "intuitive" implementation withouth knowing about REST, then refactor to a more RESTful implementation.
	*/
	public static void main(String[] args) {
		SpringApplication.run(StarterappApplication.class, args);
	}

	@GetMapping("/hello")
	public String hello() {
		return "Hello, HSE26!";
	}


}
